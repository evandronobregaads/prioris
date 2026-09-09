# Dicionário de Dados — PRIORIS

> Documento derivado do `schema.sql` da V1 do projeto.

## Convenções

- **Nulo? = Não**: campo obrigatório.
- **Nulo? = Sim**: campo opcional.
- **PK**: chave primária.
- **FK**: chave estrangeira.
- **UNIQUE**: valor ou combinação não pode se repetir.
- **CHECK**: regra de integridade validada pelo banco.

## 1. `usuarios`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_usuario` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único do usuário. |
| `nome` | `VARCHAR(120)` | Não | NOT NULL | Nome do usuário. |
| `email` | `VARCHAR(254)` | Não | NOT NULL, UNIQUE | E-mail utilizado para cadastro e autenticação. |
| `senha_hash` | `VARCHAR(255)` | Não | NOT NULL | Hash da senha do usuário; não armazena a senha em texto puro. |
| `data_criacao` | `DATETIME` | Não | DEFAULT CURRENT_TIMESTAMP | Data e hora de criação do usuário. |
| `ativo` | `BOOLEAN` | Não | DEFAULT TRUE | Indica se o usuário está ativo no sistema. |

## 2. `objetivos`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_objetivo` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único do objetivo. |
| `id_usuario` | `BIGINT` | Não | FK → usuarios.id_usuario | Usuário proprietário do objetivo. |
| `titulo` | `VARCHAR(150)` | Não | NOT NULL | Título do objetivo. |
| `descricao` | `TEXT` | Sim | — | Descrição detalhada do objetivo. |
| `area` | `VARCHAR(50)` | Não | NOT NULL | Área da vida ou categoria à qual o objetivo pertence. |
| `motivo` | `TEXT` | Sim | — | Motivação ou razão para alcançar o objetivo. |
| `prazo` | `DATE` | Sim | — | Data prevista para alcance do objetivo. |
| `status` | `VARCHAR(20)` | Não | DEFAULT 'ATIVO' | Situação atual do objetivo. |
| `data_criacao` | `DATETIME` | Não | DEFAULT CURRENT_TIMESTAMP | Data e hora de criação do objetivo. |

## 3. `ciclos`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_ciclo` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único do ciclo. |
| `id_usuario` | `BIGINT` | Não | FK → usuarios.id_usuario | Usuário proprietário do ciclo. |
| `titulo` | `VARCHAR(120)` | Não | NOT NULL | Nome do ciclo de 12 semanas. |
| `data_inicio` | `DATE` | Não | NOT NULL | Data inicial do ciclo. |
| `data_fim` | `DATE` | Não | NOT NULL | Data final do ciclo. |
| `status` | `VARCHAR(20)` | Não | DEFAULT 'PLANEJADO' | Situação atual do ciclo. |
| `data_criacao` | `DATETIME` | Não | DEFAULT CURRENT_TIMESTAMP | Data e hora de criação do ciclo. |

## 4. `ciclos_objetivos`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_ciclo_objetivo` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único da associação. |
| `id_ciclo` | `BIGINT` | Não | FK → ciclos.id_ciclo | Ciclo da associação. |
| `id_objetivo` | `BIGINT` | Não | FK → objetivos.id_objetivo | Objetivo associado ao ciclo. |

Regra adicional: `UNIQUE (id_ciclo, id_objetivo)` impede associar o mesmo objetivo duas vezes ao mesmo ciclo.

## 5. `metas`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_meta` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único da meta. |
| `id_objetivo` | `BIGINT` | Não | FK → objetivos.id_objetivo | Objetivo ao qual a meta pertence. |
| `titulo` | `VARCHAR(150)` | Não | NOT NULL | Título da meta. |
| `descricao` | `TEXT` | Sim | — | Descrição detalhada da meta. |
| `prazo` | `DATE` | Sim | — | Prazo para conclusão da meta. |
| `status` | `VARCHAR(20)` | Não | DEFAULT 'PENDENTE' | Situação atual da meta. |
| `data_criacao` | `DATETIME` | Não | DEFAULT CURRENT_TIMESTAMP | Data e hora de criação da meta. |

## 6. `tarefas`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_tarefa` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único da tarefa. |
| `id_usuario` | `BIGINT` | Não | FK → usuarios.id_usuario | Usuário proprietário da tarefa. |
| `id_meta` | `BIGINT` | Sim | FK → metas.id_meta | Meta vinculada à tarefa, quando aplicável. |
| `id_objetivo` | `BIGINT` | Sim | FK → objetivos.id_objetivo | Objetivo vinculado diretamente à tarefa, quando aplicável. |
| `titulo` | `VARCHAR(180)` | Não | NOT NULL | Título da tarefa. |
| `descricao` | `TEXT` | Sim | — | Descrição detalhada da tarefa. |
| `classificacao_abcde` | `CHAR(1)` | Sim | CHECK A–E | Classificação de prioridade segundo o método ABCDE. |
| `data_planejada` | `DATE` | Sim | — | Data planejada para execução. |
| `prazo` | `DATETIME` | Sim | — | Data e hora limite para execução. |
| `tempo_estimado` | `INT` | Sim | CHECK > 0 | Tempo estimado para execução, em minutos. |
| `status` | `VARCHAR(20)` | Não | DEFAULT 'PENDENTE' | Situação atual da tarefa. |
| `data_criacao` | `DATETIME` | Não | DEFAULT CURRENT_TIMESTAMP | Data e hora de criação da tarefa. |
| `data_conclusao` | `DATETIME` | Sim | Atualizado por trigger | Data e hora em que a tarefa foi concluída. |

