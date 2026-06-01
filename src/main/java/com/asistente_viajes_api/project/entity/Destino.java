package com.asistente_viajes_api.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.web.WebProperties;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Destino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long destinoId;

    @Column(nullable = false)
    private String ciudad;

    @Column(nullable = false)
    private String pais;

    @Column(nullable = false)
    private String codigoPais;

    private String moneda;

    private String idiomaPrincipal;

    private String region;

    private Double latitud;

    @Column(nullable = false)
    private Double longitud;


}
