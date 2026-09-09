# Documento de Requisitos — PRIORIS

> Projeto Final — Programação de Sistemas — SENAC  
> Versão 1.0

## 1. Tema

**PRIORIS — Priorize. Foque. Avance.**

Aplicação web de produtividade voltada à transformação de objetivos de longo prazo em ações executáveis, organizando prioridades, metas, tarefas, ciclos de 12 semanas, planejamento semanal, sessões de foco e revisão de desempenho.

## 2. Escopo

A V1 do Prioris permite que um usuário:

- cadastre-se e realize login;
- registre objetivos pessoais e profissionais;
- associe metas aos objetivos;
- organize objetivos em ciclos de 12 semanas;
- crie tarefas estratégicas;
- vincule cada tarefa a uma Meta, a um Objetivo ou mantenha-a sem vínculo;
- classifique tarefas pelo método ABCDE;
- defina uma Prioridade #1 diária;
- monte planejamentos semanais;
- acompanhe o Score de Execução;
- realize sessões de foco;
- faça revisão semanal;
- acompanhe indicadores no Dashboard.

### Fora do escopo da V1

- autenticação JWT;
- recuperação de senha por e-mail;
- integração com Google Calendar;
- integração com Gemini;
- notificações push;
- colaboração entre múltiplos usuários;
- aplicativos Android/iOS nativos.

## 3. Atores

### Usuário

Pessoa que utiliza o Prioris para organizar objetivos, metas, tarefas, prioridades, planejamento e sessões de foco.

Na V1, o sistema possui um ator principal: **Usuário**.

---

# 4. Requisitos Funcionais

| ID | Tipo | Descrição | Prioridade |
|---|---|---|---|
| RF-001 | Funcional | O sistema deve permitir o cadastro de usuários com nome, e-mail e senha. | Alta |
| RF-002 | Funcional | O sistema deve permitir autenticação por e-mail e senha. | Alta |
| RF-003 | Funcional | O sistema deve permitir consultar, atualizar e desativar o cadastro do usuário. | Média |
| RF-004 | Funcional | O sistema deve permitir cadastrar, consultar, editar, concluir/cancelar objetivos. | Alta |
| RF-005 | Funcional | O sistema deve permitir classificar objetivos por área da vida. | Média |
| RF-006 | Funcional | O sistema deve permitir cadastrar metas vinculadas a objetivos. | Alta |
| RF-007 | Funcional | O sistema deve permitir atualizar e cancelar metas. | Média |
| RF-008 | Funcional | O sistema deve permitir cadastrar tarefas. | Alta |
| RF-009 | Funcional | O sistema deve permitir classificar tarefas pelo método ABCDE. | Alta |
| RF-010 | Funcional | O sistema deve permitir vincular uma tarefa a uma Meta OU diretamente a um Objetivo. | Alta |
| RF-011 | Funcional | O sistema deve permitir criar tarefas sem vínculo estratégico. | Média |
| RF-012 | Funcional | O sistema deve impedir que uma tarefa esteja vinculada simultaneamente a Meta e Objetivo. | Alta |
| RF-013 | Funcional | O sistema deve permitir editar, concluir e excluir tarefas. | Alta |
| RF-014 | Funcional | O sistema deve permitir definir uma única Prioridade #1 por usuário em cada dia. | Alta |
| RF-015 | Funcional | O sistema deve permitir alterar e remover a Prioridade #1 do dia. | Média |
| RF-016 | Funcional | O sistema deve disponibilizar histórico das prioridades diárias. | Média |
| RF-017 | Funcional | O sistema deve permitir criar ciclos de 12 semanas. | Alta |
| RF-018 | Funcional | O sistema deve permitir associar objetivos aos ciclos de 12 semanas. | Alta |
| RF-019 | Funcional | O sistema deve permitir remover a associação de um objetivo com um ciclo sem excluir o objetivo. | Média |
| RF-020 | Funcional | O sistema deve permitir criar planejamentos semanais. | Alta |
| RF-021 | Funcional | O sistema deve permitir associar tarefas estratégicas ao planejamento semanal. | Alta |
| RF-022 | Funcional | O sistema deve calcular o Score de Execução com base nas tarefas planejadas e concluídas. | Alta |
| RF-023 | Funcional | O sistema deve permitir remover uma tarefa do planejamento sem excluir a tarefa do sistema. | Média |
| RF-024 | Funcional | O sistema deve permitir iniciar uma sessão de foco com ou sem tarefa associada. | Alta |
| RF-025 | Funcional | O sistema deve permitir pausar e retomar sessões de foco. | Alta |
| RF-026 | Funcional | O sistema deve permitir finalizar ou interromper sessões de foco registrando o tempo realizado. | Alta |
| RF-027 | Funcional | O sistema deve disponibilizar histórico das sessões de foco. | Média |
| RF-028 | Funcional | O sistema deve permitir criar uma revisão semanal para um planejamento. | Alta |
| RF-029 | Funcional | O sistema deve permitir registrar conquistas, dificuldades, ajustes e observações na revisão semanal. | Alta |
| RF-030 | Funcional | O sistema deve impedir mais de uma revisão para o mesmo planejamento semanal. | Alta |
| RF-031 | Funcional | O sistema deve permitir editar e excluir uma revisão semanal. | Média |
| RF-032 | Funcional | O sistema deve disponibilizar histórico das revisões semanais. | Média |
| RF-033 | Funcional | O Dashboard deve apresentar dados consolidados de execução do usuário. | Alta |
| RF-034 | Funcional | O sistema deve disponibilizar documentação interativa da API por Swagger UI. | Média |

