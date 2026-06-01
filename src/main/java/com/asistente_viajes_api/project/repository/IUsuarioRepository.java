package com.asistente_viajes_api.project.repository;

import com.asistente_viajes_api.project.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario,Long> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);
}
