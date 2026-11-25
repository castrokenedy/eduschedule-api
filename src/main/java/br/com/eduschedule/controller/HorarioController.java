package br.com.eduschedule.controller;

import br.com.eduschedule.dto.Contratos.*;
import br.com.eduschedule.service.HorarioService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Horários")
@RestController
@RequestMapping("/api/horarios")
public class HorarioController {
    private final HorarioService service;

    public HorarioController(HorarioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar registros")
    @ApiResponse(responseCode = "200", description = "Consulta realizada")
    @ApiResponse(responseCode = "400", description = "Ordenação inválida")
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @GetMapping
    public Page<HorarioSaida> listar(@ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.listar(Paginacao.validar(pageable, "id", "diaSemana", "horaInicio", "horaFim"));
    }

    @Operation(summary = "Consultar por ID")
    @ApiResponse(responseCode = "200", description = "Registro encontrado")
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @GetMapping("/{id}")
    public HorarioSaida buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @Operation(summary = "Cadastrar registro")
    @ApiResponse(responseCode = "201", description = "Registro criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflito de dados")
    @ApiResponse(responseCode = "404", description = "Cadastro vinculado não encontrado")
    @PostMapping
    public ResponseEntity<HorarioSaida> criar(@Valid @RequestBody HorarioEntrada entrada) {
        var criado = service.criar(entrada);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @Operation(summary = "Atualizar registro")
    @ApiResponse(responseCode = "200", description = "Registro atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    @ApiResponse(responseCode = "409", description = "Conflito de dados")
    @PutMapping("/{id}")
    public HorarioSaida atualizar(@PathVariable Long id, @Valid @RequestBody HorarioEntrada entrada) {
        return service.atualizar(id, entrada);
    }

    @Operation(summary = "Excluir registro")
    @ApiResponse(responseCode = "204", description = "Registro excluído", content = @Content)
    @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    @ApiResponse(responseCode = "409", description = "Conflito de integridade")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
