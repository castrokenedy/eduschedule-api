package br.com.eduschedule.controller;

import br.com.eduschedule.service.IndicadorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/indicadores")
public class IndicadorController {
    private final IndicadorService service;

    public IndicadorController(IndicadorService service) {
        this.service = service;
    }

    @GetMapping
    public IndicadorService.Indicadores consultar() {
        return service.consultar();
    }
}
