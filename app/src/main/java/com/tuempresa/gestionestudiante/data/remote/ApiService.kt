package com.tuempresa.gestionestudiante.data.remote

import com.tuempresa.gestionestudiante.data.model.Estudiante
import com.tuempresa.gestionestudiante.data.model.EstudianteRequest
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz que define todos los endpoints de nuestra API.
 *
 * Analogía: Esta interfaz es como un menú de restaurante.
 * Lista todas las opciones disponibles (endpoints) y qué necesitas
 * proporcionar para cada una (parámetros).
 *
 * Retrofit se encarga automáticamente de:
 * - Construir las URLs
 * - Hacer las peticiones HTTP
 * - Convertir JSON a objetos Kotlin
 */
interface ApiService {

    /**
     * Obtiene todos los estudiantes
     * GET http://tu-servidor:8000/estudiantes/
     */
    @GET("estudiantes/")
    suspend fun obtenerEstudiantes(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<Estudiante>>

    /**
     * Obtiene un estudiante específico por ID
     * GET http://tu-servidor:8000/estudiantes/{id}
     */
    @GET("estudiantes/{id}")
    suspend fun obtenerEstudiante(
        @Path("id") id: Int
    ): Response<Estudiante>

    /**
     * Crea un nuevo estudiante
     * POST http://tu-servidor:8000/estudiantes/
     */
    @POST("estudiantes/")
    suspend fun crearEstudiante(
        @Body estudiante: EstudianteRequest
    ): Response<Estudiante>

    /**
     * Actualiza un estudiante existente
     * PUT http://tu-servidor:8000/estudiantes/{id}
     */
    @PUT("estudiantes/{id}")
    suspend fun actualizarEstudiante(
        @Path("id") id: Int,
        @Body estudiante: EstudianteRequest
    ): Response<Estudiante>

    /**
     * Elimina un estudiante
     * DELETE http://tu-servidor:8000/estudiantes/{id}
     */
    @DELETE("estudiantes/{id}")
    suspend fun eliminarEstudiante(
        @Path("id") id: Int
    ): Response<Unit>
}