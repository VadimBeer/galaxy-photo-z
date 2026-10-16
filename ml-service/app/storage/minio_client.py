from functools import lru_cache
from minio import Minio
from app.config import minio_config
from app.service.image_service import chart_to_bytes
from app.shemas.shemas import ImgTaskMessage


@lru_cache(maxsize=1)
def minio_client() -> Minio:

    return Minio(
        minio_config.MINIO_ENDPOINT,
        access_key=minio_config.MINIO_ACCESS_KEY,
        secret_key=minio_config.MINIO_SECRET_KEY,
        secure=False,
    )

def download_galaxy_photo(object_key: str):
    file = minio_client().get_object(minio_config.PHOTOS_BUCKET, object_key)

    try:
        return file.read()
    except FileNotFoundError as e:
        file.close()
        file.release_conn()
        print(e)
    finally:
        file.close()

def upload_chart(fig, img_task_message: ImgTaskMessage) -> str:
    buf = chart_to_bytes(fig)
    size = buf.getbuffer().nbytes
    object_key = 'charts/' + img_task_message.get_object_key()

    try:
        minio_client().put_object(
            bucket_name=minio_config.PHOTOS_BUCKET,
            object_name=object_key,
            data=buf,
            length=size,
            content_type="image/png",
        )
        return object_key
    except Exception as e:
        raise e

