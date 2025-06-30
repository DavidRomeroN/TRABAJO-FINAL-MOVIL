package org.example.granturismo.modelo;

public class ChatMessage {
    private String remitente;
    private String contenido;

    public ChatMessage() {}

    public ChatMessage(String remitente, String contenido) {
        this.remitente = remitente;
        this.contenido = contenido;
    }

    public String getRemitente() {
        return remitente;
    }

    public void setRemitente(String remitente) {
        this.remitente = remitente;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}