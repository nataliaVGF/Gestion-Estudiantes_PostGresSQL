package com.tuempresa.gestionestudiante.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Objeto singleton que configura y proporciona el cliente Retrofit.
 *
 * Analogía: Este objeto es como el "centro de comunicaciones" de la app.
 * Configura cómo se hacen las llamadas a la API (timeouts, logging, etc.)
 * y proporciona el servicio listo para usar.
 */
object RetrofitClient {

    // IMPORTANTE: Cambia esta URL por la de tu servidor
    // Si estás en un emulador, usa 10.0.2.2 en lugar de localhost
    // Si estás en un dispositivo físico, usa la IP de tu computadora
    private const val BASE_URL = "http://10.31.0.71:8000/"

    /**
     * Interceptor para logging (útil para debugging)
     * Registra todas las peticiones y respuestas en Logcat
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente HTTP con configuraciones personalizadas
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)  // Agregar logging
        .connectTimeout(30, TimeUnit.SECONDS)  // Timeout de conexión
        .readTimeout(30, TimeUnit.SECONDS)     // Timeout de lectura
        .writeTimeout(30, TimeUnit.SECONDS)    // Timeout de escritura
        .build()

    /**
     * Instancia de Retrofit configurada
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())  // Convierte JSON
            .build()
    }

    /**
     * Servicio API listo para usar
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}