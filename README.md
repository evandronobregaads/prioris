# PRIORIS

> **Priorize. Foque. Avance.**

O **Prioris** é uma aplicação web de produtividade desenvolvida como Projeto Final do curso de **Programação de Sistemas do SENAC**.

O sistema foi criado para ajudar o usuário a transformar objetivos de longo prazo em ações executáveis, conectando planejamento estratégico, priorização diária, execução focada e acompanhamento da evolução.

A proposta do Prioris reúne conceitos como objetivos, metas, tarefas, classificação ABCDE, Prioridade #1, ciclos de 12 semanas, planejamento semanal, sessões de foco e revisão semanal.

---

## 🎯 Objetivo

O Prioris busca ajudar o usuário a responder quatro perguntas:

1. Onde quero chegar?
2. O que preciso priorizar?
3. O que devo executar agora?
4. Estou realmente avançando?

O fluxo conceitual da aplicação é:

**VISÃO → PRIORIDADE → EXECUÇÃO → EVOLUÇÃO**

---

## 🚀 Funcionalidades

### Usuários

- Cadastro de usuários
- Consulta de usuários
- Atualização de dados
- Desativação lógica
- Proteção de senhas com BCrypt

### Autenticação

- Login por e-mail e senha
- Validação de credenciais
- Identificação do usuário no front-end

> A versão atual do Prioris não utiliza JWT.

### Objetivos

- Cadastro de objetivos
- Definição de área da vida
- Descrição
- Motivo
- Prazo
- Status
- Acompanhamento da evolução

### Metas

- Cadastro de metas vinculadas a objetivos
- Definição de prazo
- Controle de status
- Atualização e cancelamento

### Tarefas

- Cadastro de tarefas
- Classificação ABCDE
- Data planejada
- Prazo
- Tempo estimado
- Controle de status
- Associação com Meta ou Objetivo
- Possibilidade de tarefa sem vínculo estratégico

Uma tarefa pode estar vinculada a uma **Meta OU a um Objetivo**, nunca aos dois simultaneamente.

### Prioridade #1

- Definição da tarefa mais importante do dia
- Uma prioridade por usuário por dia
- Alteração da prioridade atual
- Exclusão da prioridade do dia
- Histórico de prioridades

### Ciclos de 12 Semanas

- Criação de ciclos
- Definição do período
- Controle de status
- Associação de objetivos ao ciclo
- Remoção de objetivos do ciclo

### Planejamento Semanal

- Criação do planejamento de cada semana
- Associação de tarefas estratégicas
- Controle de tarefas planejadas
- Controle de tarefas concluídas
- Cálculo do Score de Execução

### Sessões de Foco

- Timer estilo Pomodoro
- Sessões com ou sem tarefa associada
- Definição do tempo de foco
- Definição do tempo de descanso
- Pausar sessão
- Retomar sessão
- Finalizar sessão
- Interromper sessão
- Registro do tempo de foco realizado
- Histórico de sessões
- Sons ambientes utilizando YouTube

### Revisão Semanal

- Registro das principais conquistas
- Registro das dificuldades
- Definição de ajustes para a próxima semana
- Observações
- Registro do Score de Execução
- Histórico de revisões

### Dashboard

O Dashboard apresenta uma visão resumida da execução do usuário, incluindo informações sobre:

- Prioridade #1
- Objetivos ativos
- Tarefas estratégicas
- Planejamento semanal
- Score de Execução
- Sessões de foco
- Minutos de foco

---

## 📊 Score de Execução

O Prioris utiliza um indicador semanal para acompanhar a execução das tarefas estratégicas planejadas.

A fórmula utilizada é:

```text
Score de Execução =
(Tarefas Concluídas / Tarefas Planejadas) × 100
```

O resultado é apresentado em percentual e também pode ser registrado durante a revisão semanal.

---

## 🛠 Tecnologias Utilizadas

### Back-end

- Java 26
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation
- BCrypt
- Maven
- JUnit
- Mockito
- Springdoc OpenAPI
- Swagger UI

### Banco de Dados

- MySQL 8
- SQL
- Chaves primárias
- Chaves estrangeiras
- Constraints
- Relacionamentos
- Views
- Procedures ou Triggers
- Consultas com JOIN

### Front-end

- HTML5
- CSS3
- JavaScript
- Fetch API
- Local Storage
- YouTube IFrame Player API

### Ferramentas

- IntelliJ IDEA
- MySQL Workbench
- Postman
- Swagger UI
- Git
- GitHub

---

## 🏗 Arquitetura do Back-end

O back-end foi organizado em camadas.

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Entity
    ↓
