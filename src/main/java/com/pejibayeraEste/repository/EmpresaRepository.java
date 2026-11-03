package com.pejibayeraEste.repository;

import com.pejibayeraEste.domain.Empresa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    Optional<Empresa> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Empresa> findByActivoTrue();
}
