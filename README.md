# EduSchedule API

API REST para organização de horários acadêmicos, desenvolvida na disciplina Projeto de Website - Back End do curso de Análise e Desenvolvimento de Sistemas.

## Funcionalidades

- Cadastro, consulta, atualização e exclusão de professores, disciplinas, turmas e horários.
- Agenda semanal por professor e turma.
- Validação de sobreposições de aulas e integridade dos cadastros.
- Indicadores acadêmicos e carga horária semanal em horas de 60 minutos.
- Autenticação HTTP Basic e documentação interativa OpenAPI.

## Tecnologias

Java 21, Spring Boot, Spring MVC, Spring Data JPA, Hibernate, PostgreSQL, Flyway, Spring Security, Maven, Swagger, JUnit e Docker.

## Executar localmente

Pré-requisitos: Git e Docker com Compose.

```bash
git clone https://github.com/castrokenedy/eduschedule-api.git
cd eduschedule-api
cp .env.example .env
```

No Windows PowerShell, use `Copy-Item .env.example .env` no lugar de `cp`, se necessário.

Edite o arquivo .env e informe senhas próprias para DB_PASSWORD e API_PASSWORD. Não publique esse arquivo.

```bash
docker compose up --build -d
docker compose logs -f api
```

Abra http://localhost:8080/swagger-ui/index.html e utilize Authorize com API_USERNAME e API_PASSWORD definidos no .env. O banco é inicializado por Flyway. Para encerrar, use `docker compose down`, preservando o volume de dados.

## Recursos REST

| Recurso | Caminho |
| --- | --- |
| Professores | /api/professores |
| Disciplinas | /api/disciplinas |
| Turmas | /api/turmas |
| Horários | /api/horarios |
| Agenda do professor | /api/professores/{id}/horarios |
| Agenda da turma | /api/turmas/{id}/horarios |
| Carga semanal | /api/professores/{id}/carga-horaria |
| Indicadores | /api/indicadores |

Os quatro recursos cadastrais possuem GET, POST, PUT e DELETE. As listagens aceitam page, size e sort. A numeração de páginas começa em zero.

### Exemplo de cadastro

```json
{
  "nome": "Ana Silva",
  "email": "ana@example.test",
  "especialidade": "Programação"
}
```

Envie ao endpoint POST /api/professores após autenticação. O retorno esperado é HTTP 201.

Para cadastrar um horário, utilize os identificadores de professor, disciplina e turma previamente cadastrados:

```json
{
  "professorId": 1,
  "disciplinaId": 1,
  "turmaId": 1,
  "diaSemana": "SEGUNDA",
  "horaInicio": "08:00",
  "horaFim": "09:30"
}
```

Os identificadores acima são ilustrativos. Intervalos consecutivos são permitidos; sobreposições para o mesmo professor ou turma são rejeitadas.

## Testes

Com Java 21 e Maven 3.9 instalados:

```bash
mvn clean verify
```

Os testes utilizam H2 em memória. O relatório JaCoCo fica em target/site/jacoco/index.html. Consulte também a aba Actions do repositório. A homologação em PostgreSQL e os resultados efetivos de cobertura devem ser conferidos antes da entrega.

## Documentação

- [Requisitos](docs/requisitos.md)
- [Arquitetura e modelo de dados](docs/arquitetura.md)
- [Plano de testes](docs/testes.md)

## Segurança

A configuração Docker limita a exposição da API ao computador local. HTTP Basic deve ser utilizado sobre HTTPS fora do ambiente local. As credenciais de teste não são destinadas ao uso real. A política de CSRF deve ser revisada antes de uma publicação em navegador de produção.

## Estado do projeto

Em desenvolvimento acadêmico. O código implementado ainda está sujeito à compilação, testes e homologação.
