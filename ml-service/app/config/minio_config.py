from minio import Minio
from minio.error import S3Error
import os

MINIO_ENDPOINT = os.getenv("MINIO_ENDPOINT", "localhost:9000")
MINIO_ACCESS_KEY = os.getenv("MINIO_ACCESS_KEY", "vadim")
MINIO_SECRET_KEY = os.getenv("MINIO_SECRET_KEY", "minio_password_123")
MINIO_SECURE = os.getenv("MINIO_SECURE", "false").lower() == "true"

PHOTOS_BUCKET = os.getenv("MINIO_PHOTOS_BUCKET", "galaxy-photos")
# CHARTS_BUCKET = os.getenv("MINIO_CHARTS_BUCKET", "galaxy-charts")  # сверьте с Java






