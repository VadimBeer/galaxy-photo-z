-- liquibase formatted sql

-- changeset vadimbeer:add-metrics-to-model-version
ALTER TABLE model_version
    ADD COLUMN mae  DOUBLE PRECISION,
    ADD COLUMN mse  DOUBLE PRECISION,
    ADD COLUMN rmse DOUBLE PRECISION,
    ADD COLUMN r2   DOUBLE PRECISION;