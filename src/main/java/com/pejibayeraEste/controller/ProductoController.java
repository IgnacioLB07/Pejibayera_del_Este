
package com.pejibayeraEste.controller;

import com.pejibayeraEste.domain.Categoria;
import com.pejibayeraEste.domain.Empresa;
import com.pejibayeraEste.domain.Producto;
import com.pejibayeraEste.domain.Rol;
import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.service.CategoriaService;
import com.pejibayeraEste.service.EmpresaService;
import com.pejibayeraEste.service.FirebaseStorageService;
import com.pejibayeraEste.service.ProductoService;
import com.pejibayeraEste.service.UsuarioService;
import jakarta.validation.Valid;
import java.io.IOException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final FirebaseStorageService firebaseStorageService;

    // Constructor único con los cinco servicios.
    public ProductoController(ProductoService productoService,
            CategoriaService categoriaService,
            EmpresaService empresaService,
            UsuarioService usuarioService,
            FirebaseStorageService firebaseStorageService) {

        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.empresaService = empresaService;
        this.usuarioService = usuarioService;
        this.firebaseStorageService = firebaseStorageService;
    }

    @GetMapping("/listado")
    public String listado(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioActual(authentication);

        if (esSuperadministrador(usuario)) {
            model.addAttribute("productos", productoService.getProductos(false));
            model.addAttribute("empresas", empresaService.getEmpresas(true));
            model.addAttribute("categorias", categoriaService.getCategorias());

        } else if (esAdministrador(usuario)) {
            Empresa empresa = obtenerEmpresaDelAdministrador(usuario);

            model.addAttribute("productos",
                    productoService.getTodosProductosPorEmpresa(
                            empresa.getIdEmpresa()));

            model.addAttribute("categorias",
                    categoriaService.getCategoriasPorEmpresa(
                            empresa.getIdEmpresa()));

            model.addAttribute("empresaAsignada", empresa);

        } else {
            throw new AccessDeniedException(
                    "No tiene permiso para gestionar productos.");
        }

        model.addAttribute("producto", new Producto());

        return "producto/listado";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("producto") Producto producto,
            BindingResult resultado,
            @RequestParam Integer idCategoria,
            @RequestParam(required = false) Integer idEmpresa,
            @RequestParam(name = "imagenFile", required = false)
            MultipartFile imagenFile,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Revise los datos del producto.");

            return "redirect:/producto/listado";
        }

        try {
            Usuario usuario = obtenerUsuarioActual(authentication);

            // Comprobamos si estamos creando o editando un producto.
            if (producto.getIdProducto() != null) {

                Producto productoActual = productoService
                        .getProducto(producto.getIdProducto()).orElse(null);

                if (productoActual == null) {
                    throw new IllegalArgumentException(
                            "El producto no existe.");
                }

                comprobarPropiedad(usuario, productoActual.getEmpresa());

                // Conservamos el estado y la imagen existente.
                producto.setActivo(productoActual.getActivo());
                producto.setRutaImagen(productoActual.getRutaImagen());

            } else {
                producto.setActivo(true);
            }

            Empresa empresa = obtenerEmpresaParaGuardar(usuario, idEmpresa);

            Categoria categoria = categoriaService
                    .getCategoria(idCategoria).orElse(null);

            if (categoria == null) {
                throw new IllegalArgumentException(
                        "La categoría seleccionada no existe.");
            }

            // La categoría debe pertenecer a la misma empresa.
            if (categoria.getEmpresa() == null
                    || !empresa.getIdEmpresa().equals(
                            categoria.getEmpresa().getIdEmpresa())) {

                throw new IllegalArgumentException(
                        "La categoría seleccionada no pertenece "
                        + "a la empresa del producto.");
            }

            producto.setEmpresa(empresa);
            producto.setCategoria(categoria);

            // Primero guardamos el producto para obtener su ID.
            Producto productoGuardado = productoService.save(producto);

            // Si se seleccionó una imagen, la enviamos a Firebase.
            if (imagenFile != null && !imagenFile.isEmpty()) {

                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile,
                        "productos",
                        productoGuardado.getIdProducto()
                );

                // Guardamos la URL que devuelve Firebase en MySQL.
                productoGuardado.setRutaImagen(rutaImagen);

                productoService.save(productoGuardado);
            }

            redirectAttributes.addFlashAttribute(
                    "todoOk", "El producto se guardó correctamente.");

        } catch (IOException | IllegalArgumentException
                | IllegalStateException | AccessDeniedException ex) {

            redirectAttributes.addFlashAttribute(
                    "error", "No se pudo guardar el producto: "
                    + ex.getMessage());
        }

        return "redirect:/producto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idProducto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = obtenerUsuarioActual(authentication);

            Producto producto = productoService
                    .getProducto(idProducto).orElse(null);

            if (producto == null) {
                throw new IllegalArgumentException(
                        "El producto no existe.");
            }

            comprobarPropiedad(usuario, producto.getEmpresa());

            productoService.delete(idProducto);

            redirectAttributes.addFlashAttribute(
                    "todoOk", "El producto se desactivó correctamente.");

        } catch (IllegalArgumentException | IllegalStateException
                | AccessDeniedException ex) {

            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());
        }

        return "redirect:/producto/listado";
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {

            throw new AccessDeniedException("Debe iniciar sesión.");
        }

        Usuario usuario = usuarioService
                .buscarPorCorreo(authentication.getName()).orElse(null);

        if (usuario == null) {
            throw new AccessDeniedException(
                    "No se encontró el usuario actual.");
        }

        return usuario;
    }

    private boolean esSuperadministrador(Usuario usuario) {
        Rol rol = usuario.getRol();

        return rol != null
                && "Superadministrador".equalsIgnoreCase(rol.getNombre());
    }

    private boolean esAdministrador(Usuario usuario) {
        Rol rol = usuario.getRol();

        return rol != null
                && "Administrador".equalsIgnoreCase(rol.getNombre());
    }

    private Empresa obtenerEmpresaDelAdministrador(Usuario usuario) {

        Empresa empresa = usuario.getEmpresa();

        if (!esAdministrador(usuario)
                || empresa == null
                || !Boolean.TRUE.equals(empresa.getActivo())) {

            throw new AccessDeniedException(
                    "El administrador no tiene una empresa activa asignada.");
        }

        return empresa;
    }

    private Empresa obtenerEmpresaParaGuardar(
            Usuario usuario, Integer idEmpresa) {

        if (esAdministrador(usuario)) {
            return obtenerEmpresaDelAdministrador(usuario);
        }

        if (esSuperadministrador(usuario)) {

            if (idEmpresa == null) {
                throw new IllegalArgumentException(
                        "Debe seleccionar una empresa.");
            }

            Empresa empresa = empresaService
                    .getEmpresa(idEmpresa).orElse(null);

            if (empresa == null || !Boolean.TRUE.equals(empresa.getActivo())) {
                throw new IllegalArgumentException(
                        "La empresa no existe o está inactiva.");
            }

            return empresa;
        }

        throw new AccessDeniedException(
                "No tiene permiso para gestionar productos.");
    }

    private void comprobarPropiedad(
            Usuario usuario, Empresa empresaDelProducto) {

        if (esSuperadministrador(usuario)) {
            return;
        }

        Empresa empresaUsuario = obtenerEmpresaDelAdministrador(usuario);

        if (empresaDelProducto == null
                || !empresaUsuario.getIdEmpresa().equals(
                        empresaDelProducto.getIdEmpresa())) {

            throw new AccessDeniedException(
                    "No puede modificar productos de otra empresa.");
        }
    }
}
