from app.ml.model_loader import load_model
from app.service.image_service import preprocessing_image
from app.storage.minio_client import download_galaxy_photo
from app.ml.model_CNN import transform_image_cnn
from app.ml.inference import regression_quantile

model = load_model(2)
img = preprocessing_image(download_galaxy_photo("6847888d-6017-4b36-a9d2-d6bb92b95e64"))
transforms = transform_image_cnn()
prediction = regression_quantile(model, img, transforms)

print(prediction)