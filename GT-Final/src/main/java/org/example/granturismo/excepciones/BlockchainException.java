package org.example.granturismo.excepciones;

/**
 * Excepción para encapsular errores durante la interacción con la blockchain
 */
public class BlockchainException extends RuntimeException {

    public BlockchainException(String message) {
        super(message);
    }

    public BlockchainException(String message, Throwable cause) {
        super(message, cause);
    }
}
