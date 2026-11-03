package com.pejibayeraEste.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @Column(name = "fecha", nullable = false, updatable = false)
    private LocalDateTime fecha;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado = "Pendiente";

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @PrePersist
    private void establecerValoresIniciales() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
        if (estado == null || estado.isBlank()) {
            estado = "Pendiente";
        }
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;

    }

    public Integer getIdPedido() {
        return this.idPedido;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public String getEstado() {
        return this.estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    

}
