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

### 3.4 metas

Representa metas menores relacionadas aos objetivos.

Principais informações esperadas:
- id_meta
- id_objetivo
- titulo
- descricao
- prazo
- status

---

### 3.5 tarefas

Representa as ações executáveis do usuário.

Principais informações esperadas:
- id_tarefa
- id_usuario
- id_meta
- titulo
- descricao
- classificacao_abcde
- prioridade_principal
- data_planejada
- prazo
- tempo_estimado
- status

---

### 3.6 planejamentos_semanais

Representa o planejamento estratégico semanal do usuário.

Principais informações esperadas:
- id_planejamento_semanal
- id_usuario
- id_ciclo
- semana_ciclo
- data_inicio_semana
- data_fim_semana

---

### 3.7 sessoes_foco

Representa as sessões de foco realizadas no modo Pomodoro.

Principais informações esperadas:
- id_sessao_foco
- id_usuario
- id_tarefa
- inicio
- fim
- duracao_foco
- duracao_descanso
- status

---

### 3.8 revisoes_semanais

Representa as revisões realizadas pelo usuário ao final da semana.

Principais informações esperadas:
- id_revisao_semanal
- id_usuario
- id_ciclo
- semana_ciclo
- score_execucao
- observacoes
- data_revisao

---
## 4. Relacionamentos
- Um usuário pode possuir vários objetivos.
- Um usuário pode possuir vários ciclos.
- Um objetivo pode possuir várias metas.
- Uma meta pode possuir várias tarefas.
- Um usuário pode possuir várias tarefas.
- Um usuário pode possuir vários planejamentos semanais.
- Um ciclo pode possuir vários planejamentos semanais.
- Um usuário pode possuir várias sessões de foco.
- Uma tarefa pode possuir várias sessões de foco.
- Um usuário pode possuir várias revisões semanais.
- Um ciclo pode possuir várias revisões semanais.

---
## 5. Cardinalidades
- usuarios 1:N objetivos
- usuarios 1:N ciclos
- objetivos 1:N metas
- metas 1:N tarefas
- usuarios 1:N tarefas
- usuarios 1:N planejamentos_semanais
- ciclos 1:N planejamentos_semanais
- usuarios 1:N sessoes_foco
- tarefas 1:N sessoes_foco
- usuarios 1:N revisoes_semanais
- ciclos 1:N revisoes_semanais

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