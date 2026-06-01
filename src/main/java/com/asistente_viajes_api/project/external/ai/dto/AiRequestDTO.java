package com.asistente_viajes_api.project.external.ai.dto;

import javax.swing.text.AbstractDocument;
import java.util.List;

public record AiRequestDTO(
        List<Content> contents
) {
    public record Content(
            List<Part> parts
    ) {
    }

    public record Part(
            String text
    ) {
    }
}
