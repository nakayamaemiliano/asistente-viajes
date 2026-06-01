package com.asistente_viajes_api.project.external.countries;

import com.asistente_viajes_api.project.exception.BadRequestException;
import com.asistente_viajes_api.project.exception.ResourceNotFoundException;
import com.asistente_viajes_api.project.external.countries.dto.CountryInfoResponseDTO;
import com.asistente_viajes_api.project.external.countries.dto.RestCountryResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Component
public class CountryClient {

    private  RestClient restClient;


    public CountryClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://restcountries.com/v3.1")
                .build();
    }
    public CountryInfoResponseDTO buscarPais(String pais) {
        try {
            RestCountryResponseDTO[] response = restClient.get()
                    .uri("/name/{pais}", pais)
                    .retrieve()
                    .body(RestCountryResponseDTO[].class);

            if (response == null || response.length == 0) {
                throw new ResourceNotFoundException("No se encontró información para el país: " + pais);
            }

            RestCountryResponseDTO country = Arrays.stream(response)
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró información para el país: " + pais));

            return toCountryInfoResponse(country);

        } catch (RestClientResponseException ex) {

            if (ex.getStatusCode().value() == HttpStatus.NOT_FOUND.value()) {
                throw new ResourceNotFoundException("No se encontró información para el país: " + pais);
            }

            throw new BadRequestException("Error al consultar REST Countries: " + ex.getMessage());

        } catch (RestClientException ex) {
            throw new BadRequestException("No se pudo conectar con REST Countries");
        }
    }

    private CountryInfoResponseDTO toCountryInfoResponse(RestCountryResponseDTO country) {
        return new CountryInfoResponseDTO(
                obtenerNombre(country),
                obtenerCapital(country.capital()),
                country.region(),
                country.cca2(),
                obtenerMonedas(country.currencies()),
                obtenerIdiomas(country.languages())
        );
    }

    private String obtenerNombre(RestCountryResponseDTO country) {
        if (country.name() == null) {
            return null;
        }

        return country.name().common();
    }

    private String obtenerCapital(List<String> capitales) {
        if (capitales == null || capitales.isEmpty()) {
            return null;
        }

        return capitales.get(0);
    }

    private List<String> obtenerMonedas(Map<String, RestCountryResponseDTO.Currency> currencies) {
        if (currencies == null || currencies.isEmpty()) {
            return List.of();
        }

        return currencies.values()
                .stream()
                .map(RestCountryResponseDTO.Currency::name)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<String> obtenerIdiomas(Map<String, String> languages) {
        if (languages == null || languages.isEmpty()) {
            return List.of();
        }

        return languages.values()
                .stream()
                .filter(Objects::nonNull)
                .toList();
    }
}
