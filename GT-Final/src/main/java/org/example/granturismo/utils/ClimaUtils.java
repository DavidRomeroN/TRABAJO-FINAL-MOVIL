package org.example.granturismo.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClimaUtils {

    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

    public static String obtenerClima(double lat, double lon) {
        try {
            String url = API_URL +
                    "?latitude=" + lat +
                    "&longitude=" + lon +
                    "&daily=weathercode,temperature_2m_max,precipitation_sum" +
                    "&timezone=auto";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
