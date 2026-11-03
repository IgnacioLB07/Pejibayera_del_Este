package com.pejibayeraEste.repository;

import com.pejibayeraEste.domain.Permiso;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {

    Optional<Permiso> findByNombre(String nombre);
}
