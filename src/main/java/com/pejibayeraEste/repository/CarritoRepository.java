package com.pejibayeraEste.repository;

import com.pejibayeraEste.domain.Carrito;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Optional<Carrito> findByUsuario_IdUsuario(Integer idUsuario);

    boolean existsByUsuario_IdUsuario(Integer idUsuario);
}
