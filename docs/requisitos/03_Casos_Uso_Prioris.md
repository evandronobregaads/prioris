# Casos de Uso — PRIORIS

## Ator principal

**Usuário**

## UC-01 — Cadastrar-se
**Ator:** Usuário  
**Pré-condição:** Não possuir conta com o e-mail informado.  
**Fluxo principal:** Usuário informa nome, e-mail e senha; sistema valida; conta é criada.  
**Pós-condição:** Usuário registrado no banco.

## UC-02 — Realizar Login
**Ator:** Usuário  
**Pré-condição:** Possuir cadastro ativo.  
**Fluxo principal:** Usuário informa e-mail e senha; sistema valida as credenciais; acesso é permitido.  
**Pós-condição:** Dados básicos do usuário autenticado são retornados.

## UC-03 — Gerenciar Objetivos e Metas
**Ator:** Usuário  
**Pré-condição:** Usuário identificado no sistema.  
**Fluxo principal:** Usuário cria objetivo; opcionalmente cria metas vinculadas; consulta e atualiza os registros.  
**Pós-condição:** Visão estratégica do usuário fica registrada.

## UC-04 — Gerenciar Tarefas
**Ator:** Usuário  
**Pré-condição:** Usuário identificado.  
**Fluxo principal:** Usuário cria tarefa, define classificação ABCDE e vínculo estratégico opcional; sistema valida e salva.  
**Fluxo alternativo:** Se Meta e Objetivo forem enviados juntos, a operação é rejeitada.  
**Pós-condição:** Tarefa disponível para priorização, planejamento e foco.

## UC-05 — Definir Prioridade #1
**Ator:** Usuário  
**Pré-condição:** Existir tarefa pertencente ao usuário.  
**Fluxo principal:** Usuário seleciona uma tarefa; sistema registra como prioridade do dia.  
**Fluxo alternativo:** Se já existir prioridade na data, o sistema rejeita nova definição até que seja alterada/removida.  
**Pós-condição:** Uma única tarefa fica definida como Prioridade #1.

## UC-06 — Gerenciar Ciclo de 12 Semanas
**Ator:** Usuário  
**Pré-condição:** Usuário identificado.  
**Fluxo principal:** Usuário cria ciclo e associa objetivos.  
**Pós-condição:** Objetivos estratégicos ficam organizados no ciclo.

## UC-07 — Criar Planejamento Semanal
**Ator:** Usuário  
**Pré-condição:** Usuário identificado e tarefas cadastradas.  
**Fluxo principal:** Usuário cria planejamento e adiciona tarefas estratégicas.  
**Pós-condição:** Semana possui conjunto de tarefas planejadas.

## UC-08 — Executar Sessão de Foco
**Ator:** Usuário  
**Pré-condição:** Usuário identificado.  
**Fluxo principal:** Usuário inicia sessão, executa foco e finaliza.  
**Fluxos alternativos:** Pausar, retomar ou interromper.  
**Pós-condição:** Tempo realizado fica registrado.

## UC-09 — Fazer Revisão Semanal
**Ator:** Usuário  
**Pré-condição:** Existir planejamento semanal.  
**Fluxo principal:** Usuário registra conquistas, dificuldades, ajustes e observações.  
**Pós-condição:** Revisão e Score de Execução ficam disponíveis no histórico.

## UC-10 — Consultar Dashboard
**Ator:** Usuário  
**Pré-condição:** Usuário identificado.  
**Fluxo principal:** Sistema consulta dados relevantes e apresenta resumo da execução.  
**Pós-condição:** Usuário visualiza indicadores para tomada de decisão.
