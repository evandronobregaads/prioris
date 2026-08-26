# PRIORIS
## Dicionário de Dados

Este documento descreve as tabelas, campos, tipos de dados e finalidades
dos elementos que compõem o banco de dados do sistema Prioris.

### Legenda

- PK: Chave Primária
- FK: Chave Estrangeira
- UQ: Valor único
- NN: Campo obrigatório
- AI: Auto incremento

## 1. Tabela `usuarios`

### Finalidade

Armazena as informações das contas cadastradas no Prioris.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_usuario | BIGINT | PK, AI | Sim | Identificador único do usuário |
| nome | VARCHAR(120) | NN | Sim | Nome do usuário |
| email | VARCHAR(254) | UQ, NN | Sim | E-mail utilizado para autenticação |
| senha_hash | VARCHAR(255) | NN | Sim | Hash da senha utilizada na autenticação |
| data_criacao | DATETIME | DEFAULT CURRENT_TIMESTAMP | Sim | Data e hora em que a conta foi criada |
| ativo | BOOLEAN | DEFAULT TRUE | Sim | Indica se a conta está ativa no sistema |

## 2. Tabela `objetivos`

### Finalidade

Armazena os objetivos pessoais ou profissionais cadastrados pelo usuário.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_objetivo | BIGINT | PK, AI | Sim | Identificador único do objetivo |
| id_usuario | BIGINT | FK | Sim | Identifica o usuário proprietário do objetivo |
| titulo | VARCHAR(150) | NN | Sim | Nome ou título do objetivo |
| descricao | TEXT |  | Não | Descrição detalhada do objetivo |
| area | VARCHAR(50) | NN | Sim | Área da vida ou interesse relacionada ao objetivo |
| motivo | TEXT |  | Não | Razão pela qual o objetivo é importante para o usuário |
| prazo | DATE |  | Não | Data prevista para alcance do objetivo |
| status | VARCHAR(20) | DEFAULT 'ATIVO' | Sim | Situação atual do objetivo |
| data_criacao | DATETIME | DEFAULT CURRENT_TIMESTAMP | Sim | Data de criação do objetivo |

### Chave estrangeira

`id_usuario` referencia `usuarios.id_usuario`.

## 3. Tabela `ciclos`

### Finalidade

Armazena os ciclos de planejamento de 12 semanas criados pelo usuário.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_ciclo | BIGINT | PK, AI | Sim | Identificador único do ciclo |
| id_usuario | BIGINT | FK | Sim | Usuário proprietário do ciclo |
| titulo | VARCHAR(120) | NN | Sim | Nome atribuído ao ciclo |
| data_inicio | DATE | NN | Sim | Data de início do ciclo |
| data_fim | DATE | NN | Sim | Data prevista para encerramento do ciclo |
| status | VARCHAR(20) | DEFAULT 'PLANEJADO' | Sim | Situação atual do ciclo |
| data_criacao | DATETIME | DEFAULT CURRENT_TIMESTAMP | Sim | Data de criação do registro |

### Chave estrangeira

`id_usuario` referencia `usuarios.id_usuario`.

## 4. Tabela `ciclos_objetivos`

### Finalidade

Tabela associativa responsável por relacionar os objetivos aos ciclos
de planejamento de 12 semanas.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_ciclo_objetivo | BIGINT | PK, AI | Sim | Identificador da associação |
| id_ciclo | BIGINT | FK | Sim | Ciclo relacionado |
| id_objetivo | BIGINT | FK | Sim | Objetivo relacionado |

### Chaves estrangeiras

`id_ciclo` referencia `ciclos.id_ciclo`.

`id_objetivo` referencia `objetivos.id_objetivo`.

### Restrição de unicidade

A combinação `id_ciclo + id_objetivo` deverá ser única, impedindo que
o mesmo objetivo seja associado mais de uma vez ao mesmo ciclo.

## 5. Tabela `metas`

### Finalidade

