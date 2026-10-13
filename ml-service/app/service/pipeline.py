import logging

from app.ml.inference import regression_quantile, make_quantile_chart
from app.ml.model_CNN import transform_image_cnn
from app.ml.model_loader import load_model
from app.service.image_service import preprocessing_image
from app.shemas.shemas import ImgTaskMessage, ChartTaskPublisher, Status
from app.storage.minio_client import download_galaxy_photo, upload_chart

log = logging.getLogger(__name__)

TAUS = [round(0.05 * (i + 1), 2) for i in range(19)]

def process(task: ImgTaskMessage) -> ChartTaskPublisher:

    try:
        model = load_model(task.model_version_id)
        img = preprocessing_image(download_galaxy_photo(task.get_object_key()))
        transforms = transform_image_cnn()
        prediction = regression_quantile(model, img, transforms)

        plt = make_quantile_chart(prediction)

        chart_name = upload_chart(plt, task)

        quantiles = prediction.squeeze().tolist()
        median = quantiles[len(quantiles) // 2]

        log.info("Отправлен: %s",chart_name)
        chart_task = ChartTaskPublisher(task_id=task.task_id,
                                      user_id=task.user_id,
                                      chart_object_key=chart_name,
                                      quantiles=quantiles,
                                      median_quantile=median,
                                      status=Status.COMPLETED
                                      )

        return chart_task

    except Exception as e:
        log.exception(e)
        return ChartTaskPublisher(task_id=task.task_id,
                                  user_id=task.user_id,
                                  quantiles=None,
                                  median_quantile=None,
                                  status=Status.FAILED,
                                  error_message=str(e)[:1000]
                                  )

