from pydantic import BaseModel, Field 
from typing import Optional 
from datetime import datetime 
 
class EstudianteBase(BaseModel): 
    """ 
    Este es el 'molde' básico de un estudiante. 
    Pydantic valida automáticamente que los datos sean correctos. 
    Es como un formulario con reglas estrictas. 
    """ 
    nombre: str = Field(..., min_length=1, max_length=100,  
                        description="Nombre completo del estudiante") 
    edad: int = Field(..., ge=15, le=100,  
                      description="Edad del estudiante (entre 15 y 100)") 
    carrera: str = Field(..., max_length=100) 
    promedio: float = Field(..., ge=0, le=100,  
                           description="Promedio académico (0-100)") 
 
class EstudianteCreate(EstudianteBase): 
    """Para crear un estudiante nuevo""" 
    pass 
 
class EstudianteUpdate(BaseModel): 
    """Para actualizar - todos los campos son opcionales""" 
    nombre: Optional[str] = Field(None, min_length=1, max_length=100) 
    edad: Optional[int] = Field(None, ge=15, le=100) 
    carrera: Optional[str] = Field(None, max_length=100) 
    promedio: Optional[float] = Field(None, ge=0, le=100) 
 
class EstudianteResponse(EstudianteBase): 
    """Lo que devolvemos al cliente""" 
    id: int 
    fecha_registro: datetime 
     
    class Config: 
        from_attributes = True  # Permite crear desde objetos de BD