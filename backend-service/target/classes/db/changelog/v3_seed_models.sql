--liquibase formatted sql

--changeset vadimbeer:seed-cnn-v2
INSERT INTO model_version (
    id,
    name,
    version,
    num_quantiles,
    weights_object_key
)
VALUES (
           2,
           'CNN',
           'V2',
           19,
           'weights/CNN_V2.pth'
       )
ON CONFLICT (id) DO NOTHING;