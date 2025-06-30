package pe.edu.upeu.granturismojpc.ui.presentation.screens.login

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.k0shk0sh.compose.easyforms.BuildEasyForms
import com.github.k0shk0sh.compose.easyforms.EasyFormsResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pe.edu.upeu.granturismojpc.model.UsuarioDto
import pe.edu.upeu.granturismojpc.model.getStringsByCode
import pe.edu.upeu.granturismojpc.ui.presentation.components.ImageLogin
import pe.edu.upeu.granturismojpc.ui.presentation.components.ErrorImageAuth
import pe.edu.upeu.granturismojpc.ui.presentation.components.LanguageDropdown
import pe.edu.upeu.granturismojpc.ui.presentation.components.ProgressBarLoading
import pe.edu.upeu.granturismojpc.ui.theme.LightRedColors
import pe.edu.upeu.granturismojpc.utils.ComposeReal
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.EmailTextField
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.LoginButton
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.PasswordTextField
import pe.edu.upeu.granturismojpc.ui.theme.GranTurismoJPCTheme
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    navigateToRegisterScreen: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.observeAsState(false)
    val isLogin by viewModel.islogin.observeAsState(false)
    val isError by viewModel.isError.observeAsState(false)
    val loginResul by viewModel.listUser.observeAsState()
    val errorMessage by viewModel.errorMessage.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    if(TokenUtils.TEMP_LANGUAJE=="nada"){
        TokenUtils.TEMP_LANGUAJE=getSystemLanguage()
    }
    var currentLanguage by remember { mutableStateOf(TokenUtils.TEMP_LANGUAJE) }
    val strings = remember(currentLanguage) { getStringsByCode(currentLanguage) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageLogin()
        Text(strings.welcome, fontSize = 20.sp)
        BuildEasyForms { easyForm ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LanguageDropdown(
                    selectedLanguage = currentLanguage,
                    onLanguageSelected = { selectedCode ->
                        currentLanguage = selectedCode
                        // Aquí puedes guardar el idioma en preferencias o hacer algo más
                    }
                )
                EmailTextField(easyForms = easyForm, text = "", strings.email, "U")
                PasswordTextField(easyForms = easyForm, text = "", label = strings.password, key = "password")
                Spacer(modifier = Modifier.height(10.dp))
                LoginButton(
                    easyForms = easyForm, onClick = {
                        val dataForm = easyForm.formData()
                        val user = UsuarioDto(
                            (dataForm.get(0) as EasyFormsResult.StringResult).value,
                            (dataForm.get(1) as EasyFormsResult.StringResult).value
                        )
                        viewModel.loginSys(user)
                        scope.launch {
                            delay(3600)
                            if (isLogin && loginResul != null) {
                                Log.i("TOKENV", TokenUtils.TOKEN_CONTENT)
                                Log.i("DATA", loginResul!!.email)
                                navigateToHome.invoke()
                            } else {
                                Log.v("ERRORX", "Error logeo")
                                Toast.makeText(context, "Error al conectar", Toast.LENGTH_LONG)
                            }
                        }
                    },

                    label = strings.login
                )
                /*Spacer(modifier = Modifier.height(10.dp))
                Button (onClick = {
                    navigateToHome.invoke()
                }) {
                    Text("Iniciar sesion mas tarde")
                }*/

                // Espaciador para separar el botón de registrarse
                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    navigateToRegisterScreen.invoke()
                }) {
                    Text(strings.register)
                }

                ComposeReal.COMPOSE_TOP.invoke()
            }
        }
        ErrorImageAuth(isImageValidate = isError)
        ProgressBarLoading(isLoading = isLoading)
    }
    // Mostrar Snackbar manualmente
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier
            .wrapContentHeight(Alignment.Bottom)
            .padding(16.dp),

        )
    // Mostrar el snackbar cuando haya mensaje de error
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    val colors = LightRedColors
    val darkTheme = isSystemInDarkTheme()
    GranTurismoJPCTheme(colorScheme = colors) {
        LoginScreen(
            navigateToHome = {},
            navigateToRegisterScreen = {}
        )
    }
}
fun getSystemLanguage(): String {
    return Locale.getDefault().language // "es" o "en"
}