package com.pejibayeraEste.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "Empresa")
public class Empresa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Integer idEmpresa;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre no puede estar vacio.")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
    private String nombre;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "La descripcion no puede estar vacio.")
    @Size(max = 50, message = "La descripcion no puede tener más de 50 caracteres.")
    private String descripcion;
    
    @Column(nullable = false, length = 50)
    @NotBlank(message = "El correo no puede estar vacio.")
    @Size(max = 50, message = "El correo no puede tener más de 50 caracteres.")
    private String correo;

    @NotNull(message = "El campo de existencias no puede estar vacío.")
    @Min(value = 0, message = "Las existencias deben ser un número mayor o igual a 0.")
    private Integer telefono;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La descripcion no puede estar vacio.")
    @Size(max = 100, message = "La descripcion no puede tener más de 50 caracteres.")
    private String direccion;
    
    @Column(nullable = false)
    @NotNull(message = "El campo activo no puede estar vacío.")
    private Boolean activo;

}
