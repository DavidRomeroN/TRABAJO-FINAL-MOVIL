package org.example.granturismo.servicio.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.granturismo.servicio.IClimaServicio;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class ClimaServicio implements IClimaServicio {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String obtenerResumenClimaParaDestino(double latitud, double longitud) {
        try {
            String url = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&daily=temperature_2m_max,precipitation_sum&timezone=auto",
                    latitud, longitud
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = mapper.readTree(response.body());

            JsonNode daily = root.path("daily");
            if (!daily.has("temperature_2m_max") || !daily.has("precipitation_sum")) {
                return "Error al obtener datos del clima";
            }

            double temperatura = daily.get("temperature_2m_max").get(0).asDouble();
            double lluvia = daily.get("precipitation_sum").get(0).asDouble();

            if (temperatura >= 1 && temperatura <= 35 && lluvia < 0.5) {
                return String.format("Clima previsto: Buen clima. Temperatura: %.1f°C", temperatura);
            } else {
                return String.format("Clima previsto: Posible mal clima. Lluvia: %.1fmm", lluvia);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al obtener clima";
        }
    }

    @Override
    public Map<String, Object> obtenerClimaDeDia(double lat, double lon, int dia) {
        Map<String, Object> data = new HashMap<>();
        try {
            System.out.println("🌍 LLAMADA A API DE CLIMA:");
            System.out.printf("   Latitud: %.6f%n", lat);
            System.out.printf("   Longitud: %.6f%n", lon);
            System.out.println("   Día solicitado: " + dia);

            String url = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f" +
                            "&daily=weathercode,temperature_2m_max,precipitation_sum" +
                            "&forecast_days=7&timezone=auto",
                    lat, lon
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("❌ Error HTTP: " + response.statusCode());
                System.err.println("Response body: " + response.body());
                data.put("error", "Error del servidor de clima: " + response.statusCode());
                return data;
            }

            String json = response.body();
            System.out.println("📦 JSON recibido:");
            System.out.println(json);

            JsonNode root = mapper.readTree(json);

            if (root.has("error")) {
                String errorMsg = root.get("error").asText();
                System.err.println("❌ Error de la API: " + errorMsg);
                data.put("error", "Error de la API: " + errorMsg);
                return data;
            }

            JsonNode daily = root.path("daily");

            if (!daily.has("temperature_2m_max") || !daily.has("precipitation_sum") ||
                    !daily.has("weathercode")) {
                System.err.println("❌ Datos incompletos. Estructura recibida: " + daily.toString());
                data.put("error", "Datos incompletos del proveedor");
                return data;
            }

            JsonNode temps = daily.get("temperature_2m_max");
            JsonNode lluvias = daily.get("precipitation_sum");
            JsonNode weathercodes = daily.get("weathercode");

            System.out.println("📊 Temperaturas disponibles: " + temps);
            System.out.println("📊 Precipitaciones: " + lluvias);
            System.out.println("📊 Códigos de clima: " + weathercodes);

            if (temps.size() <= dia || lluvias.size() <= dia || weathercodes.size() <= dia) {
                data.put("error", "Día fuera de rango del pronóstico (día " + dia + ", disponibles: " + temps.size() + ")");
                return data;
            }

            double temp = temps.get(dia).asDouble();
            double lluvia = lluvias.get(dia).asDouble();
            int codigo = weathercodes.get(dia).asInt();

            System.out.printf("🌡️ Temperatura del día %d: %.1f°C%n", dia, temp);
            System.out.printf("🌧️ Lluvia: %.2f mm%n", lluvia);
            System.out.println("🌀 Código del clima: " + codigo);

            boolean ideal = (temp >= 13 && temp <= 30) && (lluvia < 2.5) && (codigo < 70);

            String mensaje;
            if (!ideal) {
                if (lluvia >= 2.5) {
                    mensaje = "lluvia intensa";
                } else if (codigo >= 70 && codigo < 80) {
                    mensaje = "nublado";
                } else {
                    mensaje = "clima no ideal";
                }
            } else {
                mensaje = "buen clima";
            }

            data.put("ideal", ideal);
            data.put("temperatura", temp);
            data.put("mensaje", mensaje);

            JsonNode fechas = daily.get("time");
            if (fechas != null && fechas.size() > dia) {
                data.put("fecha", fechas.get(dia).asText());
            }

        } catch (Exception e) {
            System.err.println("❌ Excepción capturada: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            data.put("error", "Error al obtener clima: " + e.getMessage());
        }

        return data;
    }
}
