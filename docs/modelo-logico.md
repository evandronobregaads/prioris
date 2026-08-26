# PRIORIS
## Modelo Lógico do Banco de Dados

### Legenda

- PK = Primary Key
- FK = Foreign Key
- NN = NOT NULL
- UQ = UNIQUE
- AI = AUTO_INCREMENT
- DF = DEFAULT
- CK = CHECK

## 1. usuarios

| Campo | Tipo | Restrições |
|---|---|---|
| id_usuario | BIGINT | PK, AI |
| nome | VARCHAR(120) | NN |
| email | VARCHAR(254) | NN, UQ |
| senha_hash | VARCHAR(255) | NN |
| data_criacao | DATETIME | NN, DF CURRENT_TIMESTAMP |
| ativo | BOOLEAN | NN, DF TRUE |

## 2. objetivos

| Campo | Tipo | Restrições |
|---|---|---|
| id_objetivo | BIGINT | PK, AI |
| id_usuario | BIGINT | FK, NN |
| titulo | VARCHAR(150) | NN |
| descricao | TEXT | |
| area | VARCHAR(50) | NN |
| motivo | TEXT | |
| prazo | DATE | |
| status | VARCHAR(20) | NN, DF 'ATIVO' |
| data_criacao | DATETIME | NN, DF CURRENT_TIMESTAMP |

FK: id_usuario → usuarios.id_usuario

## 3. ciclos

| Campo | Tipo | Restrições |
|---|---|---|
| id_ciclo | BIGINT | PK, AI |
| id_usuario | BIGINT | FK, NN |
| titulo | VARCHAR(120) | NN |
| data_inicio | DATE | NN |
| data_fim | DATE | NN |
| status | VARCHAR(20) | NN, DF 'PLANEJADO' |
| data_criacao | DATETIME | NN, DF CURRENT_TIMESTAMP |

FK: id_usuario → usuarios.id_usuario

## 4. ciclos_objetivos

| Campo | Tipo | Restrições |
|---|---|---|
| id_ciclo_objetivo | BIGINT | PK, AI |
| id_ciclo | BIGINT | FK, NN |
| id_objetivo | BIGINT | FK, NN |

### Restrição composta

UNIQUE (id_ciclo, id_objetivo)

FKs:
id_ciclo → ciclos.id_ciclo
id_objetivo → objetivos.id_objetivo

## 5. metas

| Campo | Tipo | Restrições |
|---|---|---|
| id_meta | BIGINT | PK, AI |
| id_objetivo | BIGINT | FK, NN |
| titulo | VARCHAR(150) | NN |
| descricao | TEXT | |
| prazo | DATE | |
| status | VARCHAR(20) | NN, DF 'PENDENTE' |
| data_criacao | DATETIME | NN, DF CURRENT_TIMESTAMP |

FK: id_objetivo → objetivos.id_objetivo

## 6. tarefas

| Campo | Tipo | Restrições |
|---|---|---|
| id_tarefa | BIGINT | PK, AI |
| id_usuario | BIGINT | FK, NN |
| id_meta | BIGINT | FK |
| id_objetivo | BIGINT | FK |
| titulo | VARCHAR(180) | NN |
| descricao | TEXT | |
| classificacao_abcde | CHAR(1) | CK |
| data_planejada | DATE | |
| prazo | DATETIME | |
| tempo_estimado | INT | CK |
| status | VARCHAR(20) | NN, DF 'PENDENTE' |
| data_criacao | DATETIME | NN, DF CURRENT_TIMESTAMP |
| data_conclusao | DATETIME | |

FK:
id_usuario → usuarios.id_usuario
id_meta → metas.id_meta
id_objetivo → objetivos.id_objetivo

## 7. prioridades_diarias

| Campo | Tipo | Restrições |
|---|---|---|
| id_prioridade_diaria | BIGINT | PK, AI |
| id_usuario | BIGINT | FK, NN |
| id_tarefa | BIGINT | FK, NN |
| data_prioridade | DATE | NN |

