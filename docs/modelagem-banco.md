# PRIORIS
## Modelagem de Banco de Dados

---

## 1. Objetivo da Modelagem

Definir a estrutura conceitual e lógica do banco de dados do sistema Prioris,
identificando as principais entidades, atributos e relacionamentos necessários
para o funcionamento da aplicação.

---

## 2. Levantamento Inicial de Entidades
As entidades inicialmente identificadas para o sistema Prioris são:

- usuários
- objetivos
- ciclos de 12 semanas
- metas
- tarefas
- sessões de foco
- revisões semanais
- planejamentos semanais

---
## 3. Entidades Confirmadas
### 3.1 usuarios

Representa as contas cadastradas no sistema.

Principais informações esperadas:
- id_usuario
- nome
- email
- senha
- data_criacao

---

### 3.2 objetivos

Representa os objetivos do usuário, pessoais ou profissionais.

Principais informações esperadas:
- id_objetivo
- id_usuario
- titulo
- descricao
- area
- motivo
- prazo
- status

---

### 3.3 ciclos

Representa os ciclos de planejamento de 12 semanas.

Principais informações esperadas:
- id_ciclo
- id_usuario
- titulo
- data_inicio
- data_fim
- status

---

### 3.4 ciclos_objetivos

Representa a associação entre os ciclos de 12 semanas e os objetivos trabalhados durante cada ciclo.

Principais informações esperadas:

- id_ciclo_objetivo
- id_ciclo
- id_objetivo

---

### 3.5 metas

Representa metas menores relacionadas aos objetivos.

Principais informações esperadas:
- id_meta
- id_objetivo
- titulo
- descricao
- prazo
- status

---

### 3.6 tarefas

Representa as ações executáveis do usuário.

Principais informações esperadas:
- id_tarefa
- id_usuario
- id_meta
- id_objetivo
- titulo
- descricao
- classificacao_abcde
- data_planejada
- prazo
- tempo_estimado
- status

---

### 3.7 prioridades_diarias

Representa a tarefa escolhida como prioridade do usuário em determinada data. 

Principais informações esperadas:
- id_prioridade_diaria
- id_usuario
- id_tarefa
- data_prioridade

---

### 3.8 planejamentos_semanais

Representa o planejamento estratégico semanal do usuário.

Principais informações esperadas:
- id_planejamento_semanal
- id_usuario
- id_ciclo
- semana_ciclo
- data_inicio_semana
- data_fim_semana

---

### 3.9 planejamento_tarefas

Representa as tarefas estratégicas selecionadas para determinado planejamento semanal.

Principais informações esperadas:
- id_planejamento_tarefa
- id_planejamento_semanal
- id_tarefa

---

### 3.10 sessoes_foco

Representa as sessões de foco realizadas no modo Pomodoro.

Principais informações esperadas:
- id_sessao_foco
- id_usuario
- id_tarefa
- data_inicio
- data_fim
- tempo_foco_planejado
- tempo_descanso_planejado
- tempo_foco_realizado
- status

---

### 3.11 revisoes_semanais

Representa as revisões realizadas pelo usuário ao final da semana.

Principais informações esperadas:
- id_revisao_semanal
- id_usuario
- id_ciclo
- id_planejamento_semanal
- semana_ciclo
- score_execucao
- principais_conquistas
- dificuldades
- ajustes_proxima_semana
- observacoes
- data_revisao

---

## 4. Relacionamentos
- Um usuário pode possuir vários objetivos.
- Um usuário pode possuir vários ciclos.
- Um ciclo pode possuir vários objetivos.
- Um objetivo pode participar de vários ciclos.
- Um objetivo pode possuir várias metas.
- Uma meta pode possuir várias tarefas.
- Um objetivo pode possuir várias tarefas.
- Um usuário pode possuir várias tarefas.
- Um usuário pode possuir várias prioridades diárias.
- Uma tarefa pode ser definida como prioridade em diferentes datas.
- Um usuário pode possuir vários planejamentos semanais.
- Um ciclo pode possuir vários planejamentos semanais.
- Um planejamento semanal pode possuir várias tarefas.
- Uma tarefa pode participar de vários planejamentos semanais. 
- Um usuário pode possuir várias sessões de foco.
- Uma tarefa pode possuir várias sessões de foco.
- Um usuário pode possuir várias revisões semanais.
- Um planejamento semanal poderá possuir uma revisão semanal.

