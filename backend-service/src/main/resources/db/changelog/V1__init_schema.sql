-- ---------- ENUM-типы ----------

CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

CREATE TYPE task_state AS ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED');

CREATE TABLE users (
   id            BIGSERIAL PRIMARY KEY,
   username      VARCHAR(255) NOT NULL UNIQUE,
   password_hash VARCHAR(255) NOT NULL,
   role          user_role NOT NULL DEFAULT 'USER',
   created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- сущность, которая хранит данные о файле, который хранится в MinIO

CREATE TABLE files_storage (
    file_id           BIGSERIAL PRIMARY KEY,
    object_key        VARCHAR(512) NOT NULL UNIQUE,
    user_id           BIGINT NOT NULL REFERENCES users(id),
    original_filename VARCHAR(255),
    content_type      VARCHAR(100),
    size_bytes        BIGINT NOT NULL CHECK (size_bytes > 0),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    deleted_at        TIMESTAMPTZ
);

CREATE INDEX idx_files_storage_user_id ON files_storage(user_id);

-- версия модели

CREATE TABLE model_version (
   id                 INTEGER PRIMARY KEY,
   name               VARCHAR(100) NOT NULL,
   version            VARCHAR(50) NOT NULL,
   num_quantiles      INTEGER NOT NULL CHECK (num_quantiles > 0),
   val_loss           DOUBLE PRECISION,
   weights_object_key VARCHAR(512) NOT NULL,
   created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
   UNIQUE (name, version)
);

-- сущность, определяющая задачу

CREATE TABLE analysis_task (
    task_id         BIGSERIAL PRIMARY KEY,
    state           task_state NOT NULL DEFAULT 'PENDING',
    file_id         BIGINT NOT NULL REFERENCES files_storage(file_id),
    model_version_id BIGINT NOT NULL REFERENCES model_version(id),
    error_message   VARCHAR(1000),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_analysis_task_file_id ON analysis_task(file_id);
CREATE INDEX idx_analysis_task_model_version_id ON analysis_task(model_version_id);
CREATE INDEX idx_analysis_task_state ON analysis_task(state);

-- результат

CREATE TABLE analysis_result (
    task_id    BIGINT PRIMARY KEY REFERENCES analysis_task(task_id),
    median     DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- результат по квантилям

CREATE TABLE analysis_result_quantile (
      task_id BIGINT NOT NULL REFERENCES analysis_result(task_id),
      tau     DOUBLE PRECISION NOT NULL CHECK (tau > 0 AND tau < 1),
      value   DOUBLE PRECISION NOT NULL,
      PRIMARY KEY (task_id, tau)
);

CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_analysis_task_updated_at
    BEFORE UPDATE ON analysis_task
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();