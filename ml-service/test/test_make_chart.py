
from app.ml.model_loader import load_model
from app.service.image_service import preprocessing_image
from app.shemas.shemas import ImgTaskMessage
from app.storage.minio_client import download_galaxy_photo
from app.ml.model_CNN import transform_image_cnn
from app.ml.inference import regression_quantile
from app.ml.inference import make_quantile_chart
from app.storage.minio_client import upload_chart

message = ImgTaskMessage(
    task_id=10,
    user_id=1,
    model_version=2,
    object_key="6847888d-6017-4b36-a9d2-d6bb92b95e64"
)

model = load_model(2)
img = preprocessing_image(download_galaxy_photo(message.get_object_key()))
transforms = transform_image_cnn()
prediction = regression_quantile(model, img, transforms)

plt = make_quantile_chart(prediction)

object = upload_chart(plt, message)
print(object)