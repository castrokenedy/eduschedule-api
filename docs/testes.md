# Plano de testes

Executar `mvn clean verify` com Java 21 e Maven 3.9.

A suíte utiliza H2 em memória, migrations Flyway e MockMvc. O relatório de cobertura é gerado em `target/site/jacoco/index.html`.

| Cenário | Resultado esperado |
| --- | --- |
| Acesso sem autenticação ou senha incorreta | HTTP 401 |
| Cadastro com dados inválidos | HTTP 400 |
| Cadastro válido | HTTP 201 |
| Consulta de agenda por professor e turma | HTTP 200 |
| Sobreposição de aulas | HTTP 409 |
| Aulas consecutivas sem sobreposição | HTTP 201 |
| Horário invertido | HTTP 400 |
| Exclusão de professor vinculado | HTTP 409 |

A validação em PostgreSQL real, os testes de concorrência e a cobertura completa dos CRUDs permanecem pendentes. Os resultados efetivos serão registrados após execução da suíte.
