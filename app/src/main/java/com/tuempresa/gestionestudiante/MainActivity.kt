package com.tuempresa.gestionestudiante

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tuempresa.gestionestudiante.ui.screens.DetalleEstudianteScreen
import com.tuempresa.gestionestudiante.ui.screens.FormularioScreen
import com.tuempresa.gestionestudiante.ui.screens.ListaEstudiantesScreen
import com.tuempresa.gestionestudiante.ui.theme.GestionEstudiantesTheme
import com.tuempresa.gestionestudiante.ui.viewmodel.EstudianteViewModel
import kotlinx.coroutines.launch

/**
 * Activity principal de la aplicación.
 *
 * Analogía: La MainActivity es como el "edificio" de la aplicación.
 * Dentro de este edificio hay diferentes "habitaciones" (pantallas)
 * conectadas por "pasillos" (navegación).
 */
class MainActivity : ComponentActivity() {

    // ViewModel compartido entre todas las pantallas
    private val viewModel: EstudianteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GestionEstudiantesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

/**
 * Sistema de navegación de la aplicación.
 *
 * Analogía: Es como un mapa del edificio que indica cómo ir
 * de una habitación (pantalla) a otra.
 *
 * Rutas disponibles:
 * - "lista" → Pantalla principal con lista de estudiantes
 * - "detalle/{id}" → Pantalla de detalle de un estudiante
 * - "nuevo" → Formulario para crear estudiante
 * - "editar/{id}" → Formulario para editar estudiante
 */
@Composable
fun AppNavigation(viewModel: EstudianteViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Observamos los estados del ViewModel
    val estudiantes by viewModel.estudiantes
    val estudianteSeleccionado by viewModel.estudianteSeleccionado
    val isLoading by viewModel.isLoading
    val error by viewModel.error
    val operacionExitosa by viewModel.operacionExitosa

    // Efecto para mostrar errores
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = it,
                    actionLabel = "Cerrar"
                )
                viewModel.limpiarError()
            }
        }
    }

    // Efecto para navegar después de operación exitosa
    LaunchedEffect(operacionExitosa) {
        if (operacionExitosa) {
            navController.popBackStack()
            viewModel.resetearOperacionExitosa()
        }
    }

    // Definición de rutas
    NavHost(
        navController = navController,
        startDestination = "lista"  // Pantalla inicial
    ) {
        // ========== RUTA: Lista de estudiantes ==========
        composable("lista") {
            ListaEstudiantesScreen(
                estudiantes = estudiantes,
                isLoading = isLoading,
                error = error,
                onEstudianteClick = { id ->
                    navController.navigate("detalle/$id")
                },
                onAgregarClick = {
                    viewModel.limpiarEstudianteSeleccionado()
                    navController.navigate("nuevo")
                },
                onEliminarClick = { id ->
                    viewModel.eliminarEstudiante(id)
                },
                onRecargarClick = {
                    viewModel.cargarEstudiantes()
                }
            )
        }

        // ========== RUTA: Detalle de estudiante ==========
        composable(
            route = "detalle/{estudianteId}",
            arguments = listOf(
                navArgument("estudianteId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val estudianteId = backStackEntry.arguments?.getInt("estudianteId") ?: 0

            // Cargar el estudiante cuando entramos a la pantalla
            LaunchedEffect(estudianteId) {
                viewModel.cargarEstudiante(estudianteId)
            }

            DetalleEstudianteScreen(
                estudiante = estudianteSeleccionado,
                isLoading = isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onEditarClick = { id ->
                    navController.navigate("editar/$id")
                }
            )
        }

        // ========== RUTA: Nuevo estudiante ==========
        composable("nuevo") {
            FormularioScreen(
                estudiante = null,  // Sin datos previos
                isLoading = isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onGuardarClick = { nombre, edad, carrera, promedio ->
                    viewModel.crearEstudiante(nombre, edad, carrera, promedio)
                }
            )
        }

        // ========== RUTA: Editar estudiante ==========
        composable(
            route = "editar/{estudianteId}",
            arguments = listOf(
                navArgument("estudianteId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val estudianteId = backStackEntry.arguments?.getInt("estudianteId") ?: 0

            // Cargar el estudiante para editar
            LaunchedEffect(estudianteId) {
                viewModel.cargarEstudiante(estudianteId)
            }

            FormularioScreen(
                estudiante = estudianteSeleccionado,  // Con datos previos
                isLoading = isLoading,
                onBackClick = {
                    navController.popBackStack()
                },
                onGuardarClick = { nombre, edad, carrera, promedio ->
                    viewModel.actualizarEstudiante(
                        estudianteId,
                        nombre,
                        edad,
                        carrera,
                        promedio
                    )
                }
            )
        }
    }

    // Host para mostrar Snackbars (notificaciones)
    SnackbarHost(hostState = snackbarHostState)
}