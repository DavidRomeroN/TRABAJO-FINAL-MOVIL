package pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio

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
import pe.edu.upeu.granturismojpc.model.ServicioDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.TipoServicioResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBox
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
fun ServicioForm(
    text: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: ServicioFormViewModel= hiltViewModel()
) {
    val servicio by viewModel.servicio.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val tipos by viewModel.tipos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var servicioD: ServicioDto
    if (text!="0"){
        servicioD = Gson().fromJson(text, ServicioDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getServicio(servicioD.idServicio)
        }
        servicio?.let {
            servicioD=it.toDto()
            Log.i("DMPX","Servicio: ${servicioD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        servicioD= ServicioDto(
            0, "", "", 0.0, "", 0
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        servicioD.idServicio!!,
        darkMode,
        navController,
        servicioD,
        viewModel,
        tipos,
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
               servicio: ServicioDto,
               viewModel: ServicioFormViewModel,
               listTipo: List<TipoServicioResp>,
){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val serv= ServicioDto(
        0, "", "", 0.0, "", 0
    )
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                NameTextField(easyForms = easyForm, text=servicio?.nombreServicio!!,"Nomb. Servicio:", MyFormKeys.NAME )
                val listT: List<ComboModel> = listTipo.map { tipo ->
                    ComboModel(code = tipo.idTipo.toString(), name = tipo.nombre)
                }
                ComboBox(easyForm = easyForm, "Tipo:", servicio?.tipo?.let { it.toString() } ?: "", listT)
                /*val listC: List<ComboModel> = listCategoria.map { categor ->
                    ComboModel(code = categor.idCategoria.toString(), name = categor.nombre)
                }
                ComboBoxTwo(easyForm = easyForm, "Categoría:", servicio?.categoria?.let { it.toString() } ?: "", listC)
                val listUM: List<ComboModel> = listUnidMed.map { unitMed ->
                    ComboModel(code = unitMed.idUnidad.toString(), name = unitMed.nombreMedida)
                }*/

                //ComboBoxThre(easyForm = easyForm, label = "Unidad Medida:", servicio?.unidadMedida?.let { it.toString() } ?: "", list =listUM)

                NameTextField(easyForms = easyForm, text=servicio?.descripcion.toString()!!,"Descripción:", MyFormKeys.DESCRIPTION )
                //DateTimePickerCustom(easyForm = easyForm, label = "Fecha de registro:", texts = servicio?.fechaRegistro ?: "", key = MyFormKeys.FECHA, formDP = "yyyy-MM-dd HH:mm:ss")
                NameTextField(easyForms = easyForm, text=servicio?.precioBase.toString()!!,"Precio Base:", MyFormKeys.PU_OLD )
                //NameTextField(easyForms = easyForm, text=servicio?.estado.toString()!!,"Estado:", MyFormKeys.UTILIDAD )

                val listSiNo: List<ComboModel> = listOf(
                    ComboModel(code = "ACTIVO", name = "Servicio Activo"),
                    ComboModel(code = "INACTIVO", name = "Servicio Inactivo")
                )
                val valorInicial = servicio?.estado?.let { it.toString() } ?:"ACTIVO"
                ComboBoxTwo(
                    easyForm = easyForm, label = "Estado", textv = valorInicial,
                    list = listSiNo
                )

                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        val lista=easyForm.formData()
                        serv.nombreServicio=(lista.get(0) as EasyFormsResult.StringResult).value
                        serv.tipo= (splitCadena((lista.get(1) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        //serv.unidadMedida= (splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        serv.descripcion=((lista.get(2) as EasyFormsResult.StringResult).value)
                        serv.precioBase=((lista.get(3) as EasyFormsResult.StringResult).value).toDouble()
                        serv.estado=(splitCadena((lista.get(4) as EasyFormsResult.GenericStateResult<String>).value))

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "P:"+ serv.tipo)
                            //Log.i("AGREGAR", "VI:"+ serv.stock)
                            viewModel.addServicio(serv)
                        }else{
                            serv.idServicio=id
                            Log.i("MODIFICAR", "M:"+serv)
                            viewModel.editServicio(serv)
                        }
                        navController.navigate(Destinations.ServicioMainSC.route){
                            popUpTo(Destinations.ServicioFormSC.route) { inclusive = true }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.ServicioMainSC.route)
                    }
                }
            }
        }
    }

}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}