package br.com.eduschedule.service;

import br.com.eduschedule.dto.Contratos.*;
import br.com.eduschedule.exception.ApiException;
import br.com.eduschedule.model.Professor;
import br.com.eduschedule.repository.ProfessorRepository;
import br.com.eduschedule.repository.HorarioRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProfessorService {
    private final ProfessorRepository repository;
    private final HorarioRepository horarios;

    public ProfessorService(ProfessorRepository repository, HorarioRepository horarios) {
        this.repository = repository;
        this.horarios = horarios;
    }

    public Page<ProfessorSaida> listar(Pageable pageable) {
        return repository.findAll(pageable).map(ProfessorService::saida);
    }

    public ProfessorSaida buscar(Long id) { return saida(entidade(id)); }

    Professor entidade(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Professor", id));
    }

    @Transactional
    public ProfessorSaida criar(ProfessorEntrada entrada) {
        var entidade = new Professor();
        preencher(entidade, entrada);
        return saida(repository.saveAndFlush(entidade));
    }

    @Transactional
    public ProfessorSaida atualizar(Long id, ProfessorEntrada entrada) {
        var entidade = entidade(id);
        preencher(entidade, entrada);
        return saida(repository.saveAndFlush(entidade));
    }

    @Transactional
    public void excluir(Long id) {
        var entidade = entidade(id);
        if (horarios.existsByProfessorId(id)) {
            throw ApiException.conflito("Professor possui horarios vinculados");
        }
        repository.delete(entidade);
        repository.flush();
    }

    private void preencher(Professor entidade, ProfessorEntrada entrada) {
        entidade.setNome(entrada.nome().strip());
        entidade.setEmail(entrada.email().strip());
        entidade.setEspecialidade(entrada.especialidade());
    }

    static ProfessorSaida saida(Professor entidade) {
        return new ProfessorSaida(entidade.getId(), entidade.getNome(), entidade.getEmail(), entidade.getEspecialidade());
    }
}
