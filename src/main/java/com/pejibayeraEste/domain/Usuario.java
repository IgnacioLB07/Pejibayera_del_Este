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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    public Integer getIdUsuario() {
        return this.idUsuario;
    }

    // Puede ser null, por ejemplo, para un superadministrador o cliente general.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotBlank
    @Email
    @Size(max = 150)
    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    @NotBlank
    @Size(max = 255)
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "estado", nullable = false)
    private Boolean activo = true;

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    private void establecerValoresIniciales() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }

    public Rol getRol() {
        return this.rol;
    }

    public String getCorreo() {
        return this.correo;
    }

    public String getPassword() {
        return this.password;
    }

    public Boolean getActivo() {
        return this.activo;
    }

    public Empresa getEmpresa() {
        return this.empresa;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
}
