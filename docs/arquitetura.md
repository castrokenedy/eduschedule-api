# Arquitetura

A aplicação utiliza Spring MVC para exposição de recursos REST, com separação entre transporte HTTP, regras de negócio e persistência.

```mermaid
flowchart TD
    A[Swagger UI / Cliente HTTP] --> B[Spring Security]
    B --> C[Controller]
    C --> D[Service]
    D --> E[Repository]
    E --> F[(PostgreSQL)]
```

## Responsabilidades

- controller: endpoints e códigos HTTP.
- dto: contratos de entrada e saída.
- service: validações de negócio e transações.
- repository: acesso a dados via Spring Data JPA.
- model: entidades persistentes.
- config: configuração da aplicação.
- exception: tratamento centralizado de erros.

## Modelo preliminar

```mermaid
erDiagram
    PROFESSOR ||--o{ HORARIO : ministra
    DISCIPLINA ||--o{ HORARIO : compoe
    TURMA ||--o{ HORARIO : recebe
    PROFESSOR {
        long id PK
        string nome
        string email
        string especialidade
    }
    DISCIPLINA {
        long id PK
        string nome
        string codigo
        int cargaHoraria
    }
    TURMA {
        long id PK
        string nome
        int periodo
        string semestre
    }
    HORARIO {
        long id PK
        long professorId FK
        long disciplinaId FK
        long turmaId FK
        string diaSemana
        time horaInicio
        time horaFim
    }
```

## Decisões iniciais

PostgreSQL será utilizado para persistência, Flyway para versionamento do esquema e Bean Validation para validação dos contratos. Credenciais serão fornecidas por variáveis de ambiente. A documentação OpenAPI será utilizada para demonstrar as operações.

As decisões acima descrevem a arquitetura planejada, não funcionalidades já implementadas.
