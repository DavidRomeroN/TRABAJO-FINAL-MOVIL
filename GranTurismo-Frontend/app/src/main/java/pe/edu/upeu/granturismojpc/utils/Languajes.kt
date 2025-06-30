package pe.edu.upeu.granturismojpc.utils

fun LanguajesD(code:String):String{
    var lang: String = ""
    when(code){
        "en"->lang="English"
        "es"->lang="Español"
    }
    return lang
}