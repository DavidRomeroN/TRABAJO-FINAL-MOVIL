package pe.edu.upeu.granturismojpc.ui.navigation

//import pe.edu.upeu.granturismojpc.ui.presentation.screens.Reservas
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pe.edu.upeu.granturismojpc.ui.presentation.screens.Actividades
import pe.edu.upeu.granturismojpc.ui.presentation.screens.DetalleScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.Pantalla1
import pe.edu.upeu.granturismojpc.ui.presentation.screens.Pantalla2
import pe.edu.upeu.granturismojpc.ui.presentation.screens.Pantalla3
import pe.edu.upeu.granturismojpc.ui.presentation.screens.Pantalla5
import pe.edu.upeu.granturismojpc.ui.presentation.screens.RecomendacionScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.ReservaCarritoScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad.ActividadForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividad.ActividadMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle.ActividadDetalleForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.actividaddetalle.ActividadDetalleMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.carrito.CarritoForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.carrito.CarritoMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.chatbot.ChatScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.chatbot.ChatViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.destino.DestinoForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.destino.DestinoMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.favorito.FavoritoMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.favorito.favoritos.FavoritoScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.home.HomeScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.home.HomeViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.idioma.SettingsScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.idioma.SettingsViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.login.LoginScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.PaqueteForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquete.PaqueteMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquetedetalle.PaqueteDetalleForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.paquetedetalle.PaqueteDetalleMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.Perfil
import pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.pantallas.CondicionesScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.pantallas.ContactoScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.pantallas.CurrencyScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.pantallas.PlanesScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.pantallas.PrivacyScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.perfil.pantallas.PuntosScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.proveedor.ProveedorForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.proveedor.ProveedorMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.qr.QrGenerationScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.qr.QrManagementScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.qr.QrScannerScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.qr.VerificacionArtesanoScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.register.RegisterScreen
import pe.edu.upeu.granturismojpc.ui.presentation.screens.resena.ResenaForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.resena.ResenaMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.reserva.Pantalla4
import pe.edu.upeu.granturismojpc.ui.presentation.screens.reserva.ReservaViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.reserva.Reservas
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio.ServicioForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio.ServicioMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio.ServicioMainViewModel
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicio.detalle.ServicioDetalle
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion.ServicioAlimentacionForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioalimentacion.ServicioAlimentacionMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania.ServicioArtesaniaForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.servicioartesania.ServicioArtesaniaMain
import pe.edu.upeu.granturismojpc.ui.presentation.screens.serviciohotelera.ServicioHoteleraForm
import pe.edu.upeu.granturismojpc.ui.presentation.screens.serviciohotelera.ServicioHoteleraMain