MySQL
```

Também são utilizados DTOs para controlar os dados recebidos e retornados pela API.

O tratamento de exceções é centralizado através de um `GlobalExceptionHandler`.

---

## 📂 Estrutura Geral do Projeto

```text
prioris/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── frontend/
│
├── database/
│
├── docs/
│   ├── postman/
│   ├── swagger/
│   └── evidencias/
│
├── .gitignore
└── README.md
```

---

## ⚙️ Pré-requisitos

Para executar o projeto localmente é necessário possuir:

- Java 26
- MySQL 8
- Git
- Navegador web
- IntelliJ IDEA ou outra IDE compatível com Maven

Não é necessário instalar o Maven globalmente, pois o projeto possui o **Maven Wrapper**.

---

## 📥 Instalação

### 1. Clonar o repositório

```bash
git clone URL_DO_REPOSITORIO
```

Depois:

```bash
cd prioris
```

> Substitua `URL_DO_REPOSITORIO` pela URL pública do projeto no GitHub.

---

## 🗄 Configuração do Banco de Dados

Crie o banco de dados MySQL utilizado pelo projeto:

```sql
CREATE DATABASE prioris;
```

Depois execute os scripts SQL existentes na pasta:

```text
database/
```

O projeto utiliza o banco:

```text
prioris
```

Configure também os dados de acesso ao MySQL no back-end de acordo com seu ambiente local.

Exemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/prioris
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

> Senhas e credenciais pessoais não devem ser versionadas no GitHub.

---

## ▶️ Executando o Back-end

Entre na pasta do back-end:

```bash
cd backend
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Em Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Quando iniciado corretamente, o servidor estará disponível em:

```text
http://localhost:8080
```

A API utiliza como endereço-base:

```text
http://localhost:8080/api
```

---

## 🌐 Executando o Front-end

O front-end foi desenvolvido utilizando HTML, CSS e JavaScript puro.

Ele pode ser executado através de um servidor local da IDE ou de outra ferramenta de desenvolvimento web.

O front-end realiza requisições HTTP para a API utilizando `fetch`.

A URL-base utilizada pela aplicação é:

```text
http://localhost:8080/api
```

---

## 📚 Swagger / OpenAPI

Com o back-end em execução, a documentação interativa da API pode ser acessada em:

```text
http://localhost:8080/swagger-ui/index.html
```

A especificação OpenAPI em JSON pode ser acessada em:

```text
http://localhost:8080/v3/api-docs
```

A documentação Swagger apresenta:

- Endpoints
- Métodos HTTP
- Descrições
- Path Parameters
- Request Bodies
- Responses
- Códigos HTTP
- Exemplos de requisição
- Exemplos de resposta
- Schemas dos DTOs

Os principais códigos HTTP documentados são:

```text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
404 Not Found
409 Conflict
500 Internal Server Error
```

---

## 📮 Testes da API com Postman

Os endpoints da API foram testados utilizando o **Postman**.

Foram contemplados cenários de:

- Sucesso
- Dados inválidos
- Campos obrigatórios ausentes
- IDs inexistentes
- Conflitos de regras de negócio

A Collection exportada do Postman está versionada no projeto dentro da pasta:

```text
docs/postman/
```

---

## 🧪 Testes Unitários

O back-end possui testes unitários utilizando **JUnit e Mockito**.

Atualmente são testados serviços relacionados a:

- Tarefas
- Prioridade Diária
- Planejamento Semanal

Para executar todos os testes no Windows:

```powershell
cd backend
.\mvnw.cmd test
```

Em Linux ou macOS:

```bash
cd backend
./mvnw test
```

Resultado validado antes da entrega:

```text
Tests run: 3
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

---

## 🗃 Banco de Dados e Modelagem

O Prioris utiliza banco de dados relacional MySQL.

Entre as entidades existentes no sistema estão:

- usuarios
- objetivos
- metas
- tarefas
- ciclos
- ciclos_objetivos
- planejamentos_semanais
- planejamentos_tarefas
- prioridades_diarias
- revisoes_semanais
- sessoes_foco

O banco utiliza:

- Primary Keys
- Foreign Keys
- NOT NULL
- UNIQUE
- DEFAULT
- Integridade referencial
- Relacionamentos 1:N e N:M

A documentação do banco de dados, diagramas e scripts SQL estão organizados na pasta:

```text
database/
```

---

## 🔄 Fluxo Principal do Prioris

```text
OBJETIVOS
   ↓
CICLO DE 12 SEMANAS
   ↓
METAS
   ↓
TAREFAS
   ↓
PLANEJAMENTO SEMANAL
   ↓
PRIORIDADE #1
   ↓
SESSÃO DE FOCO
   ↓
REVISÃO SEMANAL
   ↓
EVOLUÇÃO
```

---

## 🔒 Segurança

As senhas dos usuários não são armazenadas em texto puro.

O back-end utiliza **BCrypt** para geração e validação dos hashes das senhas.

A versão atual utiliza autenticação por e-mail e senha.

Não foi implementada autenticação JWT na V1.

---

## 📌 Status do Projeto

### Versão 1.0 — Projeto Final SENAC

Funcionalidades concluídas:

- [x] Usuários
- [x] Login
- [x] Objetivos
- [x] Metas
- [x] Tarefas
- [x] Prioridade #1
- [x] Ciclos de 12 Semanas
- [x] Planejamento Semanal
- [x] Sessões de Foco
- [x] Revisão Semanal
- [x] Dashboard
- [x] API REST
- [x] Banco de Dados MySQL
- [x] Front-end integrado à API
- [x] Postman
- [x] Swagger / OpenAPI
- [x] Testes unitários

---

## 👨‍💻 Autor

**Evandro de Medeiros Nóbrega Júnior**

Projeto desenvolvido como trabalho final do curso de **Programação de Sistemas — SENAC**.

---

## 📄 Finalidade

Projeto desenvolvido para fins acadêmicos, educacionais e de portfólio profissional.
