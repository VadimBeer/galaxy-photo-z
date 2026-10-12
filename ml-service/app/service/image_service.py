from PIL import Image
import io

from app.storage.minio_client import download_galaxy_photo


def preprocessing_image(image: bytes) -> Image.Image:
    img = Image.open(io.BytesIO(image))

    if img.width != 128 or img.height != 128:
        img = img.resize((128, 128))

    return img
