package com.pejibayeraEste.controller;

import ch.qos.logback.core.model.Model;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TiendaController {
    
    @GetMapping("/")
    public String inicio(Model model, Authentication auth) {
        
        boolean esEmpresa = false;
        boolean esCliente = false;
        
        if (auth !=null && auth.isAuthenticated()) {
            esEmpresa = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"));
            esCliente = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"));
        }
        
        // El servicio trae la lista filtrada según el perfil del usuario
        // List<Producto> productos = productoService.obtenerProductos(esEmpresa, esCliente);
        // model.addAttribute("productos", productos);
        
        return "index";
    }
}
