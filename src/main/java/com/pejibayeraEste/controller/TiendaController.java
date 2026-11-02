package com.pejibayeraEste.controller;

import com.pejibayeraEste.domain.DetalleCarrito;
import com.pejibayeraEste.domain.Producto;
import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.service.CategoriaService;
import com.pejibayeraEste.service.CompraService;
import com.pejibayeraEste.service.ProductoService;
import com.pejibayeraEste.service.UsuarioService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TiendaController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final CompraService compraService;
    private final UsuarioService usuarioService;

    public TiendaController(
            ProductoService productoService,
            CategoriaService categoriaService,
            CompraService compraService,
            UsuarioService usuarioService) {

        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.compraService = compraService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio(Model model, Authentication authentication) {

        List<Producto> productos
                = new ArrayList<>(productoService.getProductos(true));

// Ordenar los productos por su ID
        productos.sort(Comparator.comparing(Producto::getIdProducto));

// Mostrar solamente los primeros tres
        if (productos.size() > 3) {
            productos = new ArrayList<>(productos.subList(0, 3));
        }

        model.addAttribute("productos", productos);
        model.addAttribute(
                "categorias",
                categoriaService.getCategorias()
        );

        List<DetalleCarrito> detalles = new ArrayList<>();
        BigDecimal totalCarrito = BigDecimal.ZERO;

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName())) {

            Usuario usuario = usuarioService.buscarPorCorreo(
                    authentication.getName()
            ).orElse(null);

            if (usuario != null) {

                detalles = compraService.listarDetallesPorUsuario(
                        usuario.getIdUsuario()
                );

                totalCarrito = compraService.calcularTotalCarrito(
                        usuario.getIdUsuario()
                );
            }
        }

        model.addAttribute("detalles", detalles);
        model.addAttribute("totalCarrito", totalCarrito);

        return "index";
    }
}
