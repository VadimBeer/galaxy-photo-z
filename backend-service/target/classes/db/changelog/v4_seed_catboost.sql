--liquibase formatted sql

--changeset vadimbeer:seed-catboost-v1
INSERT INTO model_version (
    id,
    name,
    version,
    num_quantiles,
    weights_object_key
)
VALUES (
           3,
           'CatBoost',
           'V1',
           19,
           'weights/catboost_photoz.cbm'
       );