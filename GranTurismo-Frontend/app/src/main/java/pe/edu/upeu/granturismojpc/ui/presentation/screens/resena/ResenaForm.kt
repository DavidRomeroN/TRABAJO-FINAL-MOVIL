package pe.edu.upeu.granturismojpc.ui.presentation.screens.resena

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k0shk0sh.compose.easyforms.BuildEasyForms
import com.github.k0shk0sh.compose.easyforms.EasyFormsResult
import com.google.gson.Gson
import pe.edu.upeu.granturismojpc.model.ComboModel
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.ResenaDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBox
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxThre
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxTwo
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.DateTimePickerCustom
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.DescriptionValidationType
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.LimitedTextField
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyEasyFormsCustomStringResult
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyFormKeys
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.NameTextField
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.RatingBarEasyForms
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.RatingValidationType
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion.splitCadena
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun ResenaForm(
    text: String,
    packId: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: ResenaFormViewModel= hiltViewModel()
) {
    val resena by viewModel.resena.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val packs by viewModel.packs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var resenaD: ResenaDto
    if (text!="0"){
        resenaD = Gson().fromJson(text, ResenaDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getResena(resenaD.idResena)
        }
        resena?.let {
            resenaD=it.toDto()
            Log.i("DMPX","Resena: ${resenaD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        resenaD= ResenaDto(
            0, 0, "", ahora, 0, packId.toLongOrNull() ?: 0L
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        resenaD.idResena!!,
        darkMode,
        navController,
        resenaD,
        viewModel,
        packs,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MissingPermission",
    "CoroutineCreationDuringComposition"
)
@Composable
fun formulario(id:Long,
               darkMode: MutableState<Boolean>,
               navController: NavHostController,
               resena: ResenaDto,
               viewModel: ResenaFormViewModel,
               listPaquete: List<PaqueteResp>,
){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val res= ResenaDto(
        0, 0, "", ahora, 0, resena.paquete
    )
    val idUser by viewModel.user.collectAsState()
    val paqueteId = resena.paquete.toString()
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                //NameTextField(easyForms = easyForm, text=resena?.calificacion!!.toString(),"Calificación:", MyFormKeys.NAME )

                val listRating: List<ComboModel> = listOf(
                    ComboModel(code = "5", name = "5 estrellas"),
                    ComboModel(code = "4", name = "4 estrellas"),
                    ComboModel(code = "3", name = "3 estrellas"),
                    ComboModel(code = "2", name = "2 estrellas"),
                    ComboModel(code = "1", name = "1 estrella"),
                )
                val valorInicial = resena?.calificacion?.let { it.toString() } ?:""
                ComboBox(
                    easyForm = easyForm, label = "Calificación", textv = valorInicial,
                    list = listRating
                )
                /*
                RatingBarEasyForms(
                    easyForms = easyForm,
                    initialValue = resena?.calificacion ?: 0,
                    formKey = MyFormKeys.RATING,
                    validationType = RatingValidationType
                )*/

                Text(
                    text = "Usuario: ${TokenUtils.USER_LOGIN}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Paquete: ${paqueteId}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                LimitedTextField(
                    easyForms = easyForm,
                    text = resena?.comentario.orEmpty(),
                    label = "Descripción:",
                    formKey = MyFormKeys.DESCRIPTION,
                    validationType = DescriptionValidationType,
                    maxLength = 200
                )


                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        if (TokenUtils.USER_ID <= 0) {
                            // Mostrar un mensaje de error o redirigir al login
                            Toast.makeText(
                                TokenUtils.CONTEXTO_APPX,
                                "Debe iniciar sesión para crear una reseña",
                                Toast.LENGTH_LONG
                            ).show()
                            return@AccionButtonSuccess
                        }
                        val lista=easyForm.formData()
                        /*val calificacionStr = (lista.get(0) as? EasyFormsResult.GenericStateResult<TextFieldValue>)?.value?.text
                        val calificacionInt = calificacionStr?.toIntOrNull() ?: 0
                        res.calificacion = calificacionInt*/
                        res.calificacion=(splitCadena((lista.get(0) as EasyFormsResult.GenericStateResult<String>).value)).toInt()
                        res.usuario = TokenUtils.USER_ID
                        res.paquete= paqueteId.toLong()
                        //res.unidadMedida= (splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        res.comentario=((lista.get(1) as EasyFormsResult.StringResult).value)
                        res.fecha=ahora
                        //res.precioBase=((lista.get(3) as EasyFormsResult.StringResult).value).toDouble()
                        //res.estado=((lista.get(4) as EasyFormsResult.StringResult).value)

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "P:"+ res.paquete)
                            Log.i("AGREGAR", "U:" + res.usuario)
                            viewModel.addResena(res)
                        }else{
                            res.idResena=id
                            Log.i("MODIFICAR", "M:"+res)
                            viewModel.editResena(res)
                        }
                        navController.navigate(Destinations.ResenaMainSC.passId(paqueteId))
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.ResenaMainSC.passId(paqueteId))
                    }
                }
            }
        }
    }

}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}