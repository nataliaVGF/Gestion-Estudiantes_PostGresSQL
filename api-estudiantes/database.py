import psycopg2 
from psycopg2.extras import RealDictCursor 
from contextlib import contextmanager 

DB_CONFIG = { 
    'host': 'localhost',      # Dónde está la base de datos 
    'database': 'escuela_db', # Nombre de la base de datos 
    'user': 'postgres',       # Usuario 
    'password': '123' # Contraseña 
}

@contextmanager 
def get_db_connection(): 
    """ 
    Esta función es como un pase temporal a la biblioteca. 
    Te da acceso, haces tu trabajo, y luego cierra la puerta automáticamente. 
    """ 
    conn = psycopg2.connect(**DB_CONFIG, cursor_factory=RealDictCursor) 
    try: 
        yield conn  # "yield" es como prestar algo temporalmente 
        conn.commit()  # Guardamos los cambios 
    except Exception as e: 
        conn.rollback()  # Si algo sale mal, deshacemos todo 
        raise e 
    finally: 
        conn.close()  # Siempre cerramos la conexión