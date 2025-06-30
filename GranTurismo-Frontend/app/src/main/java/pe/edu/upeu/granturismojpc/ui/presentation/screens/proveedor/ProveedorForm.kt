package pe.edu.upeu.granturismojpc.ui.presentation.screens.proveedor

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
import pe.edu.upeu.granturismojpc.model.ProveedorDto
import pe.edu.upeu.granturismojpc.model.ProveedorResp
import pe.edu.upeu.granturismojpc.model.UsuarioResp
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.ComboBox
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.DateTimePickerCustom
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyEasyFormsCustomStringResult
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyFormKeys
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.NameTextField
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun ProveedorForm(
    text: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: ProveedorFormViewModel= hiltViewModel()
) {
    val proveedor by viewModel.proveedor.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val usuarios by viewModel.users.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var proveedorD: ProveedorDto
    if (text!="0"){
        proveedorD = Gson().fromJson(text, ProveedorDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getProveedor(proveedorD.idProveedor)
        }
        proveedor?.let {
            proveedorD=it.toDto()
            Log.i("DMPX","Proveedor: ${proveedorD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        proveedorD= ProveedorDto(
            0, "", "", "", ahora, 0
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        proveedorD.idProveedor!!,
        darkMode,
        navController,
        proveedorD,
        viewModel,
        usuarios,
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
               proveedor: ProveedorDto,
               viewModel: ProveedorFormViewModel,
               listUsuario: List<ProveedorResp>,
               ){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val prov= ProveedorDto(
        0, "", "", "", ahora, 0
    )
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                NameTextField(easyForms = easyForm, text=proveedor?.nombreCompleto!!,"Nomb. Proveedor:", MyFormKeys.NAME )
                NameTextField(easyForms = easyForm, text=proveedor?.usuario.toString()!!,"ID Usuario:", MyFormKeys.UTILIDAD )
                /*val listU: List<ComboModel> = listUsuario.map { usuario ->
                    ComboModel(code = usuario.idUsuario.toString(), name = usuario.user)
                }
                ComboBox(easyForm = easyForm, "Usuario:", proveedor?.usuario?.let { it.toString() } ?: "", listU)*/
                /*val listC: List<ComboModel> = listCategoria.map { categor ->
                    ComboModel(code = categor.idCategoria.toString(), name = categor.nombre)
                }
                ComboBoxTwo(easyForm = easyForm, "Categoría:", proveedor?.categoria?.let { it.toString() } ?: "", listC)
                val listUM: List<ComboModel> = listUnidMed.map { unitMed ->
                    ComboModel(code = unitMed.idUnidad.toString(), name = unitMed.nombreMedida)
                }*/

                //ComboBoxThre(easyForm = easyForm, label = "Unidad Medida:", proveedor?.unidadMedida?.let { it.toString() } ?: "", list =listUM)

                NameTextField(easyForms = easyForm, text=proveedor?.email.toString()!!,"Correo electrónico:", MyFormKeys.EMAIL )
                NameTextField(easyForms = easyForm, text=proveedor?.telefono.toString()!!,"Teléfono:", MyFormKeys.PU )
                DateTimePickerCustom(easyForm = easyForm, label = "Fecha de registro:", texts = proveedor?.fechaRegistro ?: "", key = MyFormKeys.FECHA, formDP = "yyyy-MM-dd HH:mm:ss")
                //NameTextField(easyForms = easyForm, text=proveedor?.fechaInicio.toString()!!,"Fecha de inicio:", MyFormKeys.FECHA )
                //NameTextField(easyForms = easyForm, text=proveedor?.fechaFin.toString()!!,"Fecha Fin:", MyFormKeys.DATE2 )

                Row(Modifier.align(Alignment.CenterHorizontally)){
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        val lista=easyForm.formData()
                        prov.nombreCompleto=(lista.get(0) as EasyFormsResult.StringResult).value
                        prov.usuario=((lista.get(1) as EasyFormsResult.StringResult).value).toLong()
                        //prov.usuario= (splitCadena((lista.get(1) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        //prov.categoria= (splitCadena((lista.get(2) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        //prov.unidadMedida= (splitCadena((lista.get(3) as EasyFormsResult.GenericStateResult<String>).value)).toLong()
                        prov.email=((lista.get(2) as EasyFormsResult.StringResult).value)
                        prov.telefono=((lista.get(3) as EasyFormsResult.StringResult).value)
                        prov.fechaRegistro = (lista.get(4) as MyEasyFormsCustomStringResult).value

                        if (id==0L.toLong()){
                            Log.i("AGREGAR", "U:"+ prov.usuario)
                            //Log.i("AGREGAR", "VI:"+ prov.stock)
                            viewModel.addProveedor(prov)
                        }else{
                            prov.idProveedor=id
                            Log.i("MODIFICAR", "M:"+prov)
                            viewModel.editProveedor(prov)
                        }
                        navController.navigate(Destinations.ProveedorMainSC.route){
                            popUpTo(Destinations.ProveedorFormSC.route) { inclusive = true }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.navigate(Destinations.ProveedorMainSC.route)
                    }
                }
            }
        }
    }

}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}