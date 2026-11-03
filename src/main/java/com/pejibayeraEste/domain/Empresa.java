package com.pejibayeraEste.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresa")
@Getter
@Setter
@NoArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Integer idEmpresa;

    @NotBlank(message = "El nombre de la empresa es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres.")
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotBlank(message = "El correo de la empresa es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 100)
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 250)
    @Column(name = "direccion", length = 250)
    private String direccion;

    // En la base de datos este campo se almacena como TINYINT(1).
    @Column(name = "estado", nullable = false)
    private Boolean activo = true;

    public Boolean getActivo() {
        return this.activo;
    }

    @PrePersist
    private void establecerEstadoInicial() {
        if (activo == null) {
            activo = true;
        }
    }

    public Integer getIdEmpresa() {
        return this.idEmpresa;
    }
}
