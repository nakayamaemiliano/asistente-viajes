package com.asistente_viajes_api.project.external.ai.dto;

import java.util.List;

public record AiResponseDTO(
        List<Candidate> candidates
) {
    public record Candidate(
            Content content
    ) {
    }

    public record Content(
            List<Part> parts
    ) {
    }

    public record Part(
            String text
    ) {
    }
}
