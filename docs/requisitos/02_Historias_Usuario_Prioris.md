# Histórias de Usuário — PRIORIS

> O requisito do Projeto Final exige pelo menos 5 casos de uso ou histórias de usuário.  
> O Prioris documenta 12 histórias de usuário para representar seu fluxo principal.

## HU-001 — Cadastro

**Como** novo usuário,  
**quero** criar uma conta com nome, e-mail e senha,  
**para** acessar os recursos do Prioris.

### Critérios de aceitação
- Nome, e-mail e senha devem ser validados.
- E-mail duplicado deve ser rejeitado.
- A senha deve ser armazenada com BCrypt.

---

## HU-002 — Login

**Como** usuário cadastrado,  
**quero** realizar login com meu e-mail e senha,  
**para** acessar meus dados e planejamentos.

### Critérios de aceitação
- Credenciais válidas permitem o acesso.
- Credenciais inválidas retornam mensagem de erro.
- A V1 não depende de JWT.

---

## HU-003 — Objetivos

**Como** usuário,  
**quero** cadastrar meus objetivos de médio e longo prazo,  
**para** visualizar onde quero chegar.

### Critérios de aceitação
- Deve ser possível informar título, área, descrição, motivo e prazo.
- Deve ser possível editar o objetivo.
- Deve ser possível concluir/cancelar o objetivo.

---

## HU-004 — Metas

**Como** usuário,  
**quero** dividir um objetivo em metas menores,  
**para** tornar sua execução mais clara.

### Critérios de aceitação
- A meta deve pertencer a um objetivo.
- Deve ser possível atualizar seu status.
- Deve ser possível cancelar a meta.

---

## HU-005 — Tarefas Estratégicas

**Como** usuário,  
**quero** cadastrar e classificar tarefas pelo método ABCDE,  
**para** diferenciar atividades mais e menos importantes.

### Critérios de aceitação
- A classificação deve aceitar A, B, C, D ou E.
- A tarefa pode possuir Meta, Objetivo ou nenhum vínculo.
- Meta e Objetivo não podem estar preenchidos simultaneamente.

---

## HU-006 — Prioridade #1

**Como** usuário,  
**quero** escolher minha tarefa mais importante do dia,  
**para** saber o que deve receber minha atenção principal.

### Critérios de aceitação
- Só pode existir uma prioridade por usuário em cada data.
- Deve ser possível alterar a prioridade.
- Deve ser possível remover a prioridade.

---

## HU-007 — Ciclo de 12 Semanas

**Como** usuário,  
**quero** organizar objetivos dentro de um ciclo de 12 semanas,  
**para** concentrar minha execução em um período definido.

### Critérios de aceitação
- Deve ser possível criar o ciclo.
- Deve ser possível associar objetivos ao ciclo.
- Remover a associação não pode excluir o objetivo.

---

## HU-008 — Planejamento Semanal

**Como** usuário,  
**quero** selecionar as tarefas estratégicas da semana,  
**para** definir antecipadamente o que preciso executar.

### Critérios de aceitação
- Um planejamento pertence a um usuário.
- Pode ser associado a um ciclo.
- Pode conter várias tarefas.
- A mesma tarefa não pode aparecer duplicada no planejamento.

---

## HU-009 — Score de Execução

**Como** usuário,  
**quero** visualizar meu percentual de execução semanal,  
**para** avaliar se estou cumprindo o que planejei.

### Critérios de aceitação
- O score deve considerar tarefas planejadas e concluídas.
- O valor deve ser apresentado em percentual.

---

## HU-010 — Sessão de Foco

**Como** usuário,  
**quero** iniciar uma sessão de foco com cronômetro,  
**para** executar uma atividade sem distrações.

### Critérios de aceitação
- A sessão pode ter ou não tarefa associada.
- Deve permitir pausa e retomada.
- Deve permitir conclusão e interrupção.
- Deve registrar o tempo de foco realizado.

---

## HU-011 — Revisão Semanal

**Como** usuário,  
**quero** registrar conquistas, dificuldades e ajustes da semana,  
**para** aprender com minha execução e melhorar a semana seguinte.

### Critérios de aceitação
- Cada planejamento pode ter no máximo uma revisão.
- O usuário pode editar e excluir a revisão.
- O histórico deve permanecer consultável enquanto os registros existirem.

---

## HU-012 — Dashboard

**Como** usuário,  
**quero** visualizar um resumo dos meus principais indicadores,  
**para** entender rapidamente meu estado atual de execução.

### Critérios de aceitação
- Exibir informações da Prioridade #1.
- Exibir dados do ciclo e planejamento atual.
- Exibir score e tarefas estratégicas.
- Exibir minutos de foco e objetivos ativos.
