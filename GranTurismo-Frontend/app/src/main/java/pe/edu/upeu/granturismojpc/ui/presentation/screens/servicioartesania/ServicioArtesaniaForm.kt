package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k0shk0sh.compose.easyforms.BuildEasyForms
import com.github.k0shk0sh.compose.easyforms.EasyFormsResult
import com.google.gson.Gson
import pe.edu.upeu.granturismojpc.model.ComboModel
import pe.edu.upeu.granturismojpc.model.DestinoResp
import pe.edu.upeu.granturismojpc.model.ServicioArtesaniaDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBox
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxFour
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxThre
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxTwo
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.DateTimePickerCustom
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyEasyFormsCustomStringResult
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyFormKeys
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.NameTextField
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun ServicioArtesaniaForm(
    text: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: ServicioArtesaniaFormViewModel= hiltViewModel()
) {
    val servicioArtesania by viewModel.servicioArtesania.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val servicios by viewModel.servs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var servicioArtesaniaD: ServicioArtesaniaDto
    if (text!="0"){
        servicioArtesaniaD = Gson().fromJson(text, ServicioArtesaniaDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getServicioArtesania(servicioArtesaniaD.idArtesania)
        }
        servicioArtesania?.let {
            servicioArtesaniaD=it.toDto()
            Log.i("DMPX","ServicioArtesania: ${servicioArtesaniaD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        servicioArtesaniaD= ServicioArtesaniaDto(
            0, 0, "", "", 0, false,"", "", 0, false, ""
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        servicioArtesaniaD.idArtesania!!,
        darkMode,
        navController,
        servicioArtesaniaD,
        viewModel,
        servicios,
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
               servicioArtesania: ServicioArtesaniaDto,
               viewModel: ServicioArtesaniaFormViewModel,
               listServicio: List<ServicioResp>,
){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val servart= ServicioArtesaniaDto(
        0, 0, "", "", 0, false,"", "", 0, false, ""
    )
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                NameTextField(easyForms = easyForm, text=servicioArtesania?.tipoArtesania!!,"Nomb. ServicioArtesania:", MyFormKeys.NAME )
                val listS: List<ComboModel> = listServicio.map { servicio ->
                    ComboModel(code = servicio.idServicio.toString(), name = servicio.nombreServicio)
                }
                ComboBox(easyForm = easyForm, "Servicio:", servicioArtesania?.servicio?.let { it.toString() } ?: "", listS)
                /*val listC: List<ComboModel> = listCategoria.map { categor ->
                    ComboModel(code = categor.idCategoria.toString(), name = categor.nombre)
                }
                ComboBoxTwo(easyForm = easyForm, "Categoría:", servicioArtesania?.categoria?.let { it.toString() } ?: "", listC)
                val listUM: List<ComboModel> = listUnidMed.map { unitMed ->
                    ComboModel(code = unitMed.idUnidad.toString(), name = unitMed.nombreMedida)
                }*/

                //ComboBoxThre(easyForm = easyForm, label = "Unidad Medida:", servicioArtesania?.unidadMedida?.let { it.toString() } ?: "", list =listUM)

                //NameTextField(easyForms = easyForm, text=servicioArtesania?.nivelDificultad.toString()!!,"Nivel de dificultad:", MyFormKeys.PU )

                val listNivel: List<ComboModel> = listOf(
                    ComboModel(code = "PRINCIPIANTE", name = "Nivel Principiante"),
                    ComboModel(code = "INTERMEDIO", name = "Nivel Intermedio"),
                    ComboModel(code = "AVANZADO", name = "Nivel Avanzado")
                )
                val listSiNo: List<ComboModel> = listOf(
                    ComboModel(code = "true", name = "Sí"),
                    ComboModel(code = "false", name = "No")
                )
                val valorInicial = servicioArtesania?.nivelDificultad?.let { it.toString() } ?:""
                ComboBoxTwo(
                    easyForm = easyForm, label = "Nivel de dificultad:", textv = valorInicial,
                    list = listNivel
                )

                NameTextField(easyForms = easyForm, text=servicioArtesania?.duracionTaller.toString()!!,"Duración del Taller:", MyFormKeys.PU_OLD )
                //NameTextField(easyForms = easyForm, text=servicioArtesania?.incluyeMaterial.toString()!!,"Incluye Material:", MyFormKeys.CHECKBOX )

                ComboBoxThre(
                    easyForm = easyForm, label = "¿Incluye material?", textv = servicioArtesania?.incluyeMaterial?.let { it.toString() } ?:"false",
                    list = listSiNo
                )

                NameTextField(easyForms = easyForm, text=servicioArtesania?.artesania.toString()!!,"Artesanía:", MyFormKeys.DESCRIPTION )
                NameTextField(easyForms = easyForm, text=servicioArtesania?.origenCultural.toString()!!,"Origen Cultural:", MyFormKeys.STOCK )
                NameTextField(easyForms = easyForm, text=servicioArtesania?.maxParticipantes.toString()!!,"Num Max de Participantes:", MyFormKeys.MODFH )
                //NameTextField(easyForms = easyForm, text=servicioArtesania?.visitaTaller.toString()!!,"Visito Taller:", MyFormKeys.UTILIDAD )
                ComboBoxFour(
                    easyForm = easyForm, label = "¿Visita al taller?", textv = servicioArtesania?.visitaTaller?.let { it.toString() } ?:"false",
                    list = listSiNo
                )
                NameTextField(easyForms = easyForm, text=servicioArtesania?.artesano.toString()!!,"Artesano:", MyFormKeys.SWITCH )

                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        val lista=easyForm.formData()
                        servart.tipoArtesania=(lista.get(0) as EasyFormsResult.StringResult).value
                        servart.servicio= (splitCadena((lista.get(1) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        //serv.unidadMedida= (splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        servart.nivelDificultad=(splitCadena((lista.get(2) as EasyFormsResult.GenericStateResult<String>).value))
                        servart.duracionTaller=((lista.get(3) as EasyFormsResult.StringResult).value).toInt()
                        servart.incluyeMaterial=(splitCadena((lista.get(4) as EasyFormsResult.GenericStateResult<String>).value)).toBoolean()
                        servart.artesania=((lista.get(5) as EasyFormsResult.StringResult).value)
                        servart.origenCultural=((lista.get(6) as EasyFormsResult.StringResult).value)
                        servart.maxParticipantes=((lista.get(7) as EasyFormsResult.StringResult).value).toInt()
                        servart.visitaTaller=(splitCadena((lista.get(8) as EasyFormsResult.GenericStateResult<String>).value)).toBoolean()
                        servart.artesano=((lista.get(9) as EasyFormsResult.StringResult).value)

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "P:"+ servart.servicio)
                            //Log.i("AGREGAR", "VI:"+ serv.stock)
                            viewModel.addServicioArtesania(servart)
                        }else{
                            servart.idArtesania=id
                            Log.i("MODIFICAR", "M:"+servart)
                            viewModel.editServicioArtesania(servart)
                        }
                        navController.navigate(Destinations.ServicioArtesaniaMainSC.route){
                            popUpTo(Destinations.ServicioArtesaniaFormSC.route) { inclusive = true }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.ServicioArtesaniaMainSC.route)
                    }
                }
            }
        }
    }

}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}