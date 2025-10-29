from fastapi import FastAPI, HTTPException, status 
from fastapi.middleware.cors import CORSMiddleware 
from typing import List 
from models import EstudianteCreate, EstudianteUpdate, EstudianteResponse 
from database import get_db_connection 
 
# Creamos la aplicación FastAPI 
app = FastAPI( 
    title="API de Gestión de Estudiantes", 
    description="CRUD completo para gestionar estudiantes", 
    version="1.0.0" 
) 
 
# Configuramos CORS (permite que la app Android se conecte) 
# Es como poner "Se permite la entrada" en la puerta 
app.add_middleware( 
    CORSMiddleware, 
    allow_origins=["*"],  # En producción, especifica dominios exactos 
    allow_credentials=True, 
    allow_methods=["*"], 
    allow_headers=["*"], 
) 
 
# ========== CREATE (Crear) ========== 
@app.post("/estudiantes/", response_model=EstudianteResponse, 
status_code=status.HTTP_201_CREATED) 
async def crear_estudiante(estudiante: EstudianteCreate): 
    """ 
    Crea un nuevo estudiante en la base de datos. 
    Es como agregar un nuevo expediente al archivero. 
    """ 
    with get_db_connection() as conn: 
        cursor = conn.cursor() 
         
        query = """ 
            INSERT INTO estudiantes (nombre, edad, carrera, promedio) 
            VALUES (%s, %s, %s, %s) 
            RETURNING * 
        """ 
         
        cursor.execute(query, ( 
            estudiante.nombre, 
            estudiante.edad, 
            estudiante.carrera, 
            estudiante.promedio 
        )) 
         
        nuevo_estudiante = cursor.fetchone() 
         
        if not nuevo_estudiante: 
            raise HTTPException( 
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, 
                detail="Error al crear el estudiante" 
            ) 
         
        return nuevo_estudiante 
 
# ========== READ (Leer) ========== 
@app.get("/estudiantes/", response_model=List[EstudianteResponse]) 
async def obtener_estudiantes(skip: int = 0, limit: int = 100): 
    """ 
    Obtiene la lista de todos los estudiantes. 
    Es como pedir ver el catálogo completo. 
     
    Parámetros: 
    - skip: cuántos registros saltar (para paginación) 
    - limit: máximo de registros a devolver 
    """ 
    with get_db_connection() as conn: 
        cursor = conn.cursor() 
         
        query = """ 
            SELECT * FROM estudiantes 
            ORDER BY id 
            LIMIT %s OFFSET %s 
        """ 
         
        cursor.execute(query, (limit, skip)) 
        estudiantes = cursor.fetchall() 
         
        return estudiantes 
 
@app.get("/estudiantes/{estudiante_id}", response_model=EstudianteResponse) 
async def obtener_estudiante(estudiante_id: int): 
    """ 
    Obtiene un estudiante específico por su ID. 
    Es como buscar un expediente específico por número. 
    """ 
    with get_db_connection() as conn: 
        cursor = conn.cursor() 
         
        query = "SELECT * FROM estudiantes WHERE id = %s" 
        cursor.execute(query, (estudiante_id,)) 
         
        estudiante = cursor.fetchone() 
         
        if not estudiante: 
            raise HTTPException( 
                status_code=status.HTTP_404_NOT_FOUND, 
                detail=f"Estudiante con ID {estudiante_id} no encontrado" 
            ) 
         
        return estudiante 
 
# ========== UPDATE (Actualizar) ========== 
@app.put("/estudiantes/{estudiante_id}", response_model=EstudianteResponse) 
async def actualizar_estudiante(estudiante_id: int, estudiante: EstudianteUpdate): 
    """ 
    Actualiza la información de un estudiante. 
    Es como editar los datos en un expediente existente. 
    """ 
    with get_db_connection() as conn: 
        cursor = conn.cursor() 
         
        # Primero verificamos que el estudiante existe 
        cursor.execute("SELECT * FROM estudiantes WHERE id = %s", (estudiante_id,)) 
        estudiante_existente = cursor.fetchone() 
         
        if not estudiante_existente: 
            raise HTTPException( 
                status_code=status.HTTP_404_NOT_FOUND, 
                detail=f"Estudiante con ID {estudiante_id} no encontrado" 
            ) 
         
        # Construimos la query dinámicamente solo con campos que se actualizarán 
        campos_actualizar = [] 
        valores = [] 
         
        if estudiante.nombre is not None: 
            campos_actualizar.append("nombre = %s") 
            valores.append(estudiante.nombre) 
         
        if estudiante.edad is not None: 
            campos_actualizar.append("edad = %s") 
            valores.append(estudiante.edad) 
         
        if estudiante.carrera is not None: 
            campos_actualizar.append("carrera = %s") 
            valores.append(estudiante.carrera) 
         
        if estudiante.promedio is not None: 
            campos_actualizar.append("promedio = %s") 
            valores.append(estudiante.promedio) 
         
        if not campos_actualizar: 
            # Si no hay nada que actualizar, devolvemos el estudiante sin cambios 
            return estudiante_existente 
         
        valores.append(estudiante_id) 
        query = f""" 
            UPDATE estudiantes  
            SET {', '.join(campos_actualizar)} 
            WHERE id = %s 
            RETURNING * 
        """ 
         
        cursor.execute(query, valores) 
        estudiante_actualizado = cursor.fetchone() 
         
        return estudiante_actualizado 
 
# ========== DELETE (Eliminar) ========== 
@app.delete("/estudiantes/{estudiante_id}", 
status_code=status.HTTP_204_NO_CONTENT) 
async def eliminar_estudiante(estudiante_id: int): 
    """ 
    Elimina un estudiante de la base de datos. 
    Es como sacar un expediente del archivero permanentemente. 
    """ 
    with get_db_connection() as conn: 
        cursor = conn.cursor() 
         
        # Verificamos que existe 
        cursor.execute("SELECT * FROM estudiantes WHERE id = %s", (estudiante_id,)) 
        estudiante = cursor.fetchone() 
         
        if not estudiante: 
            raise HTTPException( 
                status_code=status.HTTP_404_NOT_FOUND, 
                detail=f"Estudiante con ID {estudiante_id} no encontrado" 
            ) 
         
        # Eliminamos 
        cursor.execute("DELETE FROM estudiantes WHERE id = %s", (estudiante_id,)) 
         
        return None  # 204 No Content no devuelve cuerpo 
 
# ========== Endpoint de prueba ========== 
@app.get("/") 
async def root(): 
    """Endpoint raíz para verificar que la API funciona""" 
    return { 
        "mensaje": "API de Gestión de Estudiantes", 
        "version": "1.0.0", 
        "endpoints": { 
            "crear": "POST /estudiantes/", 
            "listar": "GET /estudiantes/", 
            "obtener": "GET /estudiantes/{id}", 
            "actualizar": "PUT /estudiantes/{id}", 
            "eliminar": "DELETE /estudiantes/{id}" 
        } 
    } 