package com.pejibayeraEste.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos recibidos desde el formulario publico de registro.
 * El rol NO se recibe del navegador: todo registro publico sera Cliente.
 */
public class RegistroUsuarioForm {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres.")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres.")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, max = 255, message = "La contraseña debe tener entre 8 y 255 caracteres.")
    private String password;

    @NotBlank(message = "Debe confirmar la contraseña.")
    private String passwordConf;

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres.")
    private String telefono;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConf() {
        return passwordConf;
    }

    public void setPasswordConf(String passwordConf) {
        this.passwordConf = passwordConf;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
