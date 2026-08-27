USE prioris;

-- =====================================================
-- PRIORIS
-- Triggers e rotinas do banco de dados
-- =====================================================

DROP TRIGGER IF EXISTS trg_tarefas_data_conclusao;

DELIMITER //

CREATE TRIGGER trg_tarefas_data_conclusao
    BEFORE UPDATE ON tarefas
    FOR EACH ROW
BEGIN

    IF NEW.status = 'CONCLUIDA'
       AND OLD.status <> 'CONCLUIDA' THEN

        SET NEW.data_conclusao = CURRENT_TIMESTAMP;

    ELSEIF NEW.status <> 'CONCLUIDA'
       AND OLD.status = 'CONCLUIDA' THEN

        SET NEW.data_conclusao = NULL;

END IF;

END//

DELIMITER ;