Regra adicional: `id_meta` e `id_objetivo` não podem estar preenchidos simultaneamente.

## 7. `prioridades_diarias`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_prioridade_diaria` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único da prioridade diária. |
| `id_usuario` | `BIGINT` | Não | FK → usuarios.id_usuario | Usuário que definiu a prioridade. |
| `id_tarefa` | `BIGINT` | Não | FK → tarefas.id_tarefa | Tarefa definida como Prioridade #1. |
| `data_prioridade` | `DATE` | Não | UNIQUE com id_usuario | Data da definição da prioridade. |

Regra adicional: `UNIQUE (id_usuario, data_prioridade)` garante uma única prioridade por usuário por dia.

## 8. `sessoes_foco`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_sessao_foco` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único da sessão de foco. |
| `id_usuario` | `BIGINT` | Não | FK → usuarios.id_usuario | Usuário que realizou a sessão. |
| `id_tarefa` | `BIGINT` | Sim | FK → tarefas.id_tarefa | Tarefa associada à sessão, quando houver. |
| `data_inicio` | `DATETIME` | Não | NOT NULL | Data e hora de início da sessão. |
| `data_fim` | `DATETIME` | Sim | — | Data e hora de término da sessão. |
| `tempo_foco_planejado` | `INT` | Não | CHECK > 0 | Tempo de foco planejado, em minutos. |
| `tempo_descanso_planejado` | `INT` | Não | CHECK > 0 | Tempo de descanso planejado, em minutos. |
| `tempo_foco_realizado` | `INT` | Não | DEFAULT 0, CHECK >= 0 | Tempo efetivamente focado, em minutos. |
| `status` | `VARCHAR(20)` | Não | DEFAULT 'EM_ANDAMENTO' | Estado atual da sessão de foco. |

## 9. `planejamentos_semanais`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_planejamento_semanal` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único do planejamento semanal. |
| `id_usuario` | `BIGINT` | Não | FK → usuarios.id_usuario | Usuário proprietário do planejamento. |
| `id_ciclo` | `BIGINT` | Sim | FK → ciclos.id_ciclo | Ciclo de 12 semanas associado, quando houver. |
| `semana_ciclo` | `TINYINT` | Sim | CHECK 1–12 | Número da semana dentro do ciclo. |
| `data_inicio_semana` | `DATE` | Não | NOT NULL | Data inicial do planejamento semanal. |
| `data_fim_semana` | `DATE` | Não | CHECK >= data_inicio_semana | Data final do planejamento semanal. |
| `data_criacao` | `DATETIME` | Não | DEFAULT CURRENT_TIMESTAMP | Data e hora de criação do planejamento. |

## 10. `planejamentos_tarefas`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_planejamento_tarefa` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único da associação. |
| `id_planejamento_semanal` | `BIGINT` | Não | FK → planejamentos_semanais.id_planejamento_semanal | Planejamento semanal da associação. |
| `id_tarefa` | `BIGINT` | Não | FK → tarefas.id_tarefa | Tarefa incluída no planejamento. |

Regra adicional: `UNIQUE (id_planejamento_semanal, id_tarefa)` impede duplicar uma tarefa no mesmo planejamento.

## 11. `revisoes_semanais`

| Campo | Tipo | Nulo? | Chave / Regra | Descrição |
|---|---|---:|---|---|
| `id_revisao_semanal` | `BIGINT` | Não | PK, AUTO_INCREMENT | Identificador único da revisão semanal. |
| `id_planejamento_semanal` | `BIGINT` | Não | FK, UNIQUE | Planejamento semanal ao qual a revisão pertence. |
| `score_execucao` | `DECIMAL(5,2)` | Sim | CHECK 0–100 | Percentual de execução das tarefas planejadas. |
| `principais_conquistas` | `TEXT` | Sim | — | Principais conquistas registradas na semana. |
| `dificuldades` | `TEXT` | Sim | — | Dificuldades percebidas durante a semana. |
| `ajustes_proxima_semana` | `TEXT` | Sim | — | Ajustes planejados para a semana seguinte. |
| `observacoes` | `TEXT` | Sim | — | Observações complementares da revisão. |
| `data_revisao` | `DATETIME` | Não | DEFAULT CURRENT_TIMESTAMP | Data e hora em que a revisão foi registrada. |

Regra adicional: `UNIQUE (id_planejamento_semanal)` garante no máximo uma revisão por planejamento.
