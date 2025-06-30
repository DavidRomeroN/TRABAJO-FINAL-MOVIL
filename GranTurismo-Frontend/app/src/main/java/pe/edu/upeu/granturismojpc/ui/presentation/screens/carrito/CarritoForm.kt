package pe.edu.upeu.granturismojpc.ui.presentation.screens.carrito

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.k0shk0sh.compose.easyforms.BuildEasyForms
import com.github.k0shk0sh.compose.easyforms.EasyFormsResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.edu.upeu.granturismojpc.model.CarritoDto
import pe.edu.upeu.granturismojpc.model.CarritoItemDto
import pe.edu.upeu.granturismojpc.model.toDto
import pe.edu.upeu.granturismojpc.ui.navigation.Destinations
import pe.edu.upeu.granturismojpc.ui.presentation.components.Spacer
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonCancel
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.AccionButtonSuccess
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.DateTimePickerCustom
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyEasyFormsCustomStringResult
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.MyFormKeys
import pe.edu.upeu.granturismojpc.ui.presentation.components.form.NameTextField
import pe.edu.upeu.granturismojpc.ui.presentation.components.info.InfoCarrito
import pe.edu.upeu.granturismojpc.utils.TokenUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun CarritoForm(
    text: String,
    text1: String,
    text2: String,
    darkMode: MutableState<Boolean>,
    navController: NavHostController,
    viewModel: CarritoFormViewModel= hiltViewModel()
) {
    val carritoItem by viewModel.carritoItem.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDatosPrevios()
    }

    var carritoItemD: CarritoItemDto
    if (text!="0"){
        carritoItemD = Gson().fromJson(text, CarritoItemDto::class.java)
        LaunchedEffect(Unit) {
            viewModel.getCarritoItem(carritoItemD.idCarritoItem)
        }
        carritoItem?.let {
            carritoItemD=it.toDto()
            Log.i("DMPX","CarritoItem: ${carritoItemD.toString()}")
        }
    }else{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val ahora = LocalDateTime.now().format(formatter)
        carritoItemD= CarritoItemDto(
            0, 0, text1.toLong(), text2,1, "", "",
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    formulario(
        carritoItemD.idCarritoItem!!,
        darkMode,
        navController,
        carritoItemD,
        viewModel,
        text1,
        text2,
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
               carritoItem: CarritoItemDto,
               viewModel: CarritoFormViewModel,
               referenciaId: String?,
               tipo: String?
               ){
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val ahora = LocalDateTime.now().format(formatter)
    val car= CarritoItemDto(
        0, 0, 0,"", 0,  "", "",
    )
    val usuarioId = TokenUtils.USER_ID
    var carritoId by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        viewModel.carritos.collect { listaCarrito ->
            val carritosDelUsuario = listaCarrito.filter { it.usuario?.idUsuario == usuarioId }
            // Usa carritosDelUsuario como necesites
            Log.i("Carritos", "Carritos encontrados con id ${usuarioId} = ${carritosDelUsuario.size}")
            if (carritosDelUsuario.size==1){
                val carrito=carritosDelUsuario.get(0)
                Log.i("Carrito", "${carrito}")
                Log.i("Carrito", "${carrito.idCarrito}")

                carritoId=carrito.idCarrito}
        }
    }
    Log.i("Carrito", "${carritoId}")
    Scaffold(modifier = Modifier.padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom =
        32.dp)){
        BuildEasyForms { easyForm ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                InfoCarrito(tipo = tipo.toString(), referenciaId = referenciaId?.toLong()?:0)

                //NameTextField(easyForms = easyForm, text= carritoItem?.cantidadPersonas!!.toString(),"Cantidad de personas:", MyFormKeys.NAME )
                var cantidad by remember { mutableStateOf(carritoItem?.cantidadPersonas ?: 1) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Cantidad de personas:", modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { if (cantidad > 1) cantidad-- },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                    }

                    Text(
                        text = cantidad.toString(),
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { cantidad++ },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar")
                    }
                }
                NameTextField(easyForms = easyForm, text=carritoItem?.notas!!,"Notas (Opcional):", MyFormKeys.UTILIDAD )
                DateTimePickerCustom(easyForm = easyForm, label = "Fecha de reserva:", texts = carritoItem?.fechaReserva ?: "", key = MyFormKeys.FECHA, formDP = "yyyy-MM-dd'T'HH:mm:ss")


                Row(Modifier.align(Alignment.CenterHorizontally)){
                    val coroutineScope = rememberCoroutineScope()
                    AccionButtonSuccess(easyForms = easyForm, "Guardar", id){
                        coroutineScope.launch {
                            val lista=easyForm.formData()
                            car.carrito=carritoId
                            /*
                            if (idServicio=="0"){car.servicio=null}else{car.servicio=idServicio?.toLong()}
                            if (idActividad=="0"){car.actividad=null}else{car.actividad=idActividad?.toLong()}
                            if (idActividad=="0"){car.tipo="SERVICIO"}else{car.tipo="ACTIVIDAD"}
                            */
                            car.referenciaId= referenciaId?.toLong()!!
                            car.tipo= tipo.toString()
                            car.cantidadPersonas = cantidad
                            car.notas=((lista.get(0) as EasyFormsResult.StringResult).value)
                            car.fechaReserva = (lista.get(1) as MyEasyFormsCustomStringResult).value
                            if (id == 0L) {
                                if (car.carrito == 0L) {
                                    val carritoId = viewModel.crearCarritoYObtenerId()
                                    car.carrito = carritoId
                                }
                                viewModel.addCarritoItem(car)
                            }else{
                                car.idCarritoItem=id
                                Log.i("MODIFICAR", "M:"+car)
                                viewModel.editCarritoItem(car)
                            }
                            navController.navigate(Destinations.CarritoMainSC.passId(TokenUtils.USER_ID.toString())){
                                popUpTo(Destinations.CarritoFormSC.route) { inclusive = true }
                            }
                        }
                    }
                    Spacer()
                    AccionButtonCancel(easyForms = easyForm, "Cancelar"){
                        navController.popBackStack()
                    }
                }
            }
        }
    }

}

fun splitCadena(data:String):String{
    return if(data!="") data.split("-")[0] else ""
}