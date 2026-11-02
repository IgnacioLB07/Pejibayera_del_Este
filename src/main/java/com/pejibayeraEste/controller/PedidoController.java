package com.pejibayeraEste.controller;

import com.pejibayeraEste.domain.Pedido;
import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.service.CompraService;
import com.pejibayeraEste.service.UsuarioService;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    private final CompraService compraService;
    private final UsuarioService usuarioService;

    public PedidoController(CompraService compraService, UsuarioService usuarioService) {
        this.compraService = compraService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/confirmar")
    public String confirmar(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioActual(authentication);
            exigirCliente(usuario);
            List<Pedido> pedidos = compraService.confirmarCompra(usuario.getIdUsuario());
            redirectAttributes.addFlashAttribute("todoOk",
                    pedidos.size() == 1
                    ? "El pedido se registró correctamente."
                    : "Se registraron " + pedidos.size() + " pedidos, uno por cada empresa.");
            return "redirect:/pedido/historial";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("abrirCarrito", true);
            return "redirect:/";
        }
    }

    @GetMapping("/historial")
    public String historial(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        exigirCliente(usuario);
        model.addAttribute("pedidos",
                compraService.listarPedidosPorUsuario(usuario.getIdUsuario()));
        return "pedido/historial";
    }

    @GetMapping("/detalle")
    public String detalle(
            @RequestParam Integer idPedido,
            Authentication authentication,
            Model model) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        exigirCliente(usuario);
        Pedido pedido = compraService.buscarPedidoDeUsuario(
                idPedido, usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El pedido no existe o no pertenece a su cuenta."));
        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", compraService.listarDetallesPedido(idPedido));
        return "pedido/detalle";
    }

    @GetMapping("/gestion/listado")
    public String gestion(Authentication authentication, Model model) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        String nombreRol = usuario.getRol() == null ? "" : usuario.getRol().getNombre();
        List<Pedido> pedidos;

        if ("Superadministrador".equalsIgnoreCase(nombreRol)) {
            pedidos = compraService.listarPedidos();
        } else if ("Administrador".equalsIgnoreCase(nombreRol)) {
            if (usuario.getEmpresa() == null) {
                throw new AccessDeniedException(
                        "El administrador no tiene una empresa asignada.");
            }
            pedidos = compraService.listarPedidosPorEmpresa(
                    usuario.getEmpresa().getIdEmpresa());
        } else {
            throw new AccessDeniedException("No tiene permisos para gestionar pedidos.");
        }

        model.addAttribute("pedidos", pedidos);
        return "pedido/gestion/listado";
    }

    @PostMapping("/gestion/estado")
    public String actualizarEstado(
            @RequestParam Integer idPedido,
            @RequestParam String nuevoEstado,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioActual(authentication);
            String nombreRol = usuario.getRol() == null ? "" : usuario.getRol().getNombre();

            if ("Superadministrador".equalsIgnoreCase(nombreRol)) {
                compraService.actualizarEstadoPedido(idPedido, nuevoEstado);
            } else if ("Administrador".equalsIgnoreCase(nombreRol)) {
                if (usuario.getEmpresa() == null
                        || compraService.buscarPedidoDeEmpresa(
                                idPedido, usuario.getEmpresa().getIdEmpresa()).isEmpty()) {
                    throw new AccessDeniedException(
                            "No puede modificar pedidos de otra empresa.");
                }
                compraService.actualizarEstadoPedido(idPedido, nuevoEstado);
            } else {
                throw new AccessDeniedException(
                        "No tiene permisos para actualizar pedidos.");
            }

            redirectAttributes.addFlashAttribute("todoOk",
                    "El estado del pedido se actualizó correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/pedido/gestion/listado";
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Debe iniciar sesión.");
        }
        return usuarioService.buscarPorCorreo(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException(
                        "No se encontró el usuario de la sesión actual."));
    }

    private void exigirCliente(Usuario usuario) {
        if (usuario.getRol() == null
                || !"Cliente".equalsIgnoreCase(usuario.getRol().getNombre())) {
            throw new AccessDeniedException("Esta operación está disponible para clientes.");
        }
    }

    @PostMapping("/cancelar")
    public String cancelar(
            @RequestParam Integer idPedido,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = obtenerUsuarioActual(authentication);
            exigirCliente(usuario);

            compraService.cancelarPedidoDeUsuario(
                    idPedido, usuario.getIdUsuario());

            redirectAttributes.addFlashAttribute(
                    "todoOk", "El pedido fue cancelado correctamente.");

        } catch (IllegalArgumentException | IllegalStateException e) {

            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/pedido/historial";
    }
}
