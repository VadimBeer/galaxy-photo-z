import logging

from app.messaging.consumer import consumer
from app.ml.model_loader import load_model
from app.service import pipeline

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(name)s: %(message)s",
)

if __name__ == "__main__":
    load_model(1)
    load_model(2)
    consumer()