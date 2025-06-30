package org.example.granturismo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GranTurismoApplication {

    public static void main(String[] args) {


        // AÑADE ESTA LÍNEA TEMPORALMENTE PARA DEPURAR
        String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (credentialsPath != null && !credentialsPath.isEmpty()) {
            System.out.println("Variable GOOGLE_APPLICATION_CREDENTIALS encontrada: " + credentialsPath);
        } else {
            System.err.println("ADVERTENCIA: GOOGLE_APPLICATION_CREDENTIALS NO ENCONTRADA o vacía.");
        }
        // FIN DE LA DEPURACIÓN TEMPORAL

        SpringApplication.run(GranTurismoApplication.class, args);

    }

}
