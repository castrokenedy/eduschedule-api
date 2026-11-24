package br.com.eduschedule.service;

import br.com.eduschedule.dto.Contratos.*;
import br.com.eduschedule.exception.ApiException;
import br.com.eduschedule.model.Disciplina;
import br.com.eduschedule.repository.DisciplinaRepository;
import br.com.eduschedule.repository.HorarioRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DisciplinaService {
    private final DisciplinaRepository repository;
    private final HorarioRepository horarios;

    public DisciplinaService(DisciplinaRepository repository, HorarioRepository horarios) {
        this.repository = repository;
        this.horarios = horarios;
    }

    public Page<DisciplinaSaida> listar(Pageable pageable) {
        return repository.findAll(pageable).map(DisciplinaService::saida);
    }

    public DisciplinaSaida buscar(Long id) { return saida(entidade(id)); }

    Disciplina entidade(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Disciplina", id));
    }

    @Transactional
    public DisciplinaSaida criar(DisciplinaEntrada entrada) {
        var entidade = new Disciplina();
        preencher(entidade, entrada);
        return saida(repository.saveAndFlush(entidade));
    }

    @Transactional
    public DisciplinaSaida atualizar(Long id, DisciplinaEntrada entrada) {
        var entidade = entidade(id);
        preencher(entidade, entrada);
        return saida(repository.saveAndFlush(entidade));
    }

    @Transactional
    public void excluir(Long id) {
        var entidade = entidade(id);
        if (horarios.existsByDisciplinaId(id)) {
            throw ApiException.conflito("Disciplina possui horarios vinculados");
        }
        repository.delete(entidade);
        repository.flush();
    }

    private void preencher(Disciplina entidade, DisciplinaEntrada entrada) {
        entidade.setNome(entrada.nome().strip());
        entidade.setCodigo(entrada.codigo().strip());
        entidade.setCargaHoraria(entrada.cargaHoraria());
    }

    static DisciplinaSaida saida(Disciplina entidade) {
        return new DisciplinaSaida(entidade.getId(), entidade.getNome(), entidade.getCodigo(), entidade.getCargaHoraria());
    }
}
