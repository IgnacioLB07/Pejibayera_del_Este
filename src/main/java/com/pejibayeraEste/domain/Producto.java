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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // Se conserva el nombre "detalle" para que coincida con la vista de Ignacio.
    @Size(max = 500)
    @Column(name = "descripcion", length = 500)
    private String detalle;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.00", inclusive = true, message = "El precio no puede ser negativo.")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Se conserva "existencias" para mantener compatibilidad con las plantillas existentes.
    @NotNull(message = "Las existencias son obligatorias.")
    @Min(value = 0, message = "Las existencias no pueden ser negativas.")
    @Column(name = "stock", nullable = false)
    private Integer existencias = 0;

    // En MySQL la columna se llama "imagen"; la vista utiliza "rutaImagen".
    @Size(max = 1024)
    @Column(name = "imagen", length = 1024)
    private String rutaImagen;

    // En MySQL la columna se llama "estado"; las vistas de Ignacio utilizan "activo".
    @Column(name = "estado", nullable = false)
    private Boolean activo = true;

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @PrePersist
    private void establecerValoresIniciales() {
        if (existencias == null) {
            existencias = 0;
        }
        if (activo == null) {
            activo = true;
        }
    }

    public Integer getIdProducto() {
        return this.idProducto;
    }

    public Empresa getEmpresa() {
        return this.empresa;
    }

    public String getNombre() {
        return this.nombre;
    }

    public BigDecimal getPrecio() {
        return this.precio;
    }

    public Integer getExistencias() {
        return this.existencias;
    }

    public void setExistencias(Integer existencias) {
        this.existencias = existencias;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDetalle() {
        return this.detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getRutaImagen() {
        return this.rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public Boolean getActivo() {
        return this.activo;
    }
}
