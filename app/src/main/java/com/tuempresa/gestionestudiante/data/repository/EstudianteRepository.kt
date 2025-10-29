package com.tuempresa.gestionestudiante.data.repository



import com.tuempresa.gestionestudiante.data.model.Estudiante
import com.tuempresa.gestionestudiante.data.model.EstudianteRequest
import com.tuempresa.gestionestudiante.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio que maneja todas las operaciones de datos de estudiantes.
 *
 * Analogía: El repositorio es como un "asistente personal" que sabe
 * exactamente cómo obtener, guardar y modificar información.
 * El ViewModel le pide cosas, y el repositorio se encarga de hacerlas.
 *
 * Beneficios del patrón Repository:
 * - Separa la lógica de datos de la UI
 * - Facilita testing (podemos crear repositorios falsos para pruebas)
 * - Centraliza el manejo de errores
 */
class EstudianteRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Obtiene la lista de todos los estudiantes
     *
     * @return Result con lista de estudiantes o excepción en caso de error
     */
    suspend fun obtenerEstudiantes(): Result<List<Estudiante>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerEstudiantes()

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene un estudiante específico por ID
     */
    suspend fun obtenerEstudiante(id: Int): Result<Estudiante> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerEstudiante(id)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Estudiante no encontrado"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Crea un nuevo estudiante
     */
    suspend fun crearEstudiante(estudiante: EstudianteRequest): Result<Estudiante> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.crearEstudiante(estudiante)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al crear estudiante"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Actualiza un estudiante existente
     */
    suspend fun actualizarEstudiante(id: Int, estudiante: EstudianteRequest):
            Result<Estudiante> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.actualizarEstudiante(id, estudiante)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar estudiante"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Elimina un estudiante
     */
    suspend fun eliminarEstudiante(id: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.eliminarEstudiante(id)

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al eliminar estudiante"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}