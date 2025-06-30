package pe.edu.upeu.granturismojpc.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange

import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destinations(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Login : Destinations("login", "Login", Icons.Filled.Settings)
    object Register : Destinations("register", "Registro", Icons.Filled.Favorite)
    object Pantalla1 : Destinations("pantalla1", "Pantalla 1", Icons.Filled.Home)
    object Pantalla2 : Destinations("pantalla2/?newText={newText}", "Pantalla 2", Icons.Filled.Settings) {
        fun createRoute(newText: String) = "pantalla2/?newText=$newText"
    }

    object Pantalla3 : Destinations("pantalla3", "Pantalla 3", Icons.Filled.Favorite)
    object Pantalla4 : Destinations("pantalla4", "Pantalla 4x", Icons.Filled.Face)
    object Pantalla5 : Destinations("pantalla5", "Pantalla 5x", Icons.Filled.AccountCircle)

    object SettingsScreen : Destinations("ajustes", "Ajustes", Icons.Filled.Settings)

    object HomeScreen : Destinations("pantallahome", "Pantalla Home", Icons.Filled.Home)
    object RecomScreen : Destinations("recomendacion", "Recomendaciones", Icons.Filled.Star)

    object FavoritoMainSC: Destinations("favorito","Adm. Favorito", Icons.Filled.DateRange)
    object FavoritoServMainSC: Destinations("favorito2","Adm. Favorito", Icons.Filled.DateRange)


    object PaqueteMainSC: Destinations("paquetemain","Asociaciones", Icons.Filled.DateRange)
        object PaqueteFormSC:
            Destinations("paqueteForm?prodId={prodId}", "Form Paquete",
                Icons.Filled.Add){
            fun passId(prodId:String?):String{
                return "paqueteForm?prodId=$prodId"
            }
        }

    object ProveedorMainSC: Destinations("proveedormain","Proveedores", Icons.Filled.DateRange)
    object ProveedorFormSC:
        Destinations("proveedorForm?provId={provId}", "Form Proveedor",
            Icons.Filled.Add){
        fun passId(provId:String?):String{
            return "proveedorForm?provId=$provId"
        }
    }
    object ServicioMainSC: Destinations("serviciomain","Servicios (Todos)", Icons.Filled.DateRange)
    object ServicioFormSC:
        Destinations("servicioForm?servId={servId}", "Form Servicio",
            Icons.Filled.Add){
        fun passId(servId:String?):String{
            return "servicioForm?servId=$servId"
        }
    }
    object ServicioAlimentacionMainSC: Destinations("servicioalimentacionmain","S. de Alimentación", Icons.Filled.DateRange)
    object ServicioAlimentacionFormSC:
        Destinations("servicioAlimentacionForm?servaliId={servaliId}", "Form Servicio Alimentacion",
            Icons.Filled.Add){
        fun passId(servaliId:String?):String{
            return "servicioAlimentacionForm?servaliId=$servaliId"
        }
    }

    object ServicioArtesaniaMainSC: Destinations("ServicioArtesaniamain","S. de Artesanía", Icons.Filled.DateRange)
    object ServicioArtesaniaFormSC:
        Destinations("ServicioArtesaniaForm?servartId={servartId}", "Form Servicio Artesania",
            Icons.Filled.Add){
        fun passId(servartId:String?):String{
            return "ServicioArtesaniaForm?servartId=$servartId"
        }
    }

    object ServicioHoteleraMainSC: Destinations("ServicioHoteleraMain","S. de Hospedaje", Icons.Filled.DateRange)
    object ServicioHoteleraFormSC:
        Destinations("ServicioHoteleraForm?servhotId={servhotId}", "Form Servicio Hotelería",
            Icons.Filled.Add){
        fun passId(servhotId:String?):String{
            return "ServicioHoteleraForm?servhotId=$servhotId"
        }
    }

    object ResenaMainSC:
        Destinations("ResenaMain/{packId}","Reseñas",
            Icons.Filled.DateRange){
        fun passId(packId: String): String {
            return "ResenaMain/$packId"
        }
    }
    object ResenaFormSC:
        Destinations("ResenaForm?resId={resId}&packId={packId}", "Form Reseñas",
            Icons.Filled.Add){
        fun passId(resId:String?, packId: String? = "0"):String{
            return "ResenaForm?resId=$resId&packId=$packId"
        }
    }
    object PaqueteDetalleMainSC:
        Destinations("PaqueteDetalleMain/{packId}","Adm. Detalles",
            Icons.Filled.DateRange){
        fun passId(packId: String): String {
            return "PaqueteDetalleMain/$packId"
        }
    }
    object PaqueteDetalleFormSC:
        Destinations("PaqueteDetalleForm?detId={detId}&packId={packId}", "Form Detalles",
            Icons.Filled.Add){
        fun passId(detId: String?, packId: String? = "0"): String {
            return "PaqueteDetalleForm?detId=$detId&packId=$packId"
        }
    }

    object ActividadMainSC: Destinations("actividadmain","Actividades", Icons.Filled.DateRange)
    object ActividadFormSC:
        Destinations("actividadForm?actvId={actvId}", "Form Actividad",
            Icons.Filled.Add){
        fun passId(actvId:String?):String{
            return "actividadForm?actvId=$actvId"
        }
    }
    object ActividadDetalleMainSC:
        Destinations("ActividadDetalleMain/{packId}","Adm. Detalles",
            Icons.Filled.DateRange){
        fun passId(packId: String): String {
            return "ActividadDetalleMain/$packId"
        }
    }
    object ActividadDetalleFormSC:
        Destinations("ActividadDetalleForm?detId={detId}&packId={packId}", "Form Detalles",
            Icons.Filled.Add){
        fun passId(detId:String?, packId: String? = "0"):String{
            return "ActividadDetalleForm?detId=$detId&packId=$packId"
        }
    }

    object DestinoMainSC : Destinations("destinomainsc", "Destinos", Icons.Filled.DateRange)
    object DestinoFormSC : Destinations(
        "destinoForm?destId={destId}", "Form Destino", Icons.Filled.Add
    ) {
        fun passId(destId: String?): String {
            return "destinoForm?destId=$destId"
        }
    }



    //PERFIL

    object PerfilScreen: Destinations("perfil","Perfil", Icons.Filled.DateRange)

    object Language: Destinations("idioma","Idioma", Icons.Filled.DateRange)
    object Currency: Destinations("currency","Currency", Icons.Filled.DateRange)
    object Plans: Destinations("planes","Todos mis planes", Icons.Filled.DateRange)
    object Contact: Destinations("contacto","Contactenos", Icons.Filled.DateRange)
    object Web: Destinations("web","Pagina web", Icons.Filled.DateRange)
    object Privacy: Destinations("privacy","Políticas de privacidad", Icons.Filled.DateRange)
    object Terms: Destinations("condiciones","Condiciones de uso", Icons.Filled.DateRange)

    object CarritoMainSC:
        Destinations("CarritoMain/{carId}","Adm. Detalles",
            Icons.Filled.DateRange){
        fun passId(carId: String): String {
            return "CarritoMain/$carId"
        }
    }    object CarritoFormSC:
        Destinations("carritoForm?servId={servId}&refId={refId}&tipo={tipo}", "Form Carrito",
            Icons.Filled.Add){
        fun passId(servId:String?,refId:String?,tipo:String?):String{
            return "carritoForm?servId=$servId&refId=$refId&tipo=$tipo"
        }
    }
    object ReservaCarritoSC: Destinations("reserva","Reserva", Icons.Filled.DateRange)

    object chat : Destinations("chat", "ChatBot", Icons.Filled.Face)
    // NUEVAS RUTAS PARA QR Y BLOCKCHAIN
    object QrScannerScreen : Destinations(
        "qr_scanner",
        "Escanear QR",
        Icons.Filled.QrCodeScanner
    )

    object VerificacionArtesanoScreen : Destinations(
        "verificacion_artesano/{codigoVerificacion}",
        "Verificar Artesano",
        Icons.Filled.Verified
    ) {
        fun createRoute(codigoVerificacion: String): String {
            return "verificacion_artesano/$codigoVerificacion"
        }
    }

    object QrGenerationScreen : Destinations(
        "qr_generation/{idArtesania}",
        "Generar QR",
        Icons.Filled.QrCode
    ) {
        fun createRoute(idArtesania: Long): String {
            return "qr_generation/$idArtesania"
        }
    }

    object QrManagementScreen : Destinations(
        "qr_management",
        "Gestión QR",
        Icons.Filled.ManageSearch
    )
}