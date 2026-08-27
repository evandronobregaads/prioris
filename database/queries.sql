USE prioris;

-- =====================================================
-- PRIORIS
-- Consultas SQL com JOIN
-- =====================================================

-- =====================================================
-- QUERY 01
-- Planejamento semanal completo
-- =====================================================

SELECT
    ps.id_planejamento_semanal,
    ps.semana_ciclo,
    ps.data_inicio_semana,
    ps.data_fim_semana,

    t.id_tarefa,
    t.titulo AS tarefa,
    t.classificacao_abcde,
    t.status AS status_tarefa,
    t.tempo_estimado,

    m.titulo AS meta,

    COALESCE(
            objetivo_meta.titulo,
            objetivo_direto.titulo
    ) AS objetivo

FROM planejamentos_semanais ps

         INNER JOIN planejamentos_tarefas pt
                    ON pt.id_planejamento_semanal = ps.id_planejamento_semanal

         INNER JOIN tarefas t
                    ON t.id_tarefa = pt.id_tarefa

         LEFT JOIN metas m
                   ON m.id_meta = t.id_meta

         LEFT JOIN objetivos objetivo_meta
                   ON objetivo_meta.id_objetivo = m.id_objetivo

         LEFT JOIN objetivos objetivo_direto
                   ON objetivo_direto.id_objetivo = t.id_objetivo

ORDER BY
    ps.semana_ciclo,
    t.classificacao_abcde,
    t.titulo;

-- =====================================================
-- QUERY 02
-- Prioridade principal do dia e tempo de foco
-- =====================================================

SELECT
    u.id_usuario,
    u.nome,

    pd.data_prioridade,

    t.id_tarefa,
    t.titulo AS prioridade_do_dia,
    t.classificacao_abcde,
    t.status AS status_tarefa,

    COUNT(sf.id_sessao_foco) AS quantidade_sessoes,

    COALESCE(
            SUM(sf.tempo_foco_realizado),
            0
    ) AS minutos_foco_realizados

FROM usuarios u

         INNER JOIN prioridades_diarias pd
                    ON pd.id_usuario = u.id_usuario

         INNER JOIN tarefas t
                    ON t.id_tarefa = pd.id_tarefa

         LEFT JOIN sessoes_foco sf
                   ON sf.id_usuario = u.id_usuario
                       AND sf.id_tarefa = t.id_tarefa
                       AND DATE(sf.data_inicio) = pd.data_prioridade

WHERE pd.data_prioridade = CURDATE()

GROUP BY
    u.id_usuario,
    u.nome,
    pd.data_prioridade,
    t.id_tarefa,
    t.titulo,
    t.classificacao_abcde,
    t.status;

-- =====================================================
-- QUERY 03
-- Evolução dos objetivos dentro do ciclo
-- =====================================================

SELECT
    c.id_ciclo,
    c.titulo AS ciclo,

    o.id_objetivo,
    o.titulo AS objetivo,

    COUNT(DISTINCT m.id_meta) AS quantidade_metas,

    COUNT(DISTINCT t.id_tarefa) AS quantidade_tarefas,

    COUNT(
            DISTINCT CASE
                         WHEN t.status = 'CONCLUIDA'
                             THEN t.id_tarefa
        END
    ) AS tarefas_concluidas,

    ROUND(
            (
                COUNT(
                        DISTINCT CASE
                                     WHEN t.status = 'CONCLUIDA'
                                         THEN t.id_tarefa
                    END
                )
                    /
                NULLIF(
                        COUNT(DISTINCT t.id_tarefa),
                        0
                )
                ) * 100,
            2
    ) AS percentual_conclusao

FROM ciclos c

         INNER JOIN ciclos_objetivos co
                    ON co.id_ciclo = c.id_ciclo

         INNER JOIN objetivos o
                    ON o.id_objetivo = co.id_objetivo

         LEFT JOIN metas m
                   ON m.id_objetivo = o.id_objetivo

         LEFT JOIN tarefas t
                   ON (
                       t.id_meta = m.id_meta
                           OR (
                           t.id_meta IS NULL
                               AND t.id_objetivo = o.id_objetivo
                           )
                       )

GROUP BY
    c.id_ciclo,
    c.titulo,
    o.id_objetivo,
    o.titulo

ORDER BY
    c.id_ciclo,
    o.id_objetivo;