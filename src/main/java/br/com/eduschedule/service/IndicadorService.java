package br.com.eduschedule.service;

import br.com.eduschedule.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IndicadorService {
    private final ProfessorRepository professores;
    private final DisciplinaRepository disciplinas;
    private final TurmaRepository turmas;
    private final HorarioRepository horarios;

    public IndicadorService(ProfessorRepository professores, DisciplinaRepository disciplinas,
                            TurmaRepository turmas, HorarioRepository horarios) {
        this.professores = professores;
        this.disciplinas = disciplinas;
        this.turmas = turmas;
        this.horarios = horarios;
    }

    public record Indicadores(long professores, long disciplinas, long turmas, long horarios) {}

    public Indicadores consultar() {
        return new Indicadores(professores.count(), disciplinas.count(), turmas.count(), horarios.count());
    }
}
