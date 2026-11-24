package br.com.eduschedule.controller;

import br.com.eduschedule.dto.Contratos.*;
import br.com.eduschedule.service.ProfessorService;
import br.com.eduschedule.service.HorarioService;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {
    private final ProfessorService service;
    private final HorarioService horarios;

    public ProfessorController(ProfessorService service, HorarioService horarios) {
        this.service = service;
        this.horarios = horarios;
    }

    @GetMapping
    public Page<ProfessorSaida> listar(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    public ProfessorSaida buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ProfessorSaida> criar(@Valid @RequestBody ProfessorEntrada entrada) {
        var criado = service.criar(entrada);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    public ProfessorSaida atualizar(@PathVariable Long id, @Valid @RequestBody ProfessorEntrada entrada) {
        return service.atualizar(id, entrada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/horarios")
    public List<HorarioSaida> consultarHorarios(@PathVariable Long id) {
        return horarios.porProfessor(id);
    }
}
