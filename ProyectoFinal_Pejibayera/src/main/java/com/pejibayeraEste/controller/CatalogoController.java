package com.pejibayeraEste.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {
    
    @GetMapping("/listado")
    public String listado() {
        //TODO
            //Servicio para obtener productos segun tipo de Usuario
        
        //Renderizar templates.catalogo
        return "catalogo/listado";
    }
    
}