Armazena metas menores utilizadas para dividir um objetivo em resultados
mais específicos e acompanháveis.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_meta | BIGINT | PK, AI | Sim | Identificador único da meta |
| id_objetivo | BIGINT | FK | Sim | Objetivo ao qual a meta pertence |
| titulo | VARCHAR(150) | NN | Sim | Título da meta |
| descricao | TEXT |  | Não | Descrição detalhada |
| prazo | DATE |  | Não | Data prevista para conclusão |
| status | VARCHAR(20) | DEFAULT 'PENDENTE' | Sim | Situação atual da meta |
| data_criacao | DATETIME | DEFAULT CURRENT_TIMESTAMP | Sim | Data de criação |

### Chave estrangeira

`id_objetivo` referencia `objetivos.id_objetivo`.

## 6. Tabela `tarefas`

### Finalidade

Armazena as atividades executáveis cadastradas pelo usuário.

As tarefas representam as ações práticas que contribuem para metas
ou objetivos do Prioris.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_tarefa | BIGINT | PK, AI | Sim | Identificador único da tarefa |
| id_usuario | BIGINT | FK | Sim | Usuário proprietário da tarefa |
| id_meta | BIGINT | FK | Não | Meta relacionada à tarefa |
| id_objetivo | BIGINT | FK | Não | Objetivo diretamente relacionado à tarefa |
| titulo | VARCHAR(180) | NN | Sim | Título da tarefa |
| descricao | TEXT |  | Não | Descrição detalhada |
| classificacao_abcde | CHAR(1) | CHECK | Não | Classificação de prioridade A, B, C, D ou E |
| data_planejada | DATE |  | Não | Data em que a tarefa está planejada para execução |
| prazo | DATETIME |  | Não | Data e horário limite, quando aplicável |
| tempo_estimado | INT | CHECK | Não | Tempo estimado para execução em minutos |
| status | VARCHAR(20) | DEFAULT 'PENDENTE' | Sim | Situação atual da tarefa |
| data_criacao | DATETIME | DEFAULT CURRENT_TIMESTAMP | Sim | Data e hora de criação |
| data_conclusao | DATETIME |  | Não | Data e hora em que a tarefa foi concluída |

### Chaves estrangeiras

`id_usuario` referencia `usuarios.id_usuario`.

`id_meta` referencia `metas.id_meta`.

`id_objetivo` referencia `objetivos.id_objetivo`.

### Regras

`classificacao_abcde` poderá possuir apenas os valores A, B, C, D ou E.

`tempo_estimado`, quando informado, deverá ser maior que zero.

Uma tarefa poderá ser relacionada a uma meta OU diretamente a um
objetivo, mas não simultaneamente aos dois.

## 7. Tabela `prioridades_diarias`

### Finalidade

Registra qual tarefa foi definida como prioridade principal do usuário
em determinada data.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_prioridade_diaria | BIGINT | PK, AI | Sim | Identificador do registro |
| id_usuario | BIGINT | FK | Sim | Usuário responsável pela escolha |
| id_tarefa | BIGINT | FK | Sim | Tarefa definida como prioridade |
| data_prioridade | DATE | NN | Sim | Data em que a tarefa foi definida como prioridade |

### Chaves estrangeiras

`id_usuario` referencia `usuarios.id_usuario`.

`id_tarefa` referencia `tarefas.id_tarefa`.

### Restrição de unicidade

A combinação `id_usuario + data_prioridade` deverá ser única.

Essa regra garante que cada usuário possa possuir apenas uma prioridade
principal para cada dia.

## 8. Tabela `planejamentos_semanais`

### Finalidade

Armazena os planejamentos semanais criados pelo usuário, podendo estar
associados a um ciclo de 12 semanas.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_planejamento_semanal | BIGINT | PK, AI | Sim | Identificador do planejamento |
| id_usuario | BIGINT | FK | Sim | Usuário proprietário do planejamento |
| id_ciclo | BIGINT | FK | Não | Ciclo de 12 semanas relacionado |
| semana_ciclo | TINYINT | CHECK | Não | Número da semana dentro do ciclo |
| data_inicio_semana | DATE | NN | Sim | Data inicial da semana |
| data_fim_semana | DATE | NN | Sim | Data final da semana |
| data_criacao | DATETIME | DEFAULT CURRENT_TIMESTAMP | Sim | Data de criação do planejamento |

### Chaves estrangeiras

`id_usuario` referencia `usuarios.id_usuario`.

`id_ciclo` referencia `ciclos.id_ciclo`.

