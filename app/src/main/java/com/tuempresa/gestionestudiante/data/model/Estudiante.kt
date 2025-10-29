package com.tuempresa.gestionestudiante.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos que representa un estudiante.
 *
 * Analogía: Esta clase es como un formulario en blanco que podemos llenar
 * con información de un estudiante. Cada propiedad es un campo del formulario.
 *
 * @SerializedName: Le dice a Gson cómo mapear los campos del JSON
 * (por si los nombres en la API son diferentes a los de Kotlin)
 */
data class Estudiante(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("nombre")
    val nombre: String = "",

    @SerializedName("edad")
    val edad: Int = 0,

    @SerializedName("carrera")
    val carrera: String = "",

    @SerializedName("promedio")
    val promedio: Double = 0.0,

    @SerializedName("fecha_registro")
    val fechaRegistro: String = ""
)

/**
 * Modelo para crear/actualizar estudiante (sin ID ni fecha)
 */
data class EstudianteRequest(
    val nombre: String,
    val edad: Int,
    val carrera: String,
    val promedio: Double
)