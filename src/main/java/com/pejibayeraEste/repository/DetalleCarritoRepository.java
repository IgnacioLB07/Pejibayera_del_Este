package com.pejibayeraEste.repository;

import com.pejibayeraEste.domain.DetalleCarrito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Integer> {

    List<DetalleCarrito> findByCarrito_IdCarrito(Integer idCarrito);

    Optional<DetalleCarrito> findByCarrito_IdCarritoAndProducto_IdProducto(
            Integer idCarrito, Integer idProducto);
}
