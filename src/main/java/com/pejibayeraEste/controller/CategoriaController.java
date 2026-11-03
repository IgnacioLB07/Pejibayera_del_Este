package com.pejibayeraEste.controller;

import com.pejibayeraEste.domain.Categoria;
import com.pejibayeraEste.domain.Empresa;
import com.pejibayeraEste.domain.Rol;
import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.service.CategoriaService;
import com.pejibayeraEste.service.EmpresaService;
import com.pejibayeraEste.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;

    public CategoriaController(CategoriaService categoriaService,
            EmpresaService empresaService, UsuarioService usuarioService) {
        this.categoriaService = categoriaService;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listado")
    public String listado(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioActual(authentication);

        if (esSuperadministrador(usuario)) {
            model.addAttribute("categorias", categoriaService.getCategorias());
            model.addAttribute("empresas", empresaService.getEmpresas(true));
        } else if (esAdministrador(usuario)) {
            Empresa empresa = obtenerEmpresaDelAdministrador(usuario);
            model.addAttribute("categorias",
                    categoriaService.getCategoriasPorEmpresa(empresa.getIdEmpresa()));
            model.addAttribute("empresaAsignada", empresa);
        } else {
            throw new AccessDeniedException("No tiene permiso para gestionar categorías.");
        }

        model.addAttribute("categoria", new Categoria());
        return "categoria/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") Categoria categoria,
            BindingResult resultado,
            @RequestParam(required = false) Integer idEmpresa,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Revise los datos de la categoría.");
            return "redirect:/categoria/listado";
        }

        try {
            Usuario usuario = obtenerUsuarioActual(authentication);

            // Si es una edición, primero se comprueba que la categoría pertenezca al usuario.
            if (categoria.getIdCategoria() != null) {
                Categoria categoriaActual = categoriaService
                        .getCategoria(categoria.getIdCategoria()).orElse(null);

                if (categoriaActual == null) {
                    throw new IllegalArgumentException("La categoría no existe.");
                }
                comprobarPropiedad(usuario, categoriaActual.getEmpresa());
            }

            Empresa empresa = obtenerEmpresaParaGuardar(usuario, idEmpresa);
            categoria.setEmpresa(empresa);
            categoriaService.save(categoria);

            redirectAttributes.addFlashAttribute("todoOk", "La categoría se guardó correctamente.");
        } catch (IllegalArgumentException | IllegalStateException | AccessDeniedException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/categoria/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCategoria,
            Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioActual(authentication);
            Categoria categoria = categoriaService.getCategoria(idCategoria).orElse(null);

            if (categoria == null) {
                throw new IllegalArgumentException("La categoría no existe.");
            }

            comprobarPropiedad(usuario, categoria.getEmpresa());
            categoriaService.delete(idCategoria);
            redirectAttributes.addFlashAttribute("todoOk", "La categoría se eliminó correctamente.");
        } catch (IllegalArgumentException | IllegalStateException | AccessDeniedException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/categoria/listado";
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException("Debe iniciar sesión.");
        }

        Usuario usuario = usuarioService.buscarPorCorreo(authentication.getName()).orElse(null);
        if (usuario == null) {
            throw new AccessDeniedException("No se encontró el usuario actual.");
        }
        return usuario;
    }

    private boolean esSuperadministrador(Usuario usuario) {
        Rol rol = usuario.getRol();
        return rol != null && "Superadministrador".equalsIgnoreCase(rol.getNombre());
    }

    private boolean esAdministrador(Usuario usuario) {
        Rol rol = usuario.getRol();
        return rol != null && "Administrador".equalsIgnoreCase(rol.getNombre());
    }

    private Empresa obtenerEmpresaDelAdministrador(Usuario usuario) {
        Empresa empresa = usuario.getEmpresa();
        if (!esAdministrador(usuario) || empresa == null || !Boolean.TRUE.equals(empresa.getActivo())) {
            throw new AccessDeniedException("El administrador no tiene una empresa activa asignada.");
        }
        return empresa;
    }

    private Empresa obtenerEmpresaParaGuardar(Usuario usuario, Integer idEmpresa) {
        if (esAdministrador(usuario)) {
            return obtenerEmpresaDelAdministrador(usuario);
        }

        if (esSuperadministrador(usuario)) {
            if (idEmpresa == null) {
                throw new IllegalArgumentException("Debe seleccionar una empresa.");
            }
            Empresa empresa = empresaService.getEmpresa(idEmpresa).orElse(null);
            if (empresa == null || !Boolean.TRUE.equals(empresa.getActivo())) {
                throw new IllegalArgumentException("La empresa no existe o está inactiva.");
            }
            return empresa;
        }

        throw new AccessDeniedException("No tiene permiso para gestionar categorías.");
    }

    private void comprobarPropiedad(Usuario usuario, Empresa empresaDeCategoria) {
        if (esSuperadministrador(usuario)) {
            return;
        }

        Empresa empresaUsuario = obtenerEmpresaDelAdministrador(usuario);
        if (empresaDeCategoria == null
                || !empresaUsuario.getIdEmpresa().equals(empresaDeCategoria.getIdEmpresa())) {
            throw new AccessDeniedException("No puede modificar categorías de otra empresa.");
        }
    }
}
