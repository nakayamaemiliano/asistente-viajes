package com.asistente_viajes_api.project.external.ai;

import com.asistente_viajes_api.project.exception.BadRequestException;
import com.asistente_viajes_api.project.external.ai.dto.AiRecommendationResponseDTO;
import com.asistente_viajes_api.project.external.ai.dto.AiRequestDTO;
import com.asistente_viajes_api.project.external.ai.dto.AiResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class AiClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public AiClient(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model,
            @Value("${gemini.base-url}") String baseUrl
    ) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public AiRecommendationResponseDTO generarRecomendacion(String prompt) {

        if (!StringUtils.hasText(apiKey)) {
            throw new BadRequestException("Falta configurar la variable de entorno GEMINI_API_KEY");
        }

        try {
            AiRequestDTO request = new AiRequestDTO(
                    List.of(
                            new AiRequestDTO.Content(
                                    List.of(
                                            new AiRequestDTO.Part(prompt)
                                    )
                            )
                    )
            );

            AiResponseDTO response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/models/{model}:generateContent")
                            .queryParam("key", apiKey)
                            .build(model)
                    )
                    .body(request)
                    .retrieve()
                    .body(AiResponseDTO.class);

            String textoIA = extraerTexto(response);

            return objectMapper.readValue(
                    limpiarJson(textoIA),
                    AiRecommendationResponseDTO.class
            );

        } catch (RestClientException ex) {
            throw new BadRequestException("Error al consultar Gemini: " + ex.getMessage());
        } catch (Exception ex) {
            throw new BadRequestException("Error al procesar la respuesta de Gemini: " + ex.getMessage());
        }
    }

    private String extraerTexto(AiResponseDTO response) {

        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new BadRequestException("La respuesta de Gemini no contiene candidatos");
        }

        AiResponseDTO.Candidate candidate = response.candidates().get(0);

        if (candidate.content() == null ||
                candidate.content().parts() == null ||
                candidate.content().parts().isEmpty()) {
            throw new BadRequestException("La respuesta de Gemini no contiene contenido");
        }

        String text = candidate.content().parts().get(0).text();

        if (!StringUtils.hasText(text)) {
            throw new BadRequestException("La respuesta de Gemini no contiene texto");
        }

        return text;
    }

    private String limpiarJson(String texto) {
        return texto
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
}
