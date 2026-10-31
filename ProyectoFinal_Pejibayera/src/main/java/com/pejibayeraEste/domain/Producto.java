package com.pejibayeraEste.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "producto")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre no puede estar vacio.")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
    private String nombre;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "La descripcion no puede estar vacio.")
    @Size(max = 50, message = "La descripcion no puede tener más de 50 caracteres.")
    private String descripcion;

    @NotNull(message = "El campo de existencias no puede estar vacío.")
    @Min(value = 0, message = "Las existencias deben ser un número mayor o igual a 0.")
    private Integer stock;

    @Column(precision = 12, scale = 2)
    @NotNull(message = "El precio no puede estar vacío.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio debe ser mayor a 0.")
    private BigDecimal precio;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;
    private boolean activo;
    
    @Column(nullable = false)
    NotNull(message = "El campo activo no puede estar vacío.")
    private Boolean activo;
    
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
    
    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;
}
