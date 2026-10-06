import os
import pika

RABBIT_HOST = os.getenv("RABBIT_HOST", "localhost")
RABBIT_PORT = int(os.getenv("RABBIT_PORT", "5672"))
RABBIT_USER = os.getenv("RABBIT_USER", "guest")
RABBIT_PASSWORD = os.getenv("RABBIT_PASSWORD", "guest")

IMG_EXCHANGE = "img_exchange"
CHART_EXCHANGE = "chart_exchange"
IMG_QUEUE = "astroml"            # сверьте с app.rabbitmq в Spring
IMG_ROUTING_KEY = "img"          # сверьте с тем, что шлёт Java
CHART_ROUTING_KEY = "chart"      # сверьте с биндингом на стороне Java


def get_connection() -> pika.BlockingConnection:
    credentials = pika.PlainCredentials(RABBIT_USER, RABBIT_PASSWORD)
    params = pika.ConnectionParameters(
        host=RABBIT_HOST,
        port=RABBIT_PORT,
        credentials=credentials,
        heartbeat=600,
        blocked_connection_timeout=300,
    )

    return pika.BlockingConnection(params)