### Regra

`semana_ciclo`, quando informado, deverá possuir valor entre 1 e 12.

## 9. Tabela `planejamentos_tarefas`

### Finalidade

Tabela associativa utilizada para definir quais tarefas estratégicas
fazem parte de determinado planejamento semanal.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_planejamento_tarefa | BIGINT | PK, AI | Sim | Identificador da associação |
| id_planejamento_semanal | BIGINT | FK | Sim | Planejamento semanal relacionado |
| id_tarefa | BIGINT | FK | Sim | Tarefa estratégica relacionada |

### Chaves estrangeiras

`id_planejamento_semanal` referencia
`planejamentos_semanais.id_planejamento_semanal`.

`id_tarefa` referencia `tarefas.id_tarefa`.

### Restrição de unicidade

A combinação `id_planejamento_semanal + id_tarefa` deverá ser única.

## 10. Tabela `sessoes_foco`

### Finalidade

Armazena os registros das sessões de foco realizadas pelo usuário através
do temporizador do Prioris.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_sessao_foco | BIGINT | PK, AI | Sim | Identificador da sessão |
| id_usuario | BIGINT | FK | Sim | Usuário que realizou a sessão |
| id_tarefa | BIGINT | FK | Não | Tarefa relacionada à sessão |
| data_inicio | DATETIME | NN | Sim | Data e hora de início |
| data_fim | DATETIME |  | Não | Data e hora de encerramento |
| tempo_foco_planejado | INT | CHECK | Sim | Tempo de foco inicialmente planejado, em minutos |
| tempo_descanso_planejado | INT | CHECK | Sim | Tempo de descanso planejado, em minutos |
| tempo_foco_realizado | INT | DEFAULT 0, CHECK | Sim | Tempo de foco efetivamente realizado |
| status | VARCHAR(20) | DEFAULT 'EM_ANDAMENTO' | Sim | Situação atual da sessão |

### Chaves estrangeiras

`id_usuario` referencia `usuarios.id_usuario`.

`id_tarefa` referencia `tarefas.id_tarefa`.

### Regras

`tempo_foco_planejado` deverá ser maior que zero.

`tempo_descanso_planejado` deverá ser maior que zero.

`tempo_foco_realizado` deverá ser maior ou igual a zero.

A sessão poderá existir sem estar associada a uma tarefa.

## 11. Tabela `revisoes_semanais`

### Finalidade

Armazena a avaliação realizada pelo usuário sobre a execução de determinado
planejamento semanal.

| Campo | Tipo | Chave/Restrição | Obrigatório | Descrição |
|---|---|---|---|---|
| id_revisao_semanal | BIGINT | PK, AI | Sim | Identificador da revisão |
| id_planejamento_semanal | BIGINT | FK, UQ | Sim | Planejamento semanal analisado |
| score_execucao | DECIMAL(5,2) | CHECK | Não | Percentual consolidado de execução da semana |
| principais_conquistas | TEXT |  | Não | Principais resultados e conquistas percebidos |
| dificuldades | TEXT |  | Não | Dificuldades encontradas durante a semana |
| ajustes_proxima_semana | TEXT |  | Não | Mudanças planejadas para a próxima semana |
| observacoes | TEXT |  | Não | Observações adicionais |
| data_revisao | DATETIME | DEFAULT CURRENT_TIMESTAMP | Sim | Data e hora da revisão |

### Chave estrangeira

`id_planejamento_semanal` referencia
`planejamentos_semanais.id_planejamento_semanal`.

### Regras

Cada planejamento semanal poderá possuir no máximo uma revisão.

`score_execucao`, quando disponível, deverá possuir valor entre 0 e 100.

## 12. Observações Gerais

Os dados pertencentes ao usuário deverão ser isolados de forma que um
usuário não possa consultar ou modificar registros pertencentes a outro.

Campos derivados, como o progresso atual, a semana atual de um ciclo e
o Score de Execução corrente, deverão preferencialmente ser calculados
a partir dos registros existentes.

O Score de Execução somente será persistido na tabela de revisões quando
representar o resultado consolidado daquele período.

As regras de integridade deverão ser implementadas tanto no banco de dados,
quando possível, quanto na camada de negócio da aplicação.

