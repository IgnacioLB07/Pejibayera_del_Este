package com.pejibayeraEste.controller;

import com.pejibayeraEste.service.CategoriaService;
import com.pejibayeraEste.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public CatalogoController(
            ProductoService productoService,
            CategoriaService categoriaService) {

        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute(
                "productos",
                productoService.getProductos(true)
        );

        model.addAttribute(
                "categorias",
                categoriaService.getCategorias()
        );

        return "catalogo/listado";
    }
}