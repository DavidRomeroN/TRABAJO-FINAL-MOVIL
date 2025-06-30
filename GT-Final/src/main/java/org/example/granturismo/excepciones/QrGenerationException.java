package org.example.granturismo.excepciones;

/**
 * Excepción para errores al generar o almacenar el Código QR
 */
public class QrGenerationException extends RuntimeException {

  public QrGenerationException(String message) {
    super(message);
  }

  public QrGenerationException(String message, Throwable cause) {
    super(message, cause);
  }
}