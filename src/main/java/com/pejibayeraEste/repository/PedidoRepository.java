package com.pejibayeraEste.repository;

import com.pejibayeraEste.domain.Pedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario_IdUsuarioOrderByFechaDesc(Integer idUsuario);

    List<Pedido> findByEmpresa_IdEmpresaOrderByFechaDesc(Integer idEmpresa);

    List<Pedido> findByEstadoOrderByFechaDesc(String estado);

    Optional<Pedido> findByIdPedidoAndUsuario_IdUsuario(Integer idPedido, Integer idUsuario);

    Optional<Pedido> findByIdPedidoAndEmpresa_IdEmpresa(Integer idPedido, Integer idEmpresa);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.idPedido = :idPedido")
    Optional<Pedido> buscarPedidoParaActualizar(
            @Param("idPedido") Integer idPedido);
}