### Restrição composta

UNIQUE (id_usuario, data_prioridade)

FKs:
id_usuario → usuarios.id_usuario
id_tarefa → tarefas.id_tarefa

## 8. planejamentos_semanais

| Campo | Tipo | Restrições |
|---|---|---|
| id_planejamento_semanal | BIGINT | PK, AI |
| id_usuario | BIGINT | FK, NN |
| id_ciclo | BIGINT | FK |
| semana_ciclo | TINYINT | CK |
| data_inicio_semana | DATE | NN |
| data_fim_semana | DATE | NN |
| data_criacao | DATETIME | NN, DF CURRENT_TIMESTAMP |

FKs:
id_usuario → usuarios.id_usuario
id_ciclo → ciclos.id_ciclo

## 9. planejamentos_tarefas

| Campo | Tipo | Restrições |
|---|---|---|
| id_planejamento_tarefa | BIGINT | PK, AI |
| id_planejamento_semanal | BIGINT | FK, NN |
| id_tarefa | BIGINT | FK, NN |

### Restrição composta

UNIQUE (id_planejamento_semanal, id_tarefa)

FKs:
id_planejamento_semanal → planejamentos_semanais.id_planejamento_semanal
id_tarefa → tarefas.id_tarefa

## 10. sessoes_foco

| Campo | Tipo | Restrições |
|---|---|---|
| id_sessao_foco | BIGINT | PK, AI |
| id_usuario | BIGINT | FK, NN |
| id_tarefa | BIGINT | FK |
| data_inicio | DATETIME | NN |
| data_fim | DATETIME | |
| tempo_foco_planejado | INT | NN, CK |
| tempo_descanso_planejado | INT | NN, CK |
| tempo_foco_realizado | INT | NN, DF 0, CK |
| status | VARCHAR(20) | NN, DF 'EM_ANDAMENTO' |

FKs:
id_usuario → usuarios.id_usuario
id_tarefa → tarefas.id_tarefa

Checks:
tempo_foco_planejado > 0
tempo_descanso_planejado > 0
tempo_foco_realizado >= 0

## 11. revisoes_semanais

| Campo | Tipo | Restrições |
|---|---|---|
| id_revisao_semanal | BIGINT | PK, AI |
| id_planejamento_semanal | BIGINT | FK, NN, UQ |
| score_execucao | DECIMAL(5,2) | CK |
| principais_conquistas | TEXT | |
| dificuldades | TEXT | |
| ajustes_proxima_semana | TEXT | |
| observacoes | TEXT | |
| data_revisao | DATETIME | NN, DF CURRENT_TIMESTAMP |

FK: id_planejamento_semanal
→ planejamentos_semanais.id_planejamento_semanal

## 12. Domínios e valores previstos

### objetivos.status

- ATIVO
- CONCLUIDO
- PAUSADO
- CANCELADO

### ciclos.status

- PLANEJADO
- EM_ANDAMENTO
- CONCLUIDO
- CANCELADO

### metas.status

- PENDENTE
- EM_ANDAMENTO
- CONCLUIDA
- CANCELADA

### tarefas.status

- PENDENTE
- EM_ANDAMENTO
- CONCLUIDA
- CANCELADA
- ELIMINADA

### sessoes_foco.status

- EM_ANDAMENTO
- PAUSADA
- CONCLUIDA
- INTERROMPIDA

## 13. Campos calculados

Alguns valores utilizados pelo Prioris serão calculados dinamicamente
e não serão armazenados diretamente em tabelas.

### Score de Execução atual

Será calculado a partir da relação:

(tarefas estratégicas concluídas / tarefas estratégicas planejadas) × 100

O valor somente será persistido quando uma revisão semanal for concluída,
permitindo preservar o histórico daquele período.

### Semana atual do ciclo

Será calculada a partir da data de início do ciclo e da data atual.

### Progresso de tarefas

Será calculado com base nas tarefas planejadas e concluídas no período.