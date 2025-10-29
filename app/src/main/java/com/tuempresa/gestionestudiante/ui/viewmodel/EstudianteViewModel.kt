package com.tuempresa.gestionestudiante.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuempresa.gestionestudiante.data.model.Estudiante
import com.tuempresa.gestionestudiante.data.model.EstudianteRequest
import com.tuempresa.gestionestudiante.data.repository.EstudianteRepository
import kotlinx.coroutines.launch
/**
 * ViewModel que maneja la lógica de negocio y el estado de la UI.
 *
 * Analogía: El ViewModel es como el "cerebro" de la aplicación.
 * - Recibe las acciones del usuario (clicks, inputs)
 * - Decide qué hacer con esas acciones
 * - Actualiza el estado que la UI observa
 * - Se comunica con el Repository para obtener/guardar datos
 *
 * Ventajas del ViewModel:
 * - Sobrevive a cambios de configuración (rotación de pantalla)
 * - Separa la lógica de la UI
 * - Facilita testing
 */
class EstudianteViewModel : ViewModel() {
    private val repository = EstudianteRepository()
    // ========== ESTADOS ==========
// Estados privados que solo el ViewModel puede modificar
    private val _estudiantes = mutableStateOf<List<Estudiante>>(emptyList())
    private val _estudianteSeleccionado = mutableStateOf<Estudiante?>(null)
    private val _isLoading = mutableStateOf(false)
    private val _error = mutableStateOf<String?>(null)
    private val _operacionExitosa = mutableStateOf(false)

    // Estados públicos que la UI puede observar (solo lectura)
    val estudiantes: State<List<Estudiante>> = _estudiantes
    val estudianteSeleccionado: State<Estudiante?> = _estudianteSeleccionado
    val isLoading: State<Boolean> = _isLoading
    val error: State<String?> = _error
    val operacionExitosa: State<Boolean> = _operacionExitosa

    /**
     * Inicialización del ViewModel
     * Se ejecuta automáticamente al crear la instancia
     */
    init {
        cargarEstudiantes()
    }

    // ========== OPERACIONES CRUD ==========

    /**
     * Carga todos los estudiantes de la API
     *
     * Flujo:
     * 1. Activa indicador de carga
     * 2. Hace petición a la API (vía Repository)
     * 3. Si es exitosa: actualiza la lista
     * 4. Si falla: muestra error
     * 5. Desactiva indicador de carga
     */
    fun cargarEstudiantes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.obtenerEstudiantes()
                .onSuccess { lista ->
                    _estudiantes.value = lista
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar estudiantes"
                }

            _isLoading.value = false
        }
    }

    /**
     * Carga un estudiante específico por ID
     * Útil para ver detalles o para cargar datos al editar
     */
    fun cargarEstudiante(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.obtenerEstudiante(id)
                .onSuccess { estudiante ->
                    _estudianteSeleccionado.value = estudiante
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al cargar estudiante"
                }

            _isLoading.value = false
        }
    }

    /**
     * Crea un nuevo estudiante
     *
     * @param nombre Nombre completo
     * @param edad Edad en años
     * @param carrera Carrera que estudia
     * @param promedio Promedio académico
     */
    fun crearEstudiante(
        nombre: String,
        edad: Int,
        carrera: String,
        promedio: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _operacionExitosa.value = false

            val nuevoEstudiante = EstudianteRequest(
                nombre = nombre,
                edad = edad,
                carrera = carrera,
                promedio = promedio
            )

            repository.crearEstudiante(nuevoEstudiante)
                .onSuccess {
                    _operacionExitosa.value = true
                    cargarEstudiantes()  // Recargamos la lista
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al crear estudiante"
                }

            _isLoading.value = false
        }
    }

    /**
     * Actualiza un estudiante existente
     * Similar a crear, pero requiere el ID
     */
    fun actualizarEstudiante(
        id: Int,
        nombre: String,
        edad: Int,
        carrera: String,
        promedio: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _operacionExitosa.value = false

            val estudianteActualizado = EstudianteRequest(
                nombre = nombre,
                edad = edad,
                carrera = carrera,
                promedio = promedio
            )

            repository.actualizarEstudiante(id, estudianteActualizado)
                .onSuccess {
                    _operacionExitosa.value = true
                    cargarEstudiantes()  // Recargamos la lista
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al actualizar estudiante"
                }

            _isLoading.value = false
        }
    }

    /**
     * Elimina un estudiante
     *
     * @param id ID del estudiante a eliminar
     */
    fun eliminarEstudiante(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.eliminarEstudiante(id)
                .onSuccess {
                    cargarEstudiantes()  // Recargamos la lista
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Error al eliminar estudiante"
                }

            _isLoading.value = false
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Limpia el mensaje de error
     * Útil después de mostrar el error al usuario
     */
    fun limpiarError() {
        _error.value = null
    }

    /**
     * Resetea el flag de operación exitosa
     * Útil después de navegar o mostrar confirmación
     */
    fun resetearOperacionExitosa() {
        _operacionExitosa.value = false
    }

    /**
     * Limpia el estudiante seleccionado
     */
    fun limpiarEstudianteSeleccionado() {
        _estudianteSeleccionado.value = null
    }
}