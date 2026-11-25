package br.com.eduschedule.controller;

import br.com.eduschedule.service.IndicadorService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;

@Tag(name = "Indicadores acadêmicos")
@RestController
@RequestMapping("/api/indicadores")
public class IndicadorController {
    private final IndicadorService service;

    public IndicadorController(IndicadorService service) {
        this.service = service;
    }

    @Operation(summary = "Consultar totais acadêmicos")
    @ApiResponse(responseCode = "200", description = "Indicadores consultados")
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @GetMapping
    public IndicadorService.Indicadores consultar() {
        return service.consultar();
    }
}
