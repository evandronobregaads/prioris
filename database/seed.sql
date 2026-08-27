USE prioris;

-- =====================================================
-- PRIORIS
-- Dados iniciais para desenvolvimento e demonstração
-- =====================================================

-- ATENÇÃO:
-- O bloco abaixo remove apenas os DADOS existentes.
-- A estrutura das tabelas permanece intacta.

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE revisoes_semanais;
TRUNCATE TABLE planejamentos_tarefas;
TRUNCATE TABLE planejamentos_semanais;
TRUNCATE TABLE prioridades_diarias;
TRUNCATE TABLE sessoes_foco;
TRUNCATE TABLE tarefas;
TRUNCATE TABLE metas;
TRUNCATE TABLE ciclos_objetivos;
TRUNCATE TABLE objetivos;
TRUNCATE TABLE ciclos;
TRUNCATE TABLE usuarios;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- USUÁRIO
-- =====================================================

INSERT INTO usuarios (
    id_usuario,
    nome,
    email,
    senha_hash
)
VALUES (
           1,
           'Usuario Demo',
           'demo@prioris.app',
           'hash_demo_nao_real'
       );

-- =====================================================
-- OBJETIVOS
-- =====================================================

INSERT INTO objetivos (
    id_objetivo,
    id_usuario,
    titulo,
    descricao,
    area,
    motivo,
    prazo,
    status
)
VALUES
    (
        1,
        1,
        'Concluir o Projeto Prioris',
        'Finalizar o Projeto Integrador com banco, back-end, front-end, testes e documentacao.',
        'CARREIRA',
        'Aplicar os conhecimentos de programacao em um projeto completo.',
        '2026-09-11',
        'ATIVO'
    ),
    (
        2,
        1,
        'Aprimorar conhecimentos em Java',
        'Desenvolver conhecimentos de Java, POO, APIs e Spring Boot.',
        'DESENVOLVIMENTO',
        'Evoluir as habilidades necessarias para desenvolvimento back-end.',
        '2026-12-15',
        'ATIVO'
    );

-- =====================================================
-- CICLO DE 12 SEMANAS
-- =====================================================

INSERT INTO ciclos (
    id_ciclo,
    id_usuario,
    titulo,
    data_inicio,
    data_fim,
    status
)
VALUES (
           1,
           1,
           'Ciclo 1 - Desenvolvimento e Projeto Prioris',
           '2026-08-24',
           '2026-11-15',
           'EM_ANDAMENTO'
       );

-- =====================================================
-- OBJETIVOS DO CICLO
-- =====================================================

INSERT INTO ciclos_objetivos (
    id_ciclo_objetivo,
    id_ciclo,
    id_objetivo
)
VALUES
    (1, 1, 1),
    (2, 1, 2);

-- =====================================================
-- METAS
-- =====================================================

INSERT INTO metas (
    id_meta,
    id_objetivo,
    titulo,
    descricao,
    prazo,
    status
)
VALUES
    (
        1,
        1,
        'Concluir banco de dados',
        'Finalizar modelagem, estrutura SQL e testes do banco do Prioris.',
        '2026-08-28',
        'EM_ANDAMENTO'
    ),
    (
        2,
        1,
        'Implementar API REST',
        'Desenvolver o back-end do Prioris utilizando Java e Spring Boot.',
        '2026-09-04',
        'PENDENTE'
    ),
    (
        3,
        2,
        'Praticar Java e Spring Boot',
        'Aplicar conceitos de POO, API REST e persistencia de dados.',
        '2026-09-10',
        'PENDENTE'
    );

-- =====================================================
-- TAREFAS
-- =====================================================

INSERT INTO tarefas (
    id_tarefa,
    id_usuario,
    id_meta,
    id_objetivo,
    titulo,
    descricao,
    classificacao_abcde,
    data_planejada,
    prazo,
    tempo_estimado,
    status,
    data_conclusao
)
VALUES
    (
        1,
        1,
        1,
        NULL,
        'Finalizar estrutura do banco de dados',
        'Concluir todas as tabelas e constraints do Prioris.',
        'A',
        '2026-08-27',
        '2026-08-27 11:30:00',
        90,
        'CONCLUIDA',
        '2026-08-27 10:30:00'
    ),
    (
        2,
        1,
        2,
        NULL,
        'Criar projeto Spring Boot',
        'Preparar a estrutura inicial do back-end.',
        'A',
        '2026-08-28',
        '2026-08-28 11:30:00',
        120,
        'PENDENTE',
        NULL
    ),
    (
        3,
        1,
        3,
        NULL,
        'Revisar conceitos de POO',
        'Revisar encapsulamento, heranca, polimorfismo e abstracao.',
        'B',
        '2026-08-29',
        NULL,
        60,
        'PENDENTE',
        NULL
    ),
    (
        4,
        1,
        NULL,
        1,
        'Revisar documentacao do Projeto Integrador',
        'Conferir requisitos, modelagem e documentacao do Prioris.',
        'B',
        '2026-08-30',
        NULL,
        45,
        'PENDENTE',
        NULL
    );

-- =====================================================
-- PRIORIDADE PRINCIPAL DO DIA
-- =====================================================

INSERT INTO prioridades_diarias (
    id_prioridade_diaria,
    id_usuario,
    id_tarefa,
    data_prioridade
)
VALUES (
           1,
           1,
           1,
           '2026-08-27'
       );

-- =====================================================
-- SESSÕES DE FOCO
-- =====================================================

INSERT INTO sessoes_foco (
    id_sessao_foco,
    id_usuario,
    id_tarefa,
    data_inicio,
    data_fim,
    tempo_foco_planejado,
    tempo_descanso_planejado,
    tempo_foco_realizado,
    status
)
VALUES
    (
        1,
        1,
        1,
        '2026-08-27 08:30:00',
        '2026-08-27 09:20:00',
        50,
        10,
        50,
        'CONCLUIDA'
    ),
    (
        2,
        1,
        1,
        '2026-08-27 09:30:00',
        '2026-08-27 10:10:00',
        50,
        10,
        40,
        'INTERROMPIDA'
    );

-- =====================================================
-- PLANEJAMENTO SEMANAL
-- =====================================================

INSERT INTO planejamentos_semanais (
    id_planejamento_semanal,
    id_usuario,
    id_ciclo,
    semana_ciclo,
    data_inicio_semana,
    data_fim_semana
)
VALUES (
           1,
           1,
           1,
           1,
           '2026-08-24',
           '2026-08-30'
       );

-- =====================================================
-- TAREFAS DO PLANEJAMENTO
-- =====================================================

INSERT INTO planejamentos_tarefas (
    id_planejamento_tarefa,
    id_planejamento_semanal,
    id_tarefa
)
VALUES
    (1, 1, 1),
    (2, 1, 2),
    (3, 1, 3),
    (4, 1, 4);

-- =====================================================
-- REVISÃO SEMANAL
-- =====================================================

INSERT INTO revisoes_semanais (
    id_revisao_semanal,
    id_planejamento_semanal,
    score_execucao,
    principais_conquistas,
    dificuldades,
    ajustes_proxima_semana,
    observacoes
)
VALUES (
           1,
           1,
           25.00,
           'Estrutura inicial do banco de dados concluida.',
           'Configuracao inicial do ambiente e ajustes de integridade.',
           'Avancar para o desenvolvimento da API REST.',
           'Primeira semana de desenvolvimento do Prioris.'
       );

