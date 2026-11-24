package br.com.eduschedule.dto;

import br.com.eduschedule.model.DiaSemana;
import jakarta.validation.constraints.*;
import java.time.LocalTime;

public final class Contratos {
    private Contratos() {}

    public record ProfessorEntrada(@NotBlank @Size(max=150) String nome,
        @NotBlank @Email @Size(max=254) String email, @Size(max=120) String especialidade) {}
    public record ProfessorSaida(Long id, String nome, String email, String especialidade) {}
    public record DisciplinaEntrada(@NotBlank @Size(max=150) String nome,
        @NotBlank @Size(max=30) String codigo, @NotNull @Positive Integer cargaHoraria) {}
    public record DisciplinaSaida(Long id, String nome, String codigo, Integer cargaHoraria) {}
    public record TurmaEntrada(@NotBlank @Size(max=100) String nome,
        @NotNull @Positive Integer periodo, @NotBlank @Size(max=10) String semestre) {}
    public record TurmaSaida(Long id, String nome, Integer periodo, String semestre) {}
    public record HorarioEntrada(@NotNull @Positive Long professorId,
        @NotNull @Positive Long disciplinaId, @NotNull @Positive Long turmaId,
        @NotNull DiaSemana diaSemana, @NotNull LocalTime horaInicio, @NotNull LocalTime horaFim) {}
    public record HorarioSaida(Long id, ProfessorSaida professor, DisciplinaSaida disciplina,
        TurmaSaida turma, DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFim) {}
}
