# Auditoria do Banco de Dados — PRIORIS

## Arquivos SQL existentes

- `schema.sql` — criação do banco, tabelas, PKs, FKs e constraints.
- `seed.sql` — dados iniciais para desenvolvimento e demonstração.
- `queries.sql` — três consultas SQL com JOIN.
- `views.sql` — criação da view `vw_dashboard_tarefas`.
- `routines.sql` — criação da trigger `trg_tarefas_data_conclusao`.

## Conferência estrutural

| Item | Resultado |
|---|---:|
| Tabelas | 11 |
| Campos | 76 |
| Chaves primárias | 11 |
| Chaves estrangeiras | 17 |
| Constraints UNIQUE | 5 |
| Constraints CHECK | 9 |
| Campos NOT NULL | 43 |
| Campos com DEFAULT | 14 |

## Requisitos de Banco de Dados do Projeto Final

| Requisito | Situação | Evidência |
|---|---|---|
| DER/MER | ✅ | `01_DER_MER_Prioris.md` |
| PKs e FKs identificadas | ✅ | DER + Modelo Lógico + `schema.sql` |
| Cardinalidades | ✅ | DER/MER |
| Modelo lógico | ✅ | `02_Modelo_Logico_Prioris.md` |
| Dicionário de dados | ✅ | `03_Dicionario_Dados_Prioris.md` |
| Mínimo de 4 tabelas relacionadas | ✅ | 11 tabelas |
| NOT NULL / UNIQUE / DEFAULT / CHECK | ✅ | `schema.sql` |
| Integridade referencial com FK | ✅ | 17 FKs |
| Script DDL | ✅ | `schema.sql` |
| Script DML | ✅ | `seed.sql` |
| 3 consultas com JOIN | ✅ | `queries.sql` |
| 1 VIEW | ✅ | `vw_dashboard_tarefas` em `views.sql` |
| 1 procedure ou trigger | ✅ | `trg_tarefas_data_conclusao` em `routines.sql` |

## Consultas com JOIN

### Query 01 — Planejamento semanal completo
Combina:
- `planejamentos_semanais`
- `planejamentos_tarefas`
- `tarefas`
- `metas`
- `objetivos`

### Query 02 — Prioridade do dia e tempo de foco
Combina:
- `usuarios`
- `prioridades_diarias`
- `tarefas`
- `sessoes_foco`

### Query 03 — Evolução dos objetivos dentro do ciclo
Combina:
- `ciclos`
- `ciclos_objetivos`
- `objetivos`
- `metas`
- `tarefas`

## View

### `vw_dashboard_tarefas`

Consolida informações de usuário, tarefa, meta, objetivo, prioridade do dia e minutos de foco para apoiar a visualização do dashboard.

## Trigger

### `trg_tarefas_data_conclusao`

Executada antes de um `UPDATE` em `tarefas`.

- Quando o status muda para `CONCLUIDA`, define `data_conclusao = CURRENT_TIMESTAMP`.
- Quando uma tarefa deixa o status `CONCLUIDA`, limpa `data_conclusao`.

## Ordem recomendada para executar os scripts

```text
1. schema.sql
2. seed.sql
3. views.sql
4. routines.sql
5. queries.sql
```

`queries.sql` contém consultas de conferência e demonstração, portanto não precisa ser executado para criar a estrutura do banco.
