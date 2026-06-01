package com.asistente_viajes_api.project.repository;

import com.asistente_viajes_api.project.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IClienteRepository extends JpaRepository<Cliente,Long> {
    boolean existsByEmail(String email);
}
