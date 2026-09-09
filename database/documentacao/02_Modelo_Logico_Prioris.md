# Modelo Lógico — Banco de Dados PRIORIS

> Modelo lógico derivado diretamente do `schema.sql` da V1.

## Resumo

- SGBD: MySQL
- Banco: `prioris`
- Tabelas: 11
- Chaves primárias: 11
- Chaves estrangeiras: 17
- Constraints `UNIQUE`: 5
- Constraints `CHECK`: 9
- Campos `NOT NULL`: 43
- Campos com `DEFAULT`: 14

---

## 1. usuarios

```text
usuarios (
    id_usuario BIGINT PK AUTO_INCREMENT,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
)
```

### Relacionamentos
É entidade-pai de `objetivos`, `ciclos`, `tarefas`, `prioridades_diarias`, `sessoes_foco` e `planejamentos_semanais`.

---

## 2. objetivos

```text
objetivos (
    id_objetivo BIGINT PK AUTO_INCREMENT,
    id_usuario BIGINT FK NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT NULL,
    area VARCHAR(50) NOT NULL,
    motivo TEXT NULL,
    prazo DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
)
```

FK: `id_usuario → usuarios.id_usuario`

---

## 3. ciclos

```text
ciclos (
    id_ciclo BIGINT PK AUTO_INCREMENT,
    id_usuario BIGINT FK NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADO',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
)
```

FK: `id_usuario → usuarios.id_usuario`

---

## 4. ciclos_objetivos

Tabela associativa do relacionamento N:M entre ciclos e objetivos.

```text
ciclos_objetivos (
    id_ciclo_objetivo BIGINT PK AUTO_INCREMENT,
    id_ciclo BIGINT FK NOT NULL,
    id_objetivo BIGINT FK NOT NULL,
    UNIQUE (id_ciclo, id_objetivo)
)
```

FKs:
- `id_ciclo → ciclos.id_ciclo`
- `id_objetivo → objetivos.id_objetivo`

---

## 5. metas

```text
metas (
    id_meta BIGINT PK AUTO_INCREMENT,
    id_objetivo BIGINT FK NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT NULL,
    prazo DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
)
```

FK: `id_objetivo → objetivos.id_objetivo`

---

## 6. tarefas

```text
tarefas (
    id_tarefa BIGINT PK AUTO_INCREMENT,
    id_usuario BIGINT FK NOT NULL,
    id_meta BIGINT FK NULL,
    id_objetivo BIGINT FK NULL,
    titulo VARCHAR(180) NOT NULL,
    descricao TEXT NULL,
    classificacao_abcde CHAR(1) NULL,
    data_planejada DATE NULL,
    prazo DATETIME NULL,
    tempo_estimado INT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_conclusao DATETIME NULL
)
```

FKs:
- `id_usuario → usuarios.id_usuario`
- `id_meta → metas.id_meta`
- `id_objetivo → objetivos.id_objetivo`

CHECKs:
- `classificacao_abcde` deve ser A, B, C, D ou E quando informada;
- `tempo_estimado` deve ser maior que zero quando informado;
- `id_meta` e `id_objetivo` não podem ser preenchidos simultaneamente.

---

## 7. prioridades_diarias

```text
prioridades_diarias (
    id_prioridade_diaria BIGINT PK AUTO_INCREMENT,
    id_usuario BIGINT FK NOT NULL,
    id_tarefa BIGINT FK NOT NULL,
    data_prioridade DATE NOT NULL,
    UNIQUE (id_usuario, data_prioridade)
)
```

FKs:
- `id_usuario → usuarios.id_usuario`
- `id_tarefa → tarefas.id_tarefa`

A constraint `UNIQUE (id_usuario, data_prioridade)` garante uma única Prioridade #1 por usuário em cada data.

---

## 8. sessoes_foco

```text
sessoes_foco (
    id_sessao_foco BIGINT PK AUTO_INCREMENT,
    id_usuario BIGINT FK NOT NULL,
    id_tarefa BIGINT FK NULL,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME NULL,
    tempo_foco_planejado INT NOT NULL,
    tempo_descanso_planejado INT NOT NULL,
    tempo_foco_realizado INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO'
)
```

FKs:
- `id_usuario → usuarios.id_usuario`
- `id_tarefa → tarefas.id_tarefa`

CHECKs:
- `tempo_foco_planejado > 0`
- `tempo_descanso_planejado > 0`
- `tempo_foco_realizado >= 0`

---

## 9. planejamentos_semanais

```text
planejamentos_semanais (
    id_planejamento_semanal BIGINT PK AUTO_INCREMENT,
    id_usuario BIGINT FK NOT NULL,
    id_ciclo BIGINT FK NULL,
    semana_ciclo TINYINT NULL,
    data_inicio_semana DATE NOT NULL,
    data_fim_semana DATE NOT NULL,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
)
```

FKs:
- `id_usuario → usuarios.id_usuario`
- `id_ciclo → ciclos.id_ciclo`

CHECKs:
- `semana_ciclo` deve estar entre 1 e 12 quando informada;
- `data_fim_semana >= data_inicio_semana`.

---

## 10. planejamentos_tarefas

Tabela associativa do relacionamento N:M entre planejamentos semanais e tarefas.

```text
planejamentos_tarefas (
    id_planejamento_tarefa BIGINT PK AUTO_INCREMENT,
    id_planejamento_semanal BIGINT FK NOT NULL,
    id_tarefa BIGINT FK NOT NULL,
    UNIQUE (id_planejamento_semanal, id_tarefa)
)
```

FKs:
- `id_planejamento_semanal → planejamentos_semanais.id_planejamento_semanal`
- `id_tarefa → tarefas.id_tarefa`

---

## 11. revisoes_semanais

```text
revisoes_semanais (
    id_revisao_semanal BIGINT PK AUTO_INCREMENT,
    id_planejamento_semanal BIGINT FK NOT NULL UNIQUE,
    score_execucao DECIMAL(5,2) NULL,
    principais_conquistas TEXT NULL,
    dificuldades TEXT NULL,
    ajustes_proxima_semana TEXT NULL,
    observacoes TEXT NULL,
    data_revisao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
)
```

FK:
- `id_planejamento_semanal → planejamentos_semanais.id_planejamento_semanal`

CHECK:
- `score_execucao` deve estar entre 0 e 100 quando informado.

A constraint `UNIQUE (id_planejamento_semanal)` estabelece uma relação 1:0..1 entre planejamento e revisão.

---

## Relacionamentos N:M

O banco possui dois relacionamentos muitos-para-muitos resolvidos por tabelas associativas:

### Ciclos × Objetivos

```text
ciclos
   1
   |
   N
ciclos_objetivos
   N
   |
   1
objetivos
```

### Planejamentos × Tarefas

```text
planejamentos_semanais
   1
   |
   N
planejamentos_tarefas
   N
   |
   1
tarefas
```