---
## 5. Cardinalidades
- usuarios 1:N objetivos
- usuarios 1:N ciclos
- ciclos N:M objetivos, através de ciclos_objetivos
- objetivos 1:N metas
- metas 1:N tarefas
- usuarios 1:N tarefas
- usuarios 1:N prioridades_diarias
- tarefas 1:N prioridades_diarias
- usuarios 1:N planejamentos_semanais
- ciclos 1:N planejamentos_semanais
- planejamentos_semanais N:M tarefas, através de planejamento_tarefas
- usuarios 1:N sessoes_foco
- tarefas 1:N sessoes_foco
- usuarios 1:N revisoes_semanais
- planejamentos_semanais 1:0..1 revisoes_semanais

---
## 6. Observações de Modelagem
- A classificação ABCDE será armazenada na entidade tarefas.
- A prioridade principal será tratada inicialmente como atributo da tarefa.
- O cálculo do Score de Execução poderá ser armazenado na revisão semanal.
- A integração com o YouTube não exige tabela própria nesta primeira versão,
  pois será uma integração de comportamento no front-end.
- O conteúdo reproduzido no YouTube poderá ser tratado futuramente, se necessário,
  sem impactar a estrutura principal do banco.

---

## 7. Estrutura Preliminar das Entidades

### Legenda:

- PK: Primary Key (Chave Primária)
- FK: Foreign Key (Chave Estrangeira)
- NN: NOT NULL (Campo Obrigatório)
- UQ: UNIQUE (Valor único)
- AI: AUTO_INCREMENT
- CK: CHECK (Restrição de domínio)

### 7.1 usuarios

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_usuario | BIGINT | PK, AI | Identificador único do usuário |
| nome | VARCHAR(120) | NN | Nome do usuário |
| email | VARCHAR(254) | NN, UQ | E-mail utilizado para autenticação |
| senha | VARCHAR(255) | NN | Hash da senha do usuário |
| data_criacao | DATETIME | NN | Data e hora de criação da conta |
| ativo | BOOLEAN | NN | Indica se a conta está ativa |

### 7.2 objetivos

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_objetivo | BIGINT | PK, AI | Identificador único do objetivo |
| id_usuario | BIGINT | FK, NN | Usuário proprietário do objetivo |
| titulo | VARCHAR(150) | NN | Título do objetivo |
| descricao | TEXT |  | Descrição detalhada |
| area | VARCHAR(50) | NN | Área à qual o objetivo pertence |
| motivo | TEXT |  | Razão pela qual o objetivo é importante |
| prazo | DATE |  | Data prevista para alcance |
| status | VARCHAR(20) | NN | Situação atual do objetivo |
| data_criacao | DATETIME | NN | Data e hora de criação |

### 7.3 ciclos

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_ciclo | BIGINT | PK, AI | Identificador único do ciclo |
| id_usuario | BIGINT | FK, NN | Usuário proprietário do ciclo |
| titulo | VARCHAR(120) | NN | Nome atribuído ao ciclo |
| data_inicio | DATE | NN | Data inicial do ciclo |
| data_fim | DATE | NN | Data final calculada para o ciclo |
| status | VARCHAR(20) | NN | Situação atual do ciclo |
| data_criacao | DATETIME | NN | Data de criação |

### 7.4 ciclos_objetivos

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_ciclo_objetivo | BIGINT | PK, AI | Identificador da associação |
| id_ciclo | BIGINT | FK, NN | Ciclo relacionado |
| id_objetivo | BIGINT | FK, NN | Objetivo relacionado |

Restrição adicional:

- A combinação `id_ciclo + id_objetivo` deverá ser única.

### 7.5 metas

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_meta | BIGINT | PK, AI | Identificador único da meta |
| id_objetivo | BIGINT | FK, NN | Objetivo ao qual a meta pertence |
| titulo | VARCHAR(150) | NN | Título da meta |
| descricao | TEXT |  | Descrição da meta |
| prazo | DATE |  | Data prevista |
| status | VARCHAR(20) | NN | Situação da meta |
| data_criacao | DATETIME | NN | Data e hora de criação |

