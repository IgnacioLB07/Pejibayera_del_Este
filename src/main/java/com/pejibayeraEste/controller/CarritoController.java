
package com.pejibayeraEste.controller;

import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.service.CompraService;
import com.pejibayeraEste.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CompraService compraService;
    private final UsuarioService usuarioService;

    public CarritoController(CompraService compraService, UsuarioService usuarioService) {
        this.compraService = compraService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listado")
    public String listado(Authentication authentication, Model model) {

        Usuario usuario = obtenerUsuarioActual(authentication);

        var carrito = compraService.buscarCarritoPorUsuario(usuario.getIdUsuario());
        var detalles = compraService.listarDetallesPorUsuario(usuario.getIdUsuario());

        model.addAttribute("carrito", carrito.orElse(null));
        model.addAttribute("detalles", detalles);
        model.addAttribute(
                "totalCarrito",
                compraService.calcularTotalCarrito(usuario.getIdUsuario())
        );

        return "carrito/listado";
    }

    @PostMapping("/agregar")
    public String agregar(
            @RequestParam Integer idProducto,
            @RequestParam(defaultValue = "1") Integer cantidad,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            Usuario usuario = obtenerUsuarioActual(authentication);

            compraService.agregarProductoAlCarrito(
                    usuario.getIdUsuario(), idProducto, cantidad
            );

            redirectAttributes.addFlashAttribute(
                    "todoOk", "El producto se agregó al carrito."
            );

        } catch (IllegalArgumentException | IllegalStateException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("abrirCarrito", true);

        return "redirect:/";
    }

    @PostMapping("/actualizar")
    public String actualizar(
            @RequestParam Integer idDetalle,
            @RequestParam Integer cantidad,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            Usuario usuario = obtenerUsuarioActual(authentication);

            compraService.actualizarCantidadDetalleCarrito(
                    usuario.getIdUsuario(), idDetalle, cantidad
            );

            redirectAttributes.addFlashAttribute(
                    "todoOk", "La cantidad del producto se actualizó."
            );

        } catch (IllegalArgumentException | IllegalStateException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("abrirCarrito", true);

        return "redirect:/";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idDetalle,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            Usuario usuario = obtenerUsuarioActual(authentication);

            compraService.eliminarDetalleCarritoDeUsuario(
                    usuario.getIdUsuario(), idDetalle
            );

            redirectAttributes.addFlashAttribute(
                    "todoOk", "El producto se eliminó del carrito."
            );

        } catch (IllegalArgumentException | IllegalStateException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("abrirCarrito", true);

        return "redirect:/";
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "Debe iniciar sesión para utilizar el carrito."
            );
        }

        return usuarioService.buscarPorCorreo(authentication.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró el usuario de la sesión actual."
                ));
    }
}
