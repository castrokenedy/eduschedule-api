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
    @org.springframework.data.jpa.repository.Query("""
        select count(h) from Horario h
        where h.diaSemana = :dia
        and (h.professor.id = :professor or h.turma.id = :turma)
        and h.horaInicio < :fim and h.horaFim > :inicio
        and (:ignorar is null or h.id <> :ignorar)
        """)
    long contarConflitos(@org.springframework.data.repository.query.Param("dia") br.com.eduschedule.model.DiaSemana dia,
        @org.springframework.data.repository.query.Param("professor") Long professor,
        @org.springframework.data.repository.query.Param("turma") Long turma,
        @org.springframework.data.repository.query.Param("inicio") java.time.LocalTime inicio,
        @org.springframework.data.repository.query.Param("fim") java.time.LocalTime fim,
        @org.springframework.data.repository.query.Param("ignorar") Long ignorar);
}
