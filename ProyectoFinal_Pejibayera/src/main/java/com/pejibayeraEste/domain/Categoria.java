package com.pejibayeraEste.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "Categoria")
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre no puede estar vacio.")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
    private String nombre;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "La descripcion no puede estar vacio.")
    @Size(max = 50, message = "La descripcion no puede tener más de 50 caracteres.")
    private String descripcion;
    
    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

}
