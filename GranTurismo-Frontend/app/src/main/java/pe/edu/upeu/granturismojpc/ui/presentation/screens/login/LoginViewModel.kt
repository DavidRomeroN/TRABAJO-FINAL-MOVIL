package pe.edu.upeu.granturismojpc.ui.presentation.screens.login

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
import pe.edu.upeu.granturismojpc.model.UsuarioResp
import pe.edu.upeu.granturismojpc.repository.PreferenceRepository
import pe.edu.upeu.granturismojpc.repository.UsuarioRepository
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.SincronizacionControl

import pe.edu.upeu.granturismojpc.utils.TokenUtils

import java.net.SocketTimeoutException

import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UsuarioRepository,
    private val confRepo: PreferenceRepository
) : ViewModel(){
    private val _isLoading: MutableLiveData<Boolean> by lazy {MutableLiveData<Boolean>(false)}
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _islogin: MutableLiveData<Boolean> by lazy {MutableLiveData<Boolean>(false)}
    val islogin: LiveData<Boolean> get() = _islogin

    val isError=MutableLiveData<Boolean>(false)
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    val listUser = MutableLiveData<UsuarioResp>()

    fun loginSys(toData: UsuarioDto) {
        Log.i("LOGIN", toData.email)
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
        try {
            _islogin.postValue(false)
            val totek=userRepo.loginUsuario(toData).body()
            delay(1500L)
            SincronizacionControl.paquetesSincronizados=false
            TokenUtils.TOKEN_CONTENT="Bearer "+totek?.token
            TokenUtils.USER_LOGIN= ""+totek?.email
            TokenUtils.USER_ID= totek?.idUsuario!!
            TokenUtils.USER_ROLE= totek.role
            Log.i("DATAXDMP", "Holas")
            listUser.postValue(totek!!)
            Log.i("DATAXDMP", TokenUtils.TOKEN_CONTENT)
            Log.i("DATAXDMP", TokenUtils.USER_LOGIN)
            Log.i("DATAXDMP", TokenUtils.USER_ID.toString())
            Log.i("DATAXDMP", TokenUtils.USER_ROLE.toString())
            val tieneConfig = confRepo.checkMyPreferencesExist()
            Log.i("CONFIGDATA", tieneConfig.getOrNull().toString())
            if(tieneConfig.getOrNull() == true){
                Log.i("CONFIGDATA", "Configuración encontrada")
                val conf=confRepo.getMyPreferences()
                TokenUtils.TEMP_LANGUAJE= conf.getOrNull()?.preferredLanguageCode.toString()
                TokenUtils.TEMP_CURRENCY= conf.getOrNull()?.preferredCurrencyCode.toString()
                Log.i("CONFIGDATA", "Idioma: ${conf.getOrNull()?.preferredLanguageCode.toString()}")
                Log.i("CONFIGDATA", "Moneda: ${conf.getOrNull()?.preferredCurrencyCode.toString()}")
            }else{
                Log.i("CONFIGDATA", "Configuración no encontrada")
                Log.i("CONFIGDATA", "Método pa crear nueva config")
                try{
                    val resp = confRepo.createMyDefaultPreferences()
                    Log.i("CONFIGDATA", "Idioma: ${resp.getOrNull()?.preferredLanguageCode.toString()}")
                    Log.i("CONFIGDATA", "Moneda: ${resp.getOrNull()?.preferredCurrencyCode.toString()}")
                    val respo = confRepo.updateMyLanguage(TokenUtils.TEMP_LANGUAJE)
                    Log.i("CONFIGDATA", "Idioma2: ${respo.getOrNull()?.preferredLanguageCode.toString()}")
                    Log.i("CONFIGDATA", "Moneda2: ${respo.getOrNull()?.preferredCurrencyCode.toString()}")
                    TokenUtils.TEMP_CURRENCY= respo.getOrNull()?.preferredCurrencyCode.toString()
                }catch(e: Exception){
                    Log.e("CONFIGDATA", e.toString())
                }

            }
            if(TokenUtils.TOKEN_CONTENT!="Bearer null"){
                TokenUtils.USER_LOGIN=toData.email
                _islogin.postValue(true)
            }else{
                isError.postValue(true)
                _errorMessage.postValue("Error de login: verifique sus credenciales")
            }
            _isLoading.postValue(false)
        }   catch (e: SocketTimeoutException){
            isError.postValue(true)
            _errorMessage.postValue("No se pudo conectar al servidor. Verifica tu red.")
        }catch (e: IOException) {
            isError.postValue(true)
            _errorMessage.postValue("Error de red: ${e.localizedMessage}")
        } catch (e: Exception) {
            isError.postValue(true)
            _errorMessage.postValue("Ocurrió un error inesperado.")
        }

        }
    }

    fun clearErrorMessage() {
        _errorMessage.postValue(null)
        isError.postValue(false)
        _isLoading.postValue(false)
    }
}