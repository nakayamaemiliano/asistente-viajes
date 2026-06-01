package com.asistente_viajes_api.project.repository;

import com.asistente_viajes_api.project.entity.ConsultaViaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IConsultaViajeRepository extends JpaRepository<ConsultaViaje,Long> {
}
