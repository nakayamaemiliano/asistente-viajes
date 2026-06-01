package com.asistente_viajes_api.project.external.weather;

import com.asistente_viajes_api.project.exception.BadRequestException;
import com.asistente_viajes_api.project.external.weather.dto.OpenMeteoResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class WeatherClient {
    private final RestClient restClient;

    public WeatherClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://api.open-meteo.com")
                .build();
    }

    public OpenMeteoResponseDTO obtenerClimaActual(Double latitud, Double longitud) {
        try {
            OpenMeteoResponseDTO response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitud)
                            .queryParam("longitude", longitud)
                            .queryParam(
                                    "current",
                                    "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m,wind_direction_10m"
                            )
                            .queryParam("timezone", "auto")
                            .build()
                    )
                    .retrieve()
                    .body(OpenMeteoResponseDTO.class);

            if (response == null || response.current() == null) {
                throw new BadRequestException("No se pudo obtener el clima actual para las coordenadas indicadas");
            }

            return response;

        } catch (RestClientException ex) {
            throw new BadRequestException("Error al consultar Open-Meteo");
        }
    }
}
