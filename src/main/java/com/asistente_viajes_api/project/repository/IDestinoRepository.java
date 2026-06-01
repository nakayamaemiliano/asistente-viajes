package com.asistente_viajes_api.project.repository;

import com.asistente_viajes_api.project.entity.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDestinoRepository  extends JpaRepository<Destino,Long> {

    boolean existsByCiudadIgnoreCaseAndPaisIgnoreCase(String ciudad, String pais);
}
