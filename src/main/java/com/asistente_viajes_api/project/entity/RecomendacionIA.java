package com.asistente_viajes_api.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecomendacionIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recomendacionIaId;

    @OneToOne
    @JoinColumn(name = "consulta_viaje_id", nullable = false, unique = true)
    private ConsultaViaje consultaViaje;

    @Lob
    @Column(nullable = false ,columnDefinition = "TEXT")
    private String resumen;

    @Lob
    @Column(nullable = false,columnDefinition = "TEXT")
    private String recomendaciones;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String advertencias;


    @Column(nullable = false)
    private LocalDateTime fechaGeneracion;


}
