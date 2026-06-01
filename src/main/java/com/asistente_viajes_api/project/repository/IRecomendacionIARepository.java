package com.asistente_viajes_api.project.repository;

import com.asistente_viajes_api.project.entity.ConsultaViaje;
import com.asistente_viajes_api.project.entity.RecomendacionIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRecomendacionIARepository extends JpaRepository<RecomendacionIA, Long> {

    Optional<RecomendacionIA> findByConsultaViaje(ConsultaViaje consultaViaje);

    boolean existsByConsultaViaje(ConsultaViaje consultaViaje);
}
