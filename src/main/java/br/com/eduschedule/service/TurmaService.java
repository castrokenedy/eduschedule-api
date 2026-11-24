package br.com.eduschedule.service;

import br.com.eduschedule.dto.Contratos.*;
import br.com.eduschedule.exception.ApiException;
import br.com.eduschedule.model.Turma;
import br.com.eduschedule.repository.TurmaRepository;
import br.com.eduschedule.repository.HorarioRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TurmaService {
    private final TurmaRepository repository;
    private final HorarioRepository horarios;

    public TurmaService(TurmaRepository repository, HorarioRepository horarios) {
        this.repository = repository;
        this.horarios = horarios;
    }

    public Page<TurmaSaida> listar(Pageable pageable) {
        return repository.findAll(pageable).map(TurmaService::saida);
    }

    public TurmaSaida buscar(Long id) { return saida(entidade(id)); }

    Turma entidade(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Turma", id));
    }

    @Transactional
    public TurmaSaida criar(TurmaEntrada entrada) {
        var entidade = new Turma();
        preencher(entidade, entrada);
        return saida(repository.saveAndFlush(entidade));
    }

    @Transactional
    public TurmaSaida atualizar(Long id, TurmaEntrada entrada) {
        var entidade = entidade(id);
        preencher(entidade, entrada);
        return saida(repository.saveAndFlush(entidade));
    }

    @Transactional
    public void excluir(Long id) {
        var entidade = entidade(id);
        if (horarios.existsByTurmaId(id)) {
            throw ApiException.conflito("Turma possui horarios vinculados");
        }
        repository.delete(entidade);
        repository.flush();
    }

    private void preencher(Turma entidade, TurmaEntrada entrada) {
        entidade.setNome(entrada.nome().strip());
        entidade.setPeriodo(entrada.periodo());
        entidade.setSemestre(entrada.semestre().strip());
    }

    static TurmaSaida saida(Turma entidade) {
        return new TurmaSaida(entidade.getId(), entidade.getNome(), entidade.getPeriodo(), entidade.getSemestre());
    }
}
