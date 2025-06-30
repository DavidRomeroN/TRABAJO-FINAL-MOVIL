package pe.edu.upeu.granturismojpc.ui.presentation.screens.serviciohotelera

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
import pe.edu.upeu.granturismojpc.model.ServicioHoteleraDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
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
fun ServicioHoteleraForm(
    text: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: ServicioHoteleraFormViewModel= hiltViewModel()
) {
    val servicioHotelera by viewModel.servicioHotelera.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val servicios by viewModel.servs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var servicioHoteleraD: ServicioHoteleraDto
    if (text!="0"){
        servicioHoteleraD = Gson().fromJson(text, ServicioHoteleraDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getServicioHotelera(servicioHoteleraD.idHoteleria)
        }
        servicioHotelera?.let {
            servicioHoteleraD=it.toDto()
            Log.i("DMPX","ServicioHotelera: ${servicioHoteleraD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        servicioHoteleraD= ServicioHoteleraDto(
            0, 0, "", 0, "", 0
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        servicioHoteleraD.idHoteleria!!,
        darkMode,
        navController,
        servicioHoteleraD,
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
               servicioHotelera: ServicioHoteleraDto,
               viewModel: ServicioHoteleraFormViewModel,
               listServicio: List<ServicioResp>,
){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val servhot= ServicioHoteleraDto(
        0, 0, "", 0, "", 0
    )
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                NameTextField(easyForms = easyForm, text=servicioHotelera?.tipoHabitacion!!,"Nomb. ServicioHotelera:", MyFormKeys.NAME )
                val listS: List<ComboModel> = listServicio.map { servicio ->
                    ComboModel(code = servicio.idServicio.toString(), name = servicio.nombreServicio)
                }
                ComboBox(easyForm = easyForm, "Servicio:", servicioHotelera?.servicio?.let { it.toString() } ?: "", listS)


                NameTextField(easyForms = easyForm, text=servicioHotelera?.estrellas.toString()!!,"Estrellas:", MyFormKeys.PU )
                //NameTextField(easyForms = easyForm, text=servicioHotelera?.incluyeDesayuno.toString()!!,"Incluye Desayuno:", MyFormKeys.PU_OLD )

                val listSiNo: List<ComboModel> = listOf(
                    ComboModel(code = "Si", name = "Incluye desayuno"),
                    ComboModel(code = "No", name = "No incluye desayuno")
                )
                val valorInicial = servicioHotelera?.incluyeDesayuno?.let { it.toString() } ?:"No"
                ComboBoxTwo(
                    easyForm = easyForm, label = "¿Incluye desayuno?", textv = valorInicial,
                    list = listSiNo
                )

                NameTextField(easyForms = easyForm, text=servicioHotelera?.maxPersonas.toString()!!,"Cant. Máx. de Personas:", MyFormKeys.CHECKBOX )
                //NameTextField(easyForms = easyForm, text=servicioHotelera?.fechaInicio.toString()!!,"Fecha de inicio:", MyFormKeys.FECHA )
                //NameTextField(easyForms = easyForm, text=servicioHotelera?.fechaFin.toString()!!,"Fecha Fin:", MyFormKeys.DATE2 )
                //NameTextField(easyForms = easyForm, text=servicioHotelera?.destino.toString()!!,"ID Destino:", MyFormKeys.MODFH )

                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        val lista=easyForm.formData()
                        servhot.tipoHabitacion=(lista.get(0) as EasyFormsResult.StringResult).value
                        servhot.servicio= (splitCadena((lista.get(1) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        //serv.unidadMedida= (splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        servhot.estrellas=((lista.get(2) as EasyFormsResult.StringResult).value).toInt()
                        servhot.incluyeDesayuno=(splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value))
                        servhot.maxPersonas=((lista.get(4) as EasyFormsResult.StringResult).value).toInt()

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "P:"+ servhot.servicio)
                            //Log.i("AGREGAR", "VI:"+ serv.stock)
                            viewModel.addServicioHotelera(servhot)
                        }else{
                            servhot.idHoteleria=id
                            Log.i("MODIFICAR", "M:"+servhot)
                            viewModel.editServicioHotelera(servhot)
                        }
                        navController.navigate(Destinations.ServicioHoteleraMainSC.route){
                            popUpTo(Destinations.ServicioHoteleraFormSC.route) { inclusive = true }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.ServicioHoteleraMainSC.route)
                    }
                }
            }
        }
    }

}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}