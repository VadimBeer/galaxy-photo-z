--liquibase formatted sql

--changeset vadimbeer:catboost-v1-test-metrics
UPDATE model_version
SET
    mae  = 0.026639327062340158,
    mse  = 0.0017030095625933095,
    rmse = 0.041267536425055824,
    r2   = 0.9645580062270817
WHERE id = 3
  AND name = 'CatBoost'
  AND version = 'V1';