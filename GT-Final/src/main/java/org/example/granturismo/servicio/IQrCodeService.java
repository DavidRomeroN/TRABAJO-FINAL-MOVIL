package org.example.granturismo.servicio;

import org.example.granturismo.excepciones.QrGenerationException;

/**
 * Servicio para generar códigos QR
 */
public interface IQrCodeService {

    /**
     * Genera una imagen QR para los datos proporcionados y la hace accesible mediante una URL
     * @param dataToEncode Datos a codificar en el QR
     * @param width Ancho de la imagen
     * @param height Alto de la imagen
     * @return URL donde se puede acceder a la imagen QR
     * @throws QrGenerationException Si ocurre un error al generar el QR
     */
    String generarQrCodeUrl(String dataToEncode, int width, int height) throws QrGenerationException;

    /**
     * Genera una imagen QR con dimensiones por defecto
     * @param dataToEncode Datos a codificar en el QR
     * @return URL donde se puede acceder a la imagen QR
     * @throws QrGenerationException Si ocurre un error al generar el QR
     */
    String generarQrCodeUrl(String dataToEncode) throws QrGenerationException;

    /**
     * Genera la imagen QR como array de bytes
     * @param dataToEncode Datos a codificar
     * @param width Ancho
     * @param height Alto
     * @return Array de bytes de la imagen
     * @throws QrGenerationException Si ocurre un error
     */
    byte[] generarQrCodeBytes(String dataToEncode, int width, int height) throws QrGenerationException;

    /**
     * Elimina una imagen QR del almacenamiento
     * @param filename Nombre del archivo a eliminar
     * @return true si se eliminó correctamente
     */
    boolean eliminarQrCode(String filename);
}