### 7.6 tarefas

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_tarefa | BIGINT | PK, AI | Identificador único da tarefa |
| id_usuario | BIGINT | FK, NN | Usuário proprietário da tarefa |
| id_meta | BIGINT | FK | Meta relacionada, quando aplicável |
| id_objetivo | BIGINT | FK | Objetivo relacionado diretamente, quando aplicável |
| titulo | VARCHAR(180) | NN | Título da tarefa |
| descricao | TEXT |  | Descrição detalhada |
| classificacao_abcde | CHAR(1) | CK | Classificação A, B, C, D ou E |
| data_planejada | DATE |  | Data planejada para execução |
| prazo | DATETIME |  | Prazo da tarefa |
| tempo_estimado | INT | CK | Tempo estimado em minutos |
| status | VARCHAR(20) | NN | Situação da tarefa |
| data_criacao | DATETIME | NN | Data e hora de criação |
| data_conclusao | DATETIME |  | Data e hora da conclusão |

### 7.7 prioridades_diarias

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_prioridade_diaria | BIGINT | PK, AI | Identificador do registro |
| id_usuario | BIGINT | FK, NN | Usuário responsável |
| id_tarefa | BIGINT | FK, NN | Tarefa definida como prioridade |
| data_prioridade | DATE | NN | Data em que a tarefa foi definida como prioridade |

Restrição adicional:

- A combinação `id_usuario + data_prioridade` deverá ser única.

### 7.8 planejamentos_semanais

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_planejamento_semanal | BIGINT | PK, AI | Identificador do planejamento |
| id_usuario | BIGINT | FK, NN | Usuário proprietário |
| id_ciclo | BIGINT | FK | Ciclo relacionado |
| semana_ciclo | TINYINT | CK | Número da semana dentro do ciclo |
| data_inicio_semana | DATE | NN | Início da semana |
| data_fim_semana | DATE | NN | Final da semana |
| data_criacao | DATETIME | NN | Data de criação |

Restrição:

- `semana_ciclo`, quando informada, deverá possuir valor entre 1 e 12.

### 7.9 planejamento_tarefas

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_planejamento_tarefa | BIGINT | PK, AI | Identificador da associação |
| id_planejamento_semanal | BIGINT | FK, NN | Planejamento semanal relacionado |
| id_tarefa | BIGINT | FK, NN | Tarefa estratégica selecionada |

Restrição adicional:

- A combinação `id_planejamento_semanal + id_tarefa` deverá ser única.

### 7.10 sessoes_foco

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_sessao_foco | BIGINT | PK, AI | Identificador da sessão |
| id_usuario | BIGINT | FK, NN | Usuário responsável |
| id_tarefa | BIGINT | FK | Tarefa relacionada |
| data_inicio | DATETIME | NN | Início da sessão |
| data_fim | DATETIME |  | Encerramento da sessão |
| tempo_foco_planejado | INT | NN, CK | Tempo de foco planejado em minutos |
| tempo_descanso_planejado | INT | NN, CK | Tempo de descanso planejado em minutos |
| tempo_foco_realizado | INT | CK | Tempo efetivamente realizado em minutos |
| status | VARCHAR(20) | NN | Situação da sessão |

Restrição:
tempo_foco_planejado > 0
tempo_descanso_planejado > 0
tempo_foco_realizado >= 0

### 7.11 revisoes_semanais

| Campo | Tipo | Restrições | Descrição |
|---|---|---|---|
| id_revisao_semanal | BIGINT | PK, AI | Identificador da revisão |
| id_planejamento_semanal | BIGINT | FK, NN, UQ | Planejamento semanal analisado |
| score_execucao | DECIMAL(5,2) | CK | Percentual consolidado de execução |
| principais_conquistas | TEXT |  | Principais resultados percebidos |
| dificuldades | TEXT |  | Dificuldades encontradas |
| ajustes_proxima_semana | TEXT |  | Ajustes planejados |
| observacoes | TEXT |  | Observações adicionais |
| data_revisao | DATETIME | NN | Data e hora da revisão |

Restrição:

- O `score_execucao`, quando informado, deverá possuir valor entre 0 e 100.
- Cada planejamento semanal poderá possuir no máximo uma revisão semanal.

