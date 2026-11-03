package com.pejibayeraEste.repository;

import com.pejibayeraEste.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByEmpresa_IdEmpresa(Integer idEmpresa);
}
