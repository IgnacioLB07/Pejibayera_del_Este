package com.pejibayeraEste.controller;

import com.pejibayeraEste.domain.Rol;
import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.dto.RegistroUsuarioForm;
import com.pejibayeraEste.service.SeguridadService;
import com.pejibayeraEste.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SeguridadService seguridadService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioService usuarioService,
            SeguridadService seguridadService,
            PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.seguridadService = seguridadService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * El registro publico crea exclusivamente clientes.
     * No se acepta un rol enviado por el navegador para evitar que una persona
     * se registre a si misma como Administrador o Superadministrador.
     */
    @PostMapping("/registro/guardar")
    public String registrar(
            @Valid @ModelAttribute("registroUsuario") RegistroUsuarioForm formulario,
            BindingResult resultado,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            redirectAttributes.addFlashAttribute("error",
                    "Revise los datos: nombre, apellido, correo y contraseñas son obligatorios.");
            return "redirect:/registro";
        }

        if (!formulario.getPassword().equals(formulario.getPasswordConf())) {
            redirectAttributes.addFlashAttribute("error",
                    "Las contraseñas no coinciden.");
            return "redirect:/registro";
        }

        String correo = formulario.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (usuarioService.buscarPorCorreo(correo).isPresent()) {
            redirectAttributes.addFlashAttribute("error",
                    "Ya existe una cuenta registrada con ese correo.");
            return "redirect:/registro";
        }

        Rol rolCliente = seguridadService.buscarRolPorNombre("Cliente")
                .orElseThrow(() -> new IllegalStateException(
                "No existe el rol Cliente. Verifique los datos iniciales de la tabla rol."));

        Usuario usuario = new Usuario();
        usuario.setNombre(formulario.getNombre().trim());
        usuario.setApellido(formulario.getApellido().trim());
        usuario.setCorreo(correo);
        usuario.setPassword(passwordEncoder.encode(formulario.getPassword()));
        usuario.setTelefono(formulario.getTelefono() == null
                ? null : formulario.getTelefono().trim());
        usuario.setRol(rolCliente);
        usuario.setEmpresa(null);
        usuario.setActivo(true);

        try {
            usuarioService.guardarUsuario(usuario);
            redirectAttributes.addFlashAttribute("todoOk",
                    "La cuenta fue creada correctamente. Ya puede iniciar sesión.");
            return "redirect:/login";
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error",
                    "No fue posible registrar la cuenta. Verifique que el correo no esté registrado.");
            return "redirect:/registro";
        }
    }
}
