package com.pejibayeraEste.controller;

import com.pejibayeraEste.domain.Empresa;
import com.pejibayeraEste.service.EmpresaService;
import jakarta.validation.Valid;
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
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var empresas = empresaService.getEmpresas(false);
        model.addAttribute("empresas", empresas);
        model.addAttribute("totalEmpresas", empresas.size());
        return "empresa/listado";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute Empresa empresa,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error",
                    "Revise los datos del formulario de empresa.");
            return "redirect:/empresa/listado";
        }

        try {
            empresaService.save(empresa);
            redirectAttributes.addFlashAttribute("todoOk",
                    "La empresa se guardó correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error",
                    "No fue posible guardar la empresa. Verifique los datos e inténtelo nuevamente.");
        }
        return "redirect:/empresa/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam Integer idEmpresa,
            RedirectAttributes redirectAttributes) {
        try {
            empresaService.delete(idEmpresa);
            redirectAttributes.addFlashAttribute("todoOk",
                    "La empresa se eliminó correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/empresa/listado";
    }
}
