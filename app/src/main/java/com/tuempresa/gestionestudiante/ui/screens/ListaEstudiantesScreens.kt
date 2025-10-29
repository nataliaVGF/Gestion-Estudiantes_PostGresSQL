package com.tuempresa.gestionestudiante.ui.screens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tuempresa.gestionestudiante.data.model.Estudiante
/**
 * Pantalla principal que muestra la lista de estudiantes.
 *
 * Analogía: Esta pantalla es como un tablero de anuncios donde
 * se muestran todas las fichas de estudiantes. Puedes ver la lista
 * completa, agregar nuevos, o seleccionar uno para ver más detalles.
 *
 * Composables en Jetpack Compose:
 * - Son funciones que describen la UI
 * - Se recomponen automáticamente cuando el estado cambia
 * - Son declarativas (describes QUÉ quieres, no CÓMO hacerlo)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEstudiantesScreen(
    estudiantes: List<Estudiante>,
    isLoading: Boolean,
    error: String?,
    onEstudianteClick: (Int) -> Unit,      // Callback cuando se hace click
    onAgregarClick: () -> Unit,             // Callback para agregar
    onEliminarClick: (Int) -> Unit,         // Callback para eliminar
    onRecargarClick: () -> Unit             // Callback para recargar
) {
    // Scaffold es como el "marco" de la pantalla (barra superior, FAB, contenido)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Estudiantes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // FAB (Floating Action Button) para agregar estudiantes
            FloatingActionButton(
                onClick = onAgregarClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar estudiante"
                )
            }
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // Estado: Cargando
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Estado: Error
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = onRecargarClick) {
                            Text("Reintentar")
                        }
                    }
                }

                // Estado: Lista vacía
                estudiantes.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No hay estudiantes registrados",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = onAgregarClick) {
                            Text("Agregar primero")
                        }
                    }
                }

                // Estado: Lista con datos
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = estudiantes,
                            key = { it.id }  // Key única para optimización
                        ) { estudiante ->
                            EstudianteCard(
                                estudiante = estudiante,
                                onClick = { onEstudianteClick(estudiante.id) },
                                onEliminarClick = { onEliminarClick(estudiante.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun EstudianteCard(
    estudiante: Estudiante,
    onClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    // Estado local para el diálogo de confirmación
    var mostrarDialogo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Información del estudiante
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = estudiante.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = estudiante.carrera,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = "Edad: ${estudiante.edad}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Promedio: ${String.format("%.2f", estudiante.promedio)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            estudiante.promedio >= 90 -> MaterialTheme.colorScheme.primary
                            estudiante.promedio >= 70 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }

            // Botón de eliminar
            IconButton(onClick = { mostrarDialogo = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar a ${estudiante.nombre}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminarClick()
                        mostrarDialogo = false
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}