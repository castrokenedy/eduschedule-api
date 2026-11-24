# Requisitos do sistema

## Objetivo

Organizar horários acadêmicos e disponibilizar consultas por professor e turma por meio de uma API REST.

## Requisitos funcionais

- RF01 — Cadastrar, consultar, atualizar e excluir professores.
- RF02 — Cadastrar, consultar, atualizar e excluir disciplinas.
- RF03 — Cadastrar, consultar, atualizar e excluir turmas.
- RF04 — Cadastrar, consultar, atualizar e excluir horários.
- RF05 — Consultar horários por professor, incluindo disciplinas e turmas.
- RF06 — Consultar horários por turma, incluindo disciplinas e professores.
- RF07 — Validar os dados recebidos.

## Requisitos técnicos

Java, Spring Boot, persistência ORM, autenticação HTTP Basic, documentação OpenAPI e repositório público.

## Regras de negócio propostas

- O início de uma aula deve anteceder seu término.
- Um professor não pode ministrar aulas simultâneas.
- Uma turma não pode possuir aulas simultâneas.
- A exclusão de registros vinculados a horários deve ser rejeitada.
- As consultas de agenda devem ser ordenadas por dia e horário.

A interface de demonstração será o Swagger UI, sem aplicação front-end separada.
O modelo de dados é preliminar até a conferência do modelo indicado no enunciado.