## 8. Validação das Cardinalidades

### 8.1 usuarios e objetivos

Um usuário pode possuir zero ou vários objetivos.

Cada objetivo deverá pertencer obrigatoriamente a um único usuário.

Cardinalidade:

usuarios 1 : 0..N objetivos

### 8.2 usuarios e ciclos

Um usuário pode possuir zero ou vários ciclos de planejamento.

Cada ciclo deverá pertencer obrigatoriamente a um único usuário.

Cardinalidade:

usuarios 1 : 0..N ciclos

### 8.3 ciclos e objetivos

Um ciclo pode estar relacionado a vários objetivos.

Um objetivo pode participar de vários ciclos.

Essa relação N:M será resolvida pela entidade associativa
`ciclos_objetivos`.

Cardinalidades:

ciclos 1 : 0..N ciclos_objetivos

objetivos 1 : 0..N ciclos_objetivos

### 8.4 objetivos e metas

Um objetivo pode possuir zero ou várias metas.

Cada meta deverá pertencer obrigatoriamente a um único objetivo.

Cardinalidade:

objetivos 1 : 0..N metas

### 8.5 metas e tarefas

Uma meta pode possuir zero ou várias tarefas.

Uma tarefa poderá estar vinculada a zero ou uma meta.

Cardinalidade:

metas 1 : 0..N tarefas

Do ponto de vista da tarefa:

tarefas 0..1 : 1 metas

### 8.6 objetivos e tarefas

Um objetivo pode possuir zero ou várias tarefas diretamente relacionadas.

Uma tarefa poderá estar diretamente relacionada a zero ou um objetivo.

Quando uma tarefa estiver vinculada a uma meta, seu objetivo poderá ser
obtido através da própria meta.

A tarefa não deverá ser vinculada simultaneamente a uma meta e diretamente
a um objetivo.

### 8.7 usuarios e tarefas

Um usuário pode possuir zero ou várias tarefas.

Cada tarefa deverá pertencer obrigatoriamente a um único usuário.

Cardinalidade:

usuarios 1 : 0..N tarefas

### 8.8 usuarios, tarefas e prioridades_diarias

Um usuário pode possuir vários registros de prioridade diária ao longo
do tempo.

Uma tarefa poderá ter sido escolhida como prioridade em diferentes datas.

Cada registro de prioridade diária deverá estar relacionado a um usuário
e a uma única tarefa.

Cardinalidades:

usuarios 1 : 0..N prioridades_diarias

tarefas 1 : 0..N prioridades_diarias

### 8.9 usuarios, ciclos e planejamentos_semanais

Um usuário pode possuir vários planejamentos semanais.

Cada planejamento semanal pertence obrigatoriamente a um usuário.

Um ciclo pode possuir vários planejamentos semanais.

Um planejamento semanal poderá estar associado a zero ou um ciclo.

Cardinalidades:

usuarios 1 : 0..N planejamentos_semanais

ciclos 1 : 0..N planejamentos_semanais

### 8.10 planejamentos_semanais e tarefas

Um planejamento semanal pode possuir várias tarefas estratégicas.

Uma tarefa poderá fazer parte de diferentes planejamentos semanais ao
longo do tempo.

Essa relação N:M será resolvida pela entidade associativa
`planejamento_tarefas`.

Cardinalidades:

planejamentos_semanais 1 : 0..N planejamento_tarefas

tarefas 1 : 0..N planejamento_tarefas

### 8.11 usuarios, tarefas e sessoes_foco

Um usuário pode possuir várias sessões de foco.

Cada sessão de foco deverá pertencer obrigatoriamente a um usuário.

Uma tarefa pode possuir várias sessões de foco.

Uma sessão de foco poderá existir sem estar vinculada a uma tarefa.

Cardinalidades:

usuarios 1 : 0..N sessoes_foco

tarefas 1 : 0..N sessoes_foco

### 8.12 planejamentos_semanais e revisoes_semanais

Um planejamento semanal poderá possuir zero ou uma revisão semanal.

Cada revisão semanal deverá obrigatoriamente estar associada a um único
planejamento semanal.

Cardinalidade:

planejamentos_semanais 1 : 0..1 revisoes_semanais