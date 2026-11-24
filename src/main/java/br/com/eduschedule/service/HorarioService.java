package br.com.eduschedule.service;

import br.com.eduschedule.dto.Contratos.*;
import br.com.eduschedule.exception.ApiException;
import br.com.eduschedule.model.Horario;
import br.com.eduschedule.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class HorarioService {
    private final HorarioRepository repository;
    private final ProfessorRepository professores;
    private final DisciplinaRepository disciplinas;
    private final TurmaRepository turmas;

    public HorarioService(HorarioRepository repository, ProfessorRepository professores,
                          DisciplinaRepository disciplinas, TurmaRepository turmas) {
        this.repository = repository;
        this.professores = professores;
        this.disciplinas = disciplinas;
        this.turmas = turmas;
    }

    public Page<HorarioSaida> listar(Pageable pageable) {
        return repository.findAll(pageable).map(HorarioService::saida);
    }

    public HorarioSaida buscar(Long id) { return saida(entidade(id)); }

    private Horario entidade(Long id) {
        return repository.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Horario", id));
    }

    @Transactional
    public HorarioSaida criar(HorarioEntrada entrada) {
        var horario = new Horario();
        preencher(horario, entrada);
        return saida(repository.saveAndFlush(horario));
    }

    @Transactional
    public HorarioSaida atualizar(Long id, HorarioEntrada entrada) {
        var horario = entidade(id);
        preencher(horario, entrada);
        return saida(repository.saveAndFlush(horario));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(entidade(id));
        repository.flush();
    }

    public List<HorarioSaida> porProfessor(Long id) {
        if (!professores.existsById(id)) throw ApiException.naoEncontrado("Professor", id);
        return ordenar(repository.findByProfessorId(id));
    }

    public List<HorarioSaida> porTurma(Long id) {
        if (!turmas.existsById(id)) throw ApiException.naoEncontrado("Turma", id);
        return ordenar(repository.findByTurmaId(id));
    }

    public record CargaSemanal(Long professorId, long aulas, long minutos, long horas, long minutosRestantes) {}

    public CargaSemanal cargaSemanal(Long id) {
        var agenda = porProfessor(id);
        long minutos = agenda.stream().mapToLong(a ->
            java.time.Duration.between(a.horaInicio(), a.horaFim()).toMinutes()).sum();
        return new CargaSemanal(id, agenda.size(), minutos, minutos / 60, minutos % 60);
    }

    private List<HorarioSaida> ordenar(List<Horario> horarios) {
        return horarios.stream().sorted(Comparator.comparing(Horario::getDiaSemana)
            .thenComparing(Horario::getHoraInicio).thenComparing(Horario::getId))
            .map(HorarioService::saida).toList();
    }

    private void preencher(Horario horario, HorarioEntrada entrada) {
        if (!entrada.horaInicio().isBefore(entrada.horaFim())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Inicio deve anteceder o termino");
        }
        // Bloqueios em ordem fixa evitam agendamentos concorrentes para o mesmo recurso.
        var professor = professores.bloquearPorId(entrada.professorId())
            .orElseThrow(() -> ApiException.naoEncontrado("Professor", entrada.professorId()));
        var turma = turmas.bloquearPorId(entrada.turmaId())
            .orElseThrow(() -> ApiException.naoEncontrado("Turma", entrada.turmaId()));
        var disciplina = disciplinas.findById(entrada.disciplinaId())
            .orElseThrow(() -> ApiException.naoEncontrado("Disciplina", entrada.disciplinaId()));
        if (repository.contarConflitos(entrada.diaSemana(), professor.getId(), turma.getId(),
                entrada.horaInicio(), entrada.horaFim(), horario.getId()) > 0) {
            throw ApiException.conflito("Professor ou turma possui aula nesse intervalo");
        }
        horario.setProfessor(professor);
        horario.setTurma(turma);
        horario.setDisciplina(disciplina);
        horario.setDiaSemana(entrada.diaSemana());
        horario.setHoraInicio(entrada.horaInicio());
        horario.setHoraFim(entrada.horaFim());
    }

    private static HorarioSaida saida(Horario h) {
        return new HorarioSaida(h.getId(), ProfessorService.saida(h.getProfessor()),
            DisciplinaService.saida(h.getDisciplina()), TurmaService.saida(h.getTurma()),
            h.getDiaSemana(), h.getHoraInicio(), h.getHoraFim());
    }
}
