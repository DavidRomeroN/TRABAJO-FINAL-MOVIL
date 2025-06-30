package pe.edu.upeu.granturismojpc.ui.presentation.screens.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.IOException
import pe.edu.upeu.granturismojpc.model.UsuarioDto
import pe.edu.upeu.granturismojpc.model.UsuarioRegisterDto
import pe.edu.upeu.granturismojpc.repository.UsuarioRepository
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepo: UsuarioRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRegistered = MutableLiveData(false)
    val isRegistered: LiveData<Boolean> get() = _isRegistered

    val isError = MutableLiveData(false)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun registerUser(user: UsuarioRegisterDto) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                _isRegistered.postValue(false)

                val response = userRepo.registerUsuario(user)
                delay(1000L)

                if (response.isSuccessful && response.body() != null) {
                    _isRegistered.postValue(true)
                } else {
                    isError.postValue(true)
                    _errorMessage.postValue("Registro fallido: Verifique los datos ingresados")
                }
            } catch (e: SocketTimeoutException) {
                isError.postValue(true)
                _errorMessage.postValue("Tiempo de espera agotado. Verifique su conexión.")
            } catch (e: IOException) {
                isError.postValue(true)
                _errorMessage.postValue("Error de red: ${e.localizedMessage}")
            } catch (e: Exception) {
                isError.postValue(true)
                _errorMessage.postValue("Error inesperado: ${e.localizedMessage}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.postValue(null)
        isError.postValue(false)
        _isLoading.postValue(false)
    }
}
