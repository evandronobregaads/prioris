# DER / MER — PRIORIS

> Projeto Final — Programação de Sistemas — SENAC

Este documento representa o **Diagrama Entidade-Relacionamento (DER/MER)** do banco de dados do Prioris, com base no `schema.sql` atual do projeto.

## Legenda de cardinalidade

- `||` = exatamente um
- `o|` = zero ou um
- `o{` = zero ou muitos

## Diagrama

O GitHub renderiza automaticamente o bloco Mermaid abaixo.

```mermaid
erDiagram

    USUARIOS {
        BIGINT id_usuario PK
        VARCHAR nome
        VARCHAR email UK
        VARCHAR senha_hash
        DATETIME data_criacao
        BOOLEAN ativo
    }

    OBJETIVOS {
        BIGINT id_objetivo PK
        BIGINT id_usuario FK
        VARCHAR titulo
        TEXT descricao
        VARCHAR area
        TEXT motivo
        DATE prazo
        VARCHAR status
        DATETIME data_criacao
    }

    CICLOS {
        BIGINT id_ciclo PK
        BIGINT id_usuario FK
        VARCHAR titulo
        DATE data_inicio
        DATE data_fim
        VARCHAR status
        DATETIME data_criacao
    }

    CICLOS_OBJETIVOS {
        BIGINT id_ciclo_objetivo PK
        BIGINT id_ciclo FK
        BIGINT id_objetivo FK
    }

    METAS {
        BIGINT id_meta PK
        BIGINT id_objetivo FK
        VARCHAR titulo
        TEXT descricao
        DATE prazo
        VARCHAR status
        DATETIME data_criacao
    }

    TAREFAS {
        BIGINT id_tarefa PK
        BIGINT id_usuario FK
        BIGINT id_meta FK
        BIGINT id_objetivo FK
        VARCHAR titulo
        TEXT descricao
        CHAR classificacao_abcde
        DATE data_planejada
        DATETIME prazo
        INT tempo_estimado
        VARCHAR status
        DATETIME data_criacao
        DATETIME data_conclusao
    }

    PRIORIDADES_DIARIAS {
        BIGINT id_prioridade_diaria PK
        BIGINT id_usuario FK
        BIGINT id_tarefa FK
        DATE data_prioridade
    }

    SESSOES_FOCO {
        BIGINT id_sessao_foco PK
        BIGINT id_usuario FK
        BIGINT id_tarefa FK
        DATETIME data_inicio
        DATETIME data_fim
        INT tempo_foco_planejado
        INT tempo_descanso_planejado
        INT tempo_foco_realizado
        VARCHAR status
    }

    PLANEJAMENTOS_SEMANAIS {
        BIGINT id_planejamento_semanal PK
        BIGINT id_usuario FK
        BIGINT id_ciclo FK
        TINYINT semana_ciclo
        DATE data_inicio_semana
        DATE data_fim_semana
        DATETIME data_criacao
    }

    PLANEJAMENTOS_TAREFAS {
        BIGINT id_planejamento_tarefa PK
        BIGINT id_planejamento_semanal FK
        BIGINT id_tarefa FK
    }

    REVISOES_SEMANAIS {
        BIGINT id_revisao_semanal PK
        BIGINT id_planejamento_semanal FK UK
        DECIMAL score_execucao
        TEXT principais_conquistas
        TEXT dificuldades
        TEXT ajustes_proxima_semana
        TEXT observacoes
        DATETIME data_revisao
    }

    USUARIOS ||--o{ OBJETIVOS : possui
    USUARIOS ||--o{ CICLOS : possui
    USUARIOS ||--o{ TAREFAS : possui
    USUARIOS ||--o{ PRIORIDADES_DIARIAS : define
    USUARIOS ||--o{ SESSOES_FOCO : realiza
    USUARIOS ||--o{ PLANEJAMENTOS_SEMANAIS : cria

    CICLOS ||--o{ CICLOS_OBJETIVOS : recebe
    OBJETIVOS ||--o{ CICLOS_OBJETIVOS : participa

    OBJETIVOS ||--o{ METAS : possui

    METAS o|--o{ TAREFAS : vincula
    OBJETIVOS o|--o{ TAREFAS : vincula_diretamente

    TAREFAS ||--o{ PRIORIDADES_DIARIAS : pode_ser_prioridade
    TAREFAS o|--o{ SESSOES_FOCO : pode_orientar

    CICLOS o|--o{ PLANEJAMENTOS_SEMANAIS : organiza

    PLANEJAMENTOS_SEMANAIS ||--o{ PLANEJAMENTOS_TAREFAS : contem
    TAREFAS ||--o{ PLANEJAMENTOS_TAREFAS : participa

    PLANEJAMENTOS_SEMANAIS ||--o| REVISOES_SEMANAIS : possui
```

## Relacionamentos principais

| Origem | Cardinalidade | Destino | Regra |
|---|---|---|---|
| `usuarios` | 1:N | `objetivos` | Um usuário pode possuir vários objetivos. |
| `usuarios` | 1:N | `ciclos` | Um usuário pode possuir vários ciclos. |
| `usuarios` | 1:N | `tarefas` | Um usuário pode possuir várias tarefas. |
| `usuarios` | 1:N | `prioridades_diarias` | Um usuário pode registrar prioridades em diferentes datas. |
| `usuarios` | 1:N | `sessoes_foco` | Um usuário pode realizar várias sessões de foco. |
| `usuarios` | 1:N | `planejamentos_semanais` | Um usuário pode criar vários planejamentos semanais. |
| `ciclos` | N:M | `objetivos` | Implementado pela tabela `ciclos_objetivos`. |
| `objetivos` | 1:N | `metas` | Um objetivo pode possuir várias metas. |
| `metas` | 0..1:N | `tarefas` | Uma tarefa pode estar vinculada a no máximo uma meta. |
| `objetivos` | 0..1:N | `tarefas` | Uma tarefa pode estar vinculada diretamente a no máximo um objetivo. |
| `tarefas` | 1:N | `prioridades_diarias` | Uma tarefa pode ser prioridade em diferentes datas. |
| `tarefas` | 0..1:N | `sessoes_foco` | Uma sessão pode existir sem tarefa ou estar ligada a uma tarefa. |
| `ciclos` | 0..1:N | `planejamentos_semanais` | Um planejamento pode existir com ou sem ciclo associado. |
| `planejamentos_semanais` | N:M | `tarefas` | Implementado pela tabela `planejamentos_tarefas`. |
| `planejamentos_semanais` | 1:0..1 | `revisoes_semanais` | Cada planejamento pode possuir no máximo uma revisão. |

## Regra especial de vínculo das tarefas

A tabela `tarefas` possui os campos opcionais `id_meta` e `id_objetivo`.

A constraint:

```sql
CHECK (
    id_meta IS NULL
    OR id_objetivo IS NULL
)
```

impede que uma tarefa esteja vinculada simultaneamente a uma meta e a um objetivo.

Portanto, uma tarefa pode possuir:

- vínculo com uma Meta;
- vínculo direto com um Objetivo; ou
- nenhum vínculo estratégico.

Nunca os dois vínculos ao mesmo tempo.
