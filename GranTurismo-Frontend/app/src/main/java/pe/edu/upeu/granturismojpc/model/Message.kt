package pe.edu.upeu.granturismojpc.model

data class Message(
    var statusCode: Long,
    var datetime: String,
    var message: String,
    var details: String
)