---

# 5. Requisitos Não Funcionais

| ID | Tipo | Descrição | Prioridade |
|---|---|---|---|
| RNF-001 | Não Funcional | A aplicação deve possuir interface web responsiva para desktop e dispositivos móveis. | Alta |
| RNF-002 | Não Funcional | O front-end deve consumir a API REST por requisições HTTP utilizando Fetch API. | Alta |
| RNF-003 | Não Funcional | O back-end deve ser implementado em Java com Spring Boot. | Alta |
| RNF-004 | Não Funcional | O banco de dados deve utilizar MySQL relacional. | Alta |
| RNF-005 | Não Funcional | A API deve trocar dados em formato JSON. | Alta |
| RNF-006 | Não Funcional | A API deve utilizar códigos HTTP adequados para sucesso e erro. | Alta |
| RNF-007 | Não Funcional | O back-end deve utilizar arquitetura em camadas, separando Controller, Service e Repository. | Alta |
| RNF-008 | Não Funcional | Os dados de entrada da API devem possuir validação. | Alta |
| RNF-009 | Não Funcional | O tratamento de exceções deve ser centralizado. | Alta |
| RNF-010 | Não Funcional | As senhas não devem ser armazenadas em texto puro, utilizando BCrypt. | Alta |
| RNF-011 | Não Funcional | O banco deve implementar integridade referencial com chaves estrangeiras e constraints. | Alta |
| RNF-012 | Não Funcional | A API deve possuir documentação OpenAPI/Swagger acessível durante a execução do back-end. | Alta |
| RNF-013 | Não Funcional | O projeto deve possuir testes unitários básicos no back-end. | Média |
| RNF-014 | Não Funcional | O projeto deve manter versionamento em Git e repositório público no GitHub. | Alta |
| RNF-015 | Não Funcional | A interface deve manter consistência visual de cores, tipografia e espaçamentos. | Média |
| RNF-016 | Não Funcional | O sistema deve apresentar mensagens claras de sucesso, erro, carregamento e ausência de dados. | Média |
| RNF-017 | Não Funcional | Credenciais e dados sensíveis não devem ser versionados no repositório. | Alta |
| RNF-018 | Não Funcional | A aplicação deve manter compatibilidade com navegadores modernos. | Média |

---

# 6. Regras de Negócio Principais

| ID | Regra |
|---|---|
| RN-001 | Cada e-mail de usuário deve ser único. |
| RN-002 | A senha deve ser armazenada utilizando hash BCrypt. |
| RN-003 | Uma tarefa pode estar vinculada a uma Meta OU a um Objetivo, nunca aos dois simultaneamente. |
| RN-004 | Uma tarefa pode existir sem Meta e sem Objetivo. |
| RN-005 | Cada usuário pode possuir somente uma Prioridade #1 por data. |
| RN-006 | Uma sessão de foco pode estar ou não vinculada a uma tarefa. |
| RN-007 | Um planejamento semanal pode conter diversas tarefas, mas não deve repetir a mesma tarefa dentro do mesmo planejamento. |
| RN-008 | Cada planejamento semanal pode possuir no máximo uma revisão semanal. |
| RN-009 | O Score de Execução é calculado a partir da relação entre tarefas concluídas e tarefas planejadas. |
| RN-010 | A remoção de uma tarefa de um planejamento exclui apenas a associação, não a tarefa. |
| RN-011 | A remoção de um objetivo de um ciclo exclui apenas a associação, não o objetivo. |
