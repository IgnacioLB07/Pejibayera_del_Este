package com.pejibayeraEste.repository;

import com.pejibayeraEste.domain.Producto;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoTrue();

    // Productos activos de una empresa, para mostrar el catálogo.
    List<Producto> findByEmpresa_IdEmpresaAndActivoTrue(Integer idEmpresa);

    // Todos los productos de una empresa, incluidos los inactivos, para su administración.
    List<Producto> findByEmpresa_IdEmpresa(Integer idEmpresa);

    List<Producto> findByCategoria_IdCategoriaAndActivoTrue(Integer idCategoria);

    Optional<Producto> findByIdProductoAndActivoTrue(Integer idProducto);
    


    // Se conserva porque CompraService lo utiliza al confirmar una compra.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.idProducto = :idProducto and p.activo = true")
    Optional<Producto> buscarActivoParaActualizar(@Param("idProducto") Integer idProducto);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.idProducto = :idProducto")
    Optional<Producto> buscarParaActualizar(
            @Param("idProducto") Integer idProducto);
}
