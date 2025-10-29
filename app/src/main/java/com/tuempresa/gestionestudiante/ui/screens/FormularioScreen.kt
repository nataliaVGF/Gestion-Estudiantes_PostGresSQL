package com.tuempresa.gestionestudiante.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tuempresa.gestionestudiante.data.model.Estudiante

/**
 * Pantalla con formulario para crear o editar estudiantes.
 *
 * Analogía: Es como una hoja de inscripción que puedes llenar
 * para registrar un nuevo estudiante o modificar datos de uno existente.
 *
 * Esta pantalla maneja dos modos:
 * - Crear: Todos los campos vacíos
 * - Editar: Campos pre-llenados con datos existentes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    estudiante: Estudiante? = null,  // null = crear, con datos = editar
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onGuardarClick: (String, Int, String, Double) -> Unit,
) {
    // Estados locales para los campos del formulario
    var nombre by remember { mutableStateOf(estudiante?.nombre ?: "") }
    var edad by remember { mutableStateOf(estudiante?.edad?.toString() ?: "") }
    var carrera by remember { mutableStateOf(estudiante?.carrera ?: "") }
    var promedio by remember {
        mutableStateOf(
            estudiante?.promedio?.let { String.format("%.2f", it) } ?: ""
        )
    }

    // Estados de validación
    var nombreError by remember { mutableStateOf(false) }
    var edadError by remember { mutableStateOf(false) }
    var carreraError by remember { mutableStateOf(false) }
    var promedioError by remember { mutableStateOf(false) }

    val esEdicion = estudiante != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esEdicion) "Editar Estudiante" else "Nuevo Estudiante") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campo: Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = it.isBlank()
                },
                label = { Text("Nombre Completo *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError,
                supportingText = {
                    if (nombreError) {
                        Text("El nombre es obligatorio")
                    }
                },
                singleLine = true,
                enabled = !isLoading
            )

            // Campo: Edad
            OutlinedTextField(
                value = edad,
                onValueChange = {
                    edad = it
                    edadError = it.toIntOrNull() == null ||
                            (it.toIntOrNull() ?: 0) < 15 ||
                            (it.toIntOrNull() ?: 0) > 100
                },
                label = { Text("Edad *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = edadError,
                supportingText = {
                    if (edadError) {
                        Text("Edad inválida (15-100)")
                    }
                },
                singleLine = true,
                enabled = !isLoading
            )

            // Campo: Carrera
            OutlinedTextField(
                value = carrera,
                onValueChange = {
                    carrera = it
                    carreraError = it.isBlank()
                },
                label = { Text("Carrera *") },
                modifier = Modifier.fillMaxWidth(),
                isError = carreraError,
                supportingText = {
                    if (carreraError) {
                        Text("La carrera es obligatoria")
                    }
                },
                singleLine = true,
                enabled = !isLoading
            )

            // Campo: Promedio
            OutlinedTextField(
                value = promedio,
                onValueChange = {
                    promedio = it
                    promedioError = it.toDoubleOrNull() == null ||
                            (it.toDoubleOrNull() ?: 0.0) < 0 ||
                            (it.toDoubleOrNull() ?: 0.0) > 100
                },
                label = { Text("Promedio Académico *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = promedioError,
                supportingText = {
                    if (promedioError) {
                        Text("Promedio inválido (0-100)")
                    } else {
                        Text("Usa punto como separador decimal (ej: 85.5)")
                    }
                },
                singleLine = true,
                enabled = !isLoading
            )


            // Botón de guardar
            Spacer(modifier = Modifier.weight(1f))

            // Botón de guardar
            Button(
                onClick = {
                    // Validación final antes de guardar
                    val edadInt = edad.toIntOrNull()
                    val promedioDouble = promedio.toDoubleOrNull()

                    if (
                        nombre.isNotBlank() &&
                        edadInt != null && edadInt in 15..100 &&
                        carrera.isNotBlank() &&
                        promedioDouble != null && promedioDouble in 0.0..100.0
                    ) {
                        // Si todos los campos son válidos, guardar
                        onGuardarClick(nombre, edadInt, carrera, promedioDouble)
                    } else {
                        // Activar errores si hay campos inválidos
                        nombreError = nombre.isBlank()
                        edadError = edadInt == null || edadInt !in 15..100
                        carreraError = carrera.isBlank()
                        promedioError = promedioDouble == null || promedioDouble !in 0.0..100.0
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (esEdicion) "Actualizar" else "Guardar")
                }
            }
        }
    }
}