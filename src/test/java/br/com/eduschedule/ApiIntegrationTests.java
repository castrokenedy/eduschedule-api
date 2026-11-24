package br.com.eduschedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private ResultActions enviar(String rota, Object corpo) throws Exception {
        return mvc.perform(post("/api/" + rota).with(httpBasic("teste", "senha-exclusiva-de-teste"))
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(corpo)));
    }

    private long criar(String rota, Object corpo) throws Exception {
        var resposta = enviar(rota, corpo).andExpect(status().isCreated()).andReturn();
        return json.readTree(resposta.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test
    void deveExigirAutenticacao() throws Exception {
        mvc.perform(get("/api/professores")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/professores").with(httpBasic("teste", "errada")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void deveValidarProfessor() throws Exception {
        enviar("professores", Map.of("nome", "", "email", "invalido"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deveConsultarAgendaEImpedirConflitos() throws Exception {
        long professor = criar("professores", Map.of("nome", "Ana", "email", "ana@example.test"));
        long outro = criar("professores", Map.of("nome", "Bruno", "email", "bruno@example.test"));
        long disciplina = criar("disciplinas", Map.of("nome", "Java", "codigo", "JAVA", "cargaHoraria", 60));
        long turma = criar("turmas", Map.of("nome", "ADS", "periodo", 4, "semestre", "2026.2"));
        long outraTurma = criar("turmas", Map.of("nome", "ADS B", "periodo", 4, "semestre", "2026.2"));

        long horario = criar("horarios", aula(professor, disciplina, turma, "08:00", "10:00"));
        enviar("horarios", aula(professor, disciplina, outraTurma, "09:00", "11:00"))
            .andExpect(status().isConflict());
        enviar("horarios", aula(outro, disciplina, turma, "09:00", "11:00"))
            .andExpect(status().isConflict());
        enviar("horarios", aula(professor, disciplina, turma, "12:00", "11:00"))
            .andExpect(status().isBadRequest());
        criar("horarios", aula(professor, disciplina, turma, "10:00", "12:00"));

        mvc.perform(get("/api/professores/{id}/horarios", professor).with(httpBasic("teste", "senha-exclusiva-de-teste")))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(horario))
            .andExpect(jsonPath("$[1].horaInicio").value("10:00:00"));
        mvc.perform(get("/api/turmas/{id}/horarios", turma).with(httpBasic("teste", "senha-exclusiva-de-teste")))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].professor.id").value(professor));
        mvc.perform(delete("/api/professores/{id}", professor).with(httpBasic("teste", "senha-exclusiva-de-teste")))
            .andExpect(status().isConflict());
    }

    @Test
    void deveExecutarCrudProfessor() throws Exception {
        long id = criar("professores", Map.of("nome", "Carlos", "email", "carlos@example.test"));
        var auth = httpBasic("teste", "senha-exclusiva-de-teste");
        mvc.perform(get("/api/professores/{id}", id).with(auth))
            .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Carlos"));
        mvc.perform(put("/api/professores/{id}", id).with(auth)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("nome", "Carlos Silva", "email", "carlos@example.test"))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Carlos Silva"));
        mvc.perform(delete("/api/professores/{id}", id).with(auth)).andExpect(status().isNoContent());
        mvc.perform(get("/api/professores/{id}", id).with(auth)).andExpect(status().isNotFound());
    }

    @Test
    void deveConsultarIndicadoresECargaHoraria() throws Exception {
        long p = criar("professores", Map.of("nome", "Lia", "email", "lia@example.test"));
        long d = criar("disciplinas", Map.of("nome", "Redes", "codigo", "REDES", "cargaHoraria", 60));
        long t = criar("turmas", Map.of("nome", "ADS C", "periodo", 4, "semestre", "2026.2"));
        criar("horarios", aula(p, d, t, "08:00", "09:30"));
        var auth = httpBasic("teste", "senha-exclusiva-de-teste");
        mvc.perform(get("/api/professores/{id}/carga-horaria", p).with(auth))
            .andExpect(status().isOk()).andExpect(jsonPath("$.minutos").value(90))
            .andExpect(jsonPath("$.horas").value(1)).andExpect(jsonPath("$.minutosRestantes").value(30));
        mvc.perform(get("/api/indicadores").with(auth))
            .andExpect(status().isOk()).andExpect(jsonPath("$.professores").value(1))
            .andExpect(jsonPath("$.horarios").value(1));
    }

    private Map<String, Object> aula(long p, long d, long t, String inicio, String fim) {
        return Map.of("professorId", p, "disciplinaId", d, "turmaId", t,
            "diaSemana", "SEGUNDA", "horaInicio", inicio, "horaFim", fim);
    }
}
