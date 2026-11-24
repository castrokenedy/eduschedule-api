package br.com.eduschedule.repository;

import br.com.eduschedule.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Turma e where e.id = :id")
    java.util.Optional<Turma> bloquearPorId(@org.springframework.data.repository.query.Param("id") Long id);
}
