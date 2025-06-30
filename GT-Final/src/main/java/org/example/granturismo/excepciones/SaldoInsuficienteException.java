package org.example.granturismo.excepciones;

public class SaldoInsuficienteException extends RuntimeException {

  public SaldoInsuficienteException(String message) {
    super(message);
  }

  public SaldoInsuficienteException(String message, Throwable cause) {
    super(message, cause);
  }
}