CREATE DATABASE IF NOT EXISTS prioris
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE prioris;

CREATE TABLE IF NOT EXISTS usuarios (
	id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(254) NOT NULL,
    senha_hash VARCHAR(255) NOT NULL,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT uq_usuarios_email
		UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS objetivos (
	id_objetivo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    area VARCHAR(50) NOT NULL,
    motivo TEXT,
    prazo DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_objetivos_usuario
		FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS ciclos (
	id_ciclo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PLANEJADO',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_ciclos_usuario
		FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS ciclos_objetivos (
	id_ciclo_objetivo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_ciclo BIGINT NOT NULL,
    id_objetivo BIGINT NOT NULL,
    
    CONSTRAINT uq_ciclos_objetivos
		UNIQUE (id_ciclo, id_objetivo),
	
    CONSTRAINT fk_ciclos_objetivos_ciclo
		FOREIGN KEY (id_ciclo)
        REFERENCES ciclos(id_ciclo),
	
    CONSTRAINT fk_ciclos_objetivos_objetivo
		FOREIGN KEY (id_objetivo)
        REFERENCES objetivos(id_objetivo)
);

CREATE TABLE IF NOT EXISTS metas (
	id_meta BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_objetivo BIGINT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    prazo DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_metas_objetivo
		FOREIGN KEY (id_objetivo)
        REFERENCES objetivos(id_objetivo)
);

CREATE TABLE IF NOT EXISTS tarefas (
	id_tarefa BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_meta BIGINT,
    id_objetivo BIGINT,
    titulo VARCHAR(180) NOT NULL,
    descricao TEXT,
    classificacao_abcde CHAR(1),
    data_planejada DATE,
    prazo DATETIME,
    tempo_estimado INT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_conclusao DATETIME,
    
    CONSTRAINT fk_tarefas_usuario
		FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario),
	
    CONSTRAINT fk_tarefas_meta
		FOREIGN KEY (id_meta)
        REFERENCES metas(id_meta),
        
	CONSTRAINT fk_tarefas_objetivo
		FOREIGN KEY (id_objetivo)
        REFERENCES objetivos(id_objetivo),
	
    CONSTRAINT ck_tarefas_classificacao_abcde
		CHECK (
			classificacao_abcde IS NULL
            OR classificacao_abcde IN ('A', 'B', 'C', 'D', 'E')
        ),
	
    CONSTRAINT ck_tarefas_tempo_estimado
		CHECK (
			tempo_estimado IS NULL
            OR tempo_estimado > 0
        ),
        
	CONSTRAINT ck_tarefas_meta_ou_objetivo
		CHECK (
			id_meta IS NULL
            OR id_objetivo IS NULL
        )
);

CREATE TABLE IF NOT EXISTS prioridades_diarias (
	id_prioridade_diaria BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_tarefa BIGINT NOT NULL,
    data_prioridade DATE NOT NULL,
    
    CONSTRAINT uq_prioridades_diarias_usuario_data
		UNIQUE (id_usuario, data_prioridade),
	
    CONSTRAINT fk_prioridades_diarias_usuario
		FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario),
	
    CONSTRAINT fk_prioridades_diarias_tarefa
		FOREIGN KEY (id_tarefa)
        REFERENCES tarefas(id_tarefa)
);

CREATE TABLE IF NOT EXISTS sessoes_foco (
	id_sessao_foco BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_tarefa BIGINT,
    data_inicio DATETIME NOT NULL,
    data_fim DATETIME,
    tempo_foco_planejado INT NOT NULL,
    tempo_descanso_planejado INT NOT NULL,
    tempo_foco_realizado INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',
    
    CONSTRAINT fk_sessoes_foco_usuario
		FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario),
	
    CONSTRAINT fk_sessoes_foco_tarefa
		FOREIGN KEY (id_tarefa)
        REFERENCES tarefas(id_tarefa),
	
    CONSTRAINT ck_sessoes_foco_tempo_planejado
		CHECK (tempo_foco_planejado > 0),
	
    CONSTRAINT ck_sessoes_foco_descanso_planejado
		CHECK (tempo_descanso_planejado > 0),
        
	CONSTRAINT ck_sessoes_foco_tempo_realizado
		CHECK (tempo_foco_realizado >= 0)
);

CREATE TABLE IF NOT EXISTS planejamentos_semanais (
	id_planejamento_semanal BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_ciclo BIGINT,
    semana_ciclo TINYINT,
    data_inicio_semana DATE NOT NULL,
    data_fim_semana DATE NOT NULL,
    data_criacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_planejamentos_semanais_usuario
		FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario),
	
    CONSTRAINT fk_planejamentos_semanais_ciclo
		FOREIGN KEY (id_ciclo)
        REFERENCES ciclos(id_ciclo),
        
	CONSTRAINT ck_planejamentos_semanais_semana
		CHECK (
			semana_ciclo IS NULL
            OR semana_ciclo BETWEEN 1 AND 12
        ),
        
	CONSTRAINT ck_planejamentos_semanais_datas
		CHECK (
			data_fim_semana >= data_inicio_semana
        )
);

CREATE TABLE IF NOT EXISTS planejamentos_tarefas (
	id_planejamento_tarefa BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_planejamento_semanal BIGINT NOT NULL,
    id_tarefa BIGINT NOT NULL,
    
    CONSTRAINT uq_planejamentos_tarefas
		UNIQUE (id_planejamento_semanal, id_tarefa),
	
    CONSTRAINT fk_planejamentos_tarefas_planejamento
		FOREIGN KEY (id_planejamento_semanal)
        REFERENCES planejamentos_semanais(id_planejamento_semanal),
        
	CONSTRAINT fk_planejamentos_tarefas_tarefa
		FOREIGN KEY (id_tarefa)
        REFERENCES tarefas(id_tarefa)
);

CREATE TABLE IF NOT EXISTS revisoes_semanais (
	id_revisao_semanal BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_planejamento_semanal BIGINT NOT NULL,
    score_execucao DECIMAL(5,2),
    principais_conquistas TEXT,
    dificuldades TEXT,
    ajustes_proxima_semana TEXT,
    observacoes TEXT,
    data_revisao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uq_revisoes_semanais_planejamento
		UNIQUE (id_planejamento_semanal),
        
	CONSTRAINT fk_revisoes_semanais_planejamento
		FOREIGN KEY (id_planejamento_semanal)
        REFERENCES planejamentos_semanais(id_planejamento_semanal),
        
	CONSTRAINT ck_revisoes_semanais_score
		CHECK (
			score_execucao IS NULL
            OR score_execucao BETWEEN 0 AND 100
        )
);
