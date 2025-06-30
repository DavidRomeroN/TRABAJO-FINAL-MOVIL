package pe.edu.upeu.granturismojpc.ui.presentation.screens.paquetedetalle

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k0shk0sh.compose.easyforms.BuildEasyForms
import com.github.k0shk0sh.compose.easyforms.EasyFormsResult
import com.google.gson.Gson
import pe.edu.upeu.granturismojpc.model.ComboModel
import pe.edu.upeu.granturismojpc.model.PaqueteDetalleDto
import pe.edu.upeu.granturismojpc.model.PaqueteResp
import pe.edu.upeu.granturismojpc.model.ServicioResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBox
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBoxTwo
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyFormKeys
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.NameTextField
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun PaqueteDetalleForm(
    text: String,
    packId: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: PaqueteDetalleFormViewModel= hiltViewModel()
) {
    val paqueteDetalle by viewModel.paqueteDetalle.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val packs by viewModel.packs.collectAsState()
    val servs by viewModel.servs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var paqueteDetalleD: PaqueteDetalleDto
    if (text!="0"){
        paqueteDetalleD = Gson().fromJson(text, PaqueteDetalleDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getPaqueteDetalle(paqueteDetalleD.idPaqueteDetalle)
        }
        paqueteDetalle?.let {
            paqueteDetalleD=it.toDto()
            Log.i("DMPX","PaqueteDetalle: ${paqueteDetalleD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        paqueteDetalleD= PaqueteDetalleDto(
            0, 0, 0.0, packId.toLongOrNull() ?: 0L, 0
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        paqueteDetalleD.idPaqueteDetalle!!,
        darkMode,
        navController,
        paqueteDetalleD,
        viewModel,
        packs,
        servs
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
               paqueteDetalle: PaqueteDetalleDto,
               viewModel: PaqueteDetalleFormViewModel,
               listPaquete: List<PaqueteResp>,
               listServicio: List<ServicioResp>,
){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val serv= PaqueteDetalleDto(
        0, 0, 0.0, paqueteDetalle.paquete, 0
    )
    val paqueteId = paqueteDetalle.paquete.toString()
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                /*val listP: List<ComboModel> = listPaquete.map { paquete ->
                    ComboModel(code = paquete.idPaquete.toString(), name = paquete.titulo)
                }
                ComboBox(easyForm = easyForm, "Paquete:", paqueteDetalle?.paquete?.let { it.toString() } ?: "", listP)*/
                Text(
                    text = "Paquete: ${paqueteId}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                val listS: List<ComboModel> = listServicio.map { servicio ->
                    ComboModel(code = servicio.idServicio.toString(), name = servicio.nombreServicio)
                }
                ComboBoxTwo(easyForm = easyForm, "Servicio:", paqueteDetalle?.servicio?.let { it.toString() } ?: "", listS)

                NameTextField(easyForms = easyForm, text=paqueteDetalle?.cantidad.toString()!!,"Cantidad:", MyFormKeys.DESCRIPTION )
                NameTextField(easyForms = easyForm, text=paqueteDetalle?.precioEspecial.toString()!!,"Precio Especial:", MyFormKeys.PU_OLD )

                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        val lista=easyForm.formData()
                        serv.paquete= paqueteId.toLong()
                        serv.servicio= (splitCadena((lista.get(0) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        serv.cantidad=((lista.get(1) as EasyFormsResult.StringResult).value).toLong()
                        serv.precioEspecial=((lista.get(2) as EasyFormsResult.StringResult).value).toDouble()

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "P:"+ serv.paquete)
                            Log.i("AGREGAR", "S:"+ serv.servicio)
                            //Log.i("AGREGAR", "VI:"+ serv.stock)
                            viewModel.addPaqueteDetalle(serv)
                        }else{
                            serv.idPaqueteDetalle=id
                            Log.i("MODIFICAR", "M:"+serv)
                            viewModel.editPaqueteDetalle(serv)
                        }
                        navController.navigate(Destinations.PaqueteDetalleMainSC.passId(paqueteId)){
                            popUpTo(Destinations.PaqueteDetalleFormSC.route) { inclusive = true }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.PaqueteDetalleMainSC.passId(paqueteId))
                    }
                }
            }
        }
    }

}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}