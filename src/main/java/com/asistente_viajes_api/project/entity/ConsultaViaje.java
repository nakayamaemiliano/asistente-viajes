package com.asistente_viajes_api.project.entity;

import com.asistente_viajes_api.project.enums.Estado;
import com.asistente_viajes_api.project.enums.Presupuesto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConsultaViaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long consultaViajeId;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Destino destino;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Integer cantidadPersonas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Presupuesto presupuesto;

    @Column(nullable = false)
    private String intereses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;




}