@Composable
fun NavigationHost(
    navController: NavHostController,
    darkMode: MutableState<Boolean>,
    modif: PaddingValues,
) {
    NavHost(
        navController = navController, startDestination =
            Destinations.Login.route
    ) {
        composable(Destinations.Login.route) {
            LoginScreen(
                navigateToHome = {navController.navigate(Destinations.HomeScreen.route) {
                    popUpTo(Destinations.Login.route) { inclusive = true }
                }},
                navigateToRegisterScreen = { navController.navigate(Destinations.Register.route) }
            )
        }

        composable(Destinations.Register.route) {
            RegisterScreen(
                navigateToLogin = { navController.navigate(Destinations.Login.route) },
                navigateToHome = { navController.navigate(Destinations.Pantalla1.route) },
            )
        }

        composable(Destinations.Pantalla1.route) {
            Pantalla1(navegarPantalla2 = { newText ->
                navController.navigate(
                    Destinations.Pantalla2.createRoute(
                        newText
                    )
                )
            }
            )
        }

        composable(
            Destinations.Pantalla2.route, arguments = listOf(navArgument("newText") {
                defaultValue = "Pantalla 2"
            })
        ) { navBackStackEntry ->
            var newText = navBackStackEntry.arguments?.getString("newText")
            requireNotNull(newText)
            Pantalla2(newText, darkMode)
        }

        composable(Destinations.Pantalla3.route) {
            Pantalla3()
        }
        composable(Destinations.Pantalla4.route) {
            val reservaViewModel: ReservaViewModel = hiltViewModel()
            Pantalla4(
                navController = navController,
                reservaViewModel = reservaViewModel
            )
        }
        composable(Destinations.Pantalla5.route) {
            Pantalla5()
        }

        composable(Destinations.SettingsScreen.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
            )
        }

        composable(Destinations.HomeScreen.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                navegarPantalla2 = { newText ->
                    navController.navigate(
                        Destinations.Pantalla2.createRoute(newText)
                    )
                },
                navController = navController,
                viewModel = viewModel,
            )
        }

        composable(Destinations.RecomScreen.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            RecomendacionScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }
        composable(Destinations.FavoritoMainSC.route){
            FavoritoMain(navegarEditarAct = {newText->
                navController.navigate(Destinations.ServicioFormSC.passId(newText))},
                navController =navController )
        }
        composable(Destinations.FavoritoServMainSC.route){
            FavoritoScreen(navController =navController )
        }

        composable(Destinations.PaqueteMainSC.route){
            PaqueteMain(
                navegarDetalle = { id ->
                    navController.navigate(Destinations.PaqueteDetalleMainSC.passId(id))
                },
                navegarActDetalle = { id ->
                    navController.navigate(Destinations.ActividadDetalleMainSC.passId(id))
                },
                navegarResena = { id ->
                    navController.navigate(Destinations.ResenaMainSC.passId(id))
                },
                navegarEditarAct = { paqueteJson ->
                    navController.navigate(Destinations.PaqueteFormSC.passId(paqueteJson))
                },
                navController = navController
            )
        }
        composable(Destinations.PaqueteFormSC.route, arguments =
            listOf(navArgument("prodId"){
                defaultValue="prodId"
            })){navBackStackEntry -> var
                prodId=navBackStackEntry.arguments?.getString("prodId")
            requireNotNull(prodId)
            PaqueteForm(text = prodId, darkMode = darkMode,
                navController=navController )
        }

        composable(Destinations.ProveedorMainSC.route){
            ProveedorMain(navegarEditarAct = {newText->
                navController.navigate(Destinations.ProveedorFormSC.passId(newText))},
                navController =navController )
        }
        composable(Destinations.ProveedorFormSC.route, arguments =
            listOf(navArgument("provId"){
                defaultValue="provId"
            })){navBackStackEntry -> var
                provId=navBackStackEntry.arguments?.getString("provId")
            requireNotNull(provId)
            ProveedorForm(text = provId, darkMode = darkMode,
                navController=navController )
        }

        composable(Destinations.ServicioMainSC.route){
            ServicioMain(navegarEditarAct = {newText->
                navController.navigate(Destinations.ServicioFormSC.passId(newText))},
                navController =navController )
        }
        composable(Destinations.ServicioFormSC.route, arguments =
            listOf(navArgument("servId"){
                defaultValue="servId"
            })){navBackStackEntry -> var
                servId=navBackStackEntry.arguments?.getString("servId")
            requireNotNull(servId)
            ServicioForm(text = servId, darkMode = darkMode,
                navController=navController )
        }

        composable(Destinations.ServicioAlimentacionMainSC.route){
            ServicioAlimentacionMain(navegarEditarAct = {newText->
                navController.navigate(Destinations.ServicioAlimentacionFormSC.passId(newText))},
                navController =navController )
        }
        composable(Destinations.ServicioAlimentacionFormSC.route, arguments =
            listOf(navArgument("servaliId"){
                defaultValue="servaliId"
            })){navBackStackEntry -> var
                servaliId=navBackStackEntry.arguments?.getString("servaliId")
            requireNotNull(servaliId)
            ServicioAlimentacionForm(text = servaliId, darkMode = darkMode,
                navController=navController )
        }

        composable(Destinations.ServicioArtesaniaMainSC.route){
            ServicioArtesaniaMain(navegarEditarAct = {newText->
                navController.navigate(Destinations.ServicioArtesaniaFormSC.passId(newText))},
                navController =navController )
        }
        composable(Destinations.ServicioArtesaniaFormSC.route, arguments =
            listOf(navArgument("servartId"){
                defaultValue="servartId"
            })){navBackStackEntry -> var
                servartId=navBackStackEntry.arguments?.getString("servartId")
            requireNotNull(servartId)
            ServicioArtesaniaForm(text = servartId, darkMode = darkMode,
                navController=navController )
        }

        composable(Destinations.ServicioHoteleraMainSC.route){
            ServicioHoteleraMain(navegarEditarAct = {newText->
                navController.navigate(Destinations.ServicioHoteleraFormSC.passId(newText))},
                navController =navController )
        }
        composable(Destinations.ServicioHoteleraFormSC.route, arguments =
            listOf(navArgument("servhotId"){
                defaultValue="servhotId"
            })){navBackStackEntry -> var
                servhotId=navBackStackEntry.arguments?.getString("servhotId")
            requireNotNull(servhotId)
            ServicioHoteleraForm(text = servhotId, darkMode = darkMode,
                navController=navController )
        }
        composable(
            route = Destinations.ResenaMainSC.route,
            arguments = listOf(
                navArgument("packId") {
                    type = NavType.StringType // Especifica el tipo explícitamente
                }
            )
        ) { navBackStackEntry ->
            val packId = navBackStackEntry.arguments?.getString("packId")
            requireNotNull(packId)
            ResenaMain(
                text = packId,
                navegarEditarAct = { resId, packIdValue ->
                    navController.navigate(Destinations.ResenaFormSC.passId(resId, packIdValue))
                },
                navController = navController
            )
        }
        composable(
            route = Destinations.ResenaFormSC.route,
            arguments = listOf(
                navArgument("resId") {
                    defaultValue = "resId"
                    type = NavType.StringType
                },
                navArgument("packId") {
                    defaultValue = "0"
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val resId = navBackStackEntry.arguments?.getString("resId")
            val packId = navBackStackEntry.arguments?.getString("packId")
            requireNotNull(resId)
            requireNotNull(packId)

            ResenaForm(
                text = resId,
                packId = packId,
                darkMode = darkMode,
                navController = navController
            )
        }
        composable(
            route = Destinations.PaqueteDetalleMainSC.route,
            arguments = listOf(
                navArgument("packId") {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val packId = navBackStackEntry.arguments?.getString("packId")
            requireNotNull(packId)

            PaqueteDetalleMain(
                text = packId,
                navegarEditarAct = { detId, packIdValue ->
                    navController.navigate(Destinations.PaqueteDetalleFormSC.passId(detId, packIdValue))
                },
                navController = navController
            )
        }
        composable(
            route = Destinations.PaqueteDetalleFormSC.route,
            arguments = listOf(
                navArgument("detId") {
                    defaultValue = "detId"
                    type = NavType.StringType
                },
                navArgument("packId") {
                    defaultValue = "0"
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val detId = navBackStackEntry.arguments?.getString("detId")
            val packId = navBackStackEntry.arguments?.getString("packId")
            requireNotNull(detId)
            requireNotNull(packId)

            PaqueteDetalleForm(
                text = detId,
                packId = packId,
                darkMode = darkMode,
                navController = navController
            )
        }
        composable(Destinations.ActividadMainSC.route){
            ActividadMain(navegarEditarAct = {newText->
                navController.navigate(Destinations.ActividadFormSC.passId(newText))},
                navController =navController )
        }
        composable(Destinations.ActividadFormSC.route, arguments =
            listOf(navArgument("actvId"){
                defaultValue="actvId"
            })){navBackStackEntry -> var
                actvId=navBackStackEntry.arguments?.getString("actvId")
            requireNotNull(actvId)
            ActividadForm(text = actvId, darkMode = darkMode,
                navController=navController )
        }
        composable(
            route = Destinations.ActividadDetalleMainSC.route,
            arguments = listOf(
                navArgument("packId") {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val packId = navBackStackEntry.arguments?.getString("packId")
            requireNotNull(packId)

            ActividadDetalleMain(
                text = packId,
                navegarEditarAct = { detId, packIdValue ->
                    navController.navigate(Destinations.ActividadDetalleFormSC.passId(detId, packIdValue))
                },
                navController = navController
            )
        }
        composable(
            route = Destinations.ActividadDetalleFormSC.route,
            arguments = listOf(
                navArgument("detId") {
                    defaultValue = "detId"
                    type = NavType.StringType
                },
                navArgument("packId") {
                    defaultValue = "0"
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val detId = navBackStackEntry.arguments?.getString("detId")
            val packId = navBackStackEntry.arguments?.getString("packId")
            requireNotNull(detId)
            requireNotNull(packId)

            ActividadDetalleForm(
                text = detId,
                packId = packId,
                darkMode = darkMode,
                navController = navController
            )
        }
        composable(Destinations.DestinoMainSC.route) {
            DestinoMain(
                navController = navController,
                navegarEditarAct = { id ->
                    navController.navigate("destinoForm?destId=$id") // Por ejemplo
                }
            )
        }
        composable(Destinations.DestinoFormSC.route, arguments =
            listOf(navArgument("destId"){
                defaultValue="destId"
            })){navBackStackEntry -> var
                destId=navBackStackEntry.arguments?.getString("destId")
            requireNotNull(destId)
            DestinoForm(text = destId, darkMode = darkMode,
                navController=navController )
        }
        composable("detallePaquete/{idPaquete}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("idPaquete")?.toLongOrNull()
            if (id != null) {
                DetalleScreen(idPaquete = id, navController=navController)
            }


        }

        composable(
            route = "actividades/{idPaquete}",
            arguments = listOf(navArgument("idPaquete") {
                type = NavType.LongType
            })
        ) { backStackEntry ->
            val idPaquete = backStackEntry.arguments?.getLong("idPaquete") ?: -1L
            Actividades(idPaquete = idPaquete,
                navController = navController)
        }

        composable("PaqueteReserva/{idPaquete}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("idPaquete")?.toLongOrNull()
            if (id != null) {
                Reservas(idPaquete = id, navController=navController)
            }

        }

        //PANTALLAS DE PERFIL

        composable(Destinations.PerfilScreen.route) {
            Perfil(
                navergarRegistro = { navController.navigate(Destinations.Register.route) },
                navController = navController
            )
        }
        // Cambiar esta línea:
        composable("puntos") {
            val servicioViewModel: ServicioMainViewModel = hiltViewModel()
            PuntosScreen(
                navController = navController,
                servicioRepository = servicioViewModel.repository
            )
        }

        composable(Destinations.Language.route) { SettingsScreen() }
        composable(Destinations.Currency.route) { CurrencyScreen(navController) }
        composable(Destinations.Plans.route) { PlanesScreen() }
        composable(Destinations.Contact.route) { ContactoScreen() }
        composable(Destinations.Web.route) {
            val context = LocalContext.current

            // Lanzar el intent al cargar el composable
            LaunchedEffect (Unit) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                context.startActivity(intent)
            }

            // Puedes mostrar un texto de redirección o pantalla en blanco
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Redireccionando a Google...")
            }
        }
        composable(Destinations.Privacy.route) { PrivacyScreen() }
        composable(Destinations.Terms.route) { CondicionesScreen() }

        composable(
            route = Destinations.CarritoMainSC.route,
            arguments = listOf(
                navArgument("carId") {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val carId = navBackStackEntry.arguments?.getString("carId")
            requireNotNull(carId)

            CarritoMain(
                text = carId,
                navegarEditarAct = { carId, refId, tipo ->
                    navController.navigate(
                        Destinations.CarritoFormSC.passId(carId, refId, tipo)
                    )
                },
                navController = navController
            )
        }
        composable(Destinations.CarritoFormSC.route, arguments =
            listOf(navArgument("servId"){
                defaultValue="servId"
            },navArgument("refId"){
                defaultValue="refId"
            },navArgument("tipo"){
                defaultValue="tipo"
            }
            )){navBackStackEntry ->
            var servId=navBackStackEntry.arguments?.getString("servId")
            var refId=navBackStackEntry.arguments?.getString("refId")
            var tipo=navBackStackEntry.arguments?.getString("tipo")
            requireNotNull(servId)
            requireNotNull(refId)
            requireNotNull(tipo)
            CarritoForm(text = servId, text1 = refId, text2 = tipo, darkMode = darkMode,
                navController=navController )
        }
        //composable(Destinations.ReservaCarritoSC.route) { ReservaCarritoScreen(navController) }


        composable("ReservaCarrito/{idCarrito}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("idCarrito")?.toLongOrNull()
            if (id != null) {
                ReservaCarritoScreen(idCarrito = id, navController=navController)
            }

        }
        composable(
            "detalleServicio/{idServicio}/{tipo}"
        ) { backStackEntry ->
            val idServicio = backStackEntry.arguments?.getString("idServicio")?.toLong() ?: return@composable
            val tipo = backStackEntry.arguments?.getString("tipo") ?: return@composable
            ServicioDetalle(idServicio,navController)
        }
        composable(Destinations.chat.route) {
            ChatScreen(viewModel = hiltViewModel<ChatViewModel>())
        }
        // NUEVAS RUTAS PARA QR Y BLOCKCHAIN
        composable(Destinations.QrScannerScreen.route) {
            QrScannerScreen(
                navController = navController
            )
        }

        composable(
            route = Destinations.VerificacionArtesanoScreen.route,
            arguments = listOf(
                navArgument("codigoVerificacion") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val codigoVerificacion = backStackEntry.arguments?.getString("codigoVerificacion") ?: ""
            VerificacionArtesanoScreen(
                codigoVerificacion = codigoVerificacion,
                navController = navController
            )
        }

        composable(
            route = Destinations.QrGenerationScreen.route,
            arguments = listOf(
                navArgument("idArtesania") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val idArtesania = backStackEntry.arguments?.getLong("idArtesania") ?: 0L
            QrGenerationScreen(
                idArtesania = idArtesania,
                navController = navController
            )
        }

        composable(Destinations.QrManagementScreen.route) {
            QrManagementScreen(
                navController = navController
            )
        }
    }

}
