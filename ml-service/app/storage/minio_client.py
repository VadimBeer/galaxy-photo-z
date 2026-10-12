from functools import lru_cache
from minio import Minio
from app.config import minio_config

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

