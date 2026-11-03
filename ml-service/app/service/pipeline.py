import logging

import matplotlib.pyplot as plt
import numpy as np

from app.ml.inference import (
    regression_quantile,
    regression_quantile_catboost,
    make_quantile_chart,
)
from app.ml.model_CNN import transform_image_cnn
from app.ml.model_loader import (
    find_model,
    load_model,
    load_catboost_metadata,
)
from app.ml.preprocessing import read_catboost_csv
from app.service.image_service import preprocessing_image
from app.shemas.shemas import (
    ImgTaskMessage,
    ChartTaskPublisher,
    Status,
    QuantilePoint,
)
from app.storage.minio_client import download_input_file, upload_chart

log = logging.getLogger(__name__)

CNN_TAUS = np.round(np.linspace(0.05, 0.95, 19), 2)


def process(task: ImgTaskMessage) -> ChartTaskPublisher:
    try:
        dto = find_model(task.model_version_id)
        model = load_model(task.model_version_id)
        file_bytes = download_input_file(task.get_object_key())

        if dto.model_name == "CATBOOST_V1":
            metadata = load_catboost_metadata()
            taus = np.asarray(metadata["taus"], dtype=float)

            features = read_catboost_csv(file_bytes, metadata)
            prediction = regression_quantile_catboost(
                model, features, taus
            )

        elif dto.model_name in ("CNN_V1", "CNN_V2"):
            taus = CNN_TAUS

            img = preprocessing_image(file_bytes)
            transforms = transform_image_cnn()

            prediction = regression_quantile(
                model, img, transforms
            ).detach().cpu().numpy()

        else:
            raise ValueError(
                f"Обработка модели {dto.model_name} не реализована"
            )

        prediction = np.asarray(prediction, dtype=float)

        if prediction.shape != (1, len(taus)):
            raise ValueError(
                f"Неверная форма прогноза: {prediction.shape}"
            )

        if not np.isfinite(prediction).all():
            raise ValueError("Прогноз содержит некорректные значения")

        values = np.sort(prediction[0])

        median_indices = np.flatnonzero(
            np.isclose(taus, 0.5, rtol=0, atol=1e-8)
        )
        if len(median_indices) != 1:
            raise ValueError("В уровнях квантилей должна быть одна медиана")

        median = float(values[median_indices[0]])
        quantiles = [
            QuantilePoint(tau=float(t), value=float(v))
            for t, v in zip(taus, values)
        ]

        fig = make_quantile_chart(values, taus)

        try:
            chart_name = upload_chart(fig, task)
        finally:
            plt.close(fig)

        log.info(
            "Задача %s обработана моделью %s, график: %s",
            task.task_id,
            dto.model_name,
            chart_name,
        )

        return ChartTaskPublisher(
            task_id=task.task_id,
            user_id=task.user_id,
            chart_object_key=chart_name,
            quantiles=quantiles,
            median_quantile=median,
            state=Status.COMPLETED,
            content_type="image/png",
        )

    except Exception as exc:
        log.exception("Ошибка обработки задачи %s", task.task_id)

        return ChartTaskPublisher(
            task_id=task.task_id,
            user_id=task.user_id,
            quantiles=None,
            median_quantile=None,
            state=Status.FAILED,
            error_message=str(exc)[:1000],
        )