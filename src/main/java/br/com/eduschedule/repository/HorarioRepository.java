package br.com.eduschedule.repository;

import br.com.eduschedule.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {

    boolean existsByProfessorId(Long professorId);
    boolean existsByDisciplinaId(Long disciplinaId);
    boolean existsByTurmaId(Long turmaId);

    @EntityGraph(attributePaths = {"professor", "disciplina", "turma"})
    List<Horario> findByProfessorId(Long professorId);

    @EntityGraph(attributePaths = {"professor", "disciplina", "turma"})
    List<Horario> findByTurmaId(Long turmaId);
}
