package br.com.eduschedule.repository;

import br.com.eduschedule.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Professor e where e.id = :id")
    java.util.Optional<Professor> bloquearPorId(@org.springframework.data.repository.query.Param("id") Long id);
}
