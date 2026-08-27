USE prioris;

-- =====================================================
-- PRIORIS
-- Views do banco de dados
-- =====================================================

CREATE OR REPLACE VIEW vw_dashboard_tarefas AS

SELECT
    u.id_usuario,
    u.nome AS usuario,

    t.id_tarefa,
    t.titulo AS tarefa,
    t.descricao,
    t.classificacao_abcde,
    t.status AS status_tarefa,
    t.data_planejada,
    t.prazo,
    t.tempo_estimado,

    m.titulo AS meta,

    COALESCE(
            objetivo_meta.titulo,
            objetivo_direto.titulo
    ) AS objetivo,

    CASE
        WHEN pd.id_prioridade_diaria IS NOT NULL
            THEN TRUE
        ELSE FALSE
        END AS prioridade_hoje,

    COALESCE(
            sf.minutos_foco_realizados,
            0
    ) AS minutos_foco_realizados

FROM usuarios u

         INNER JOIN tarefas t
                    ON t.id_usuario = u.id_usuario

         LEFT JOIN metas m
                   ON m.id_meta = t.id_meta

         LEFT JOIN objetivos objetivo_meta
                   ON objetivo_meta.id_objetivo = m.id_objetivo

         LEFT JOIN objetivos objetivo_direto
                   ON objetivo_direto.id_objetivo = t.id_objetivo

         LEFT JOIN prioridades_diarias pd
                   ON pd.id_usuario = u.id_usuario
                       AND pd.id_tarefa = t.id_tarefa
                       AND pd.data_prioridade = CURDATE()

         LEFT JOIN (
    SELECT
        id_usuario,
        id_tarefa,
        SUM(tempo_foco_realizado) AS minutos_foco_realizados
    FROM sessoes_foco
    WHERE id_tarefa IS NOT NULL
    GROUP BY
        id_usuario,
        id_tarefa
) sf
                   ON sf.id_usuario = u.id_usuario
                       AND sf.id_tarefa = t.id_tarefa;

