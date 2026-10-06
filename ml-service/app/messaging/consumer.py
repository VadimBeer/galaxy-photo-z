import json
import logging
from app.config import rabbit_config
from app.storage import minio_client
from app.shemas.shemas import ImgTaskMessage

log = logging.getLogger(__name__)

def to_img_task(body: bytes) -> ImgTaskMessage:
    return ImgTaskMessage.model_validate_json(body)


def callback(ch, method, properties, body):
    try:
        message = to_img_task(body)
        log.info("Получено сообщение: %s", message)
        ch.basic_ack(delivery_tag=method.delivery_tag)
    except json.JSONDecodeError:
        log.exception("Невалидный JSON, сообщение отбрасывается")
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)




def consumer():
    connection = rabbit_config.get_connection()
    channel = connection.channel()

    channel.exchange_declare(rabbit_config.IMG_EXCHANGE, exchange_type="direct", durable=True)
    channel.queue_declare(queue=rabbit_config.IMG_QUEUE, durable=True)
    channel.queue_bind(
        queue=rabbit_config.IMG_QUEUE,
        exchange=rabbit_config.IMG_EXCHANGE,
        routing_key=rabbit_config.IMG_ROUTING_KEY,
    )

    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue=rabbit_config.IMG_QUEUE, on_message_callback=callback)

    log.info("Ожидание сообщений...")
    try:
        channel.start_consuming()
    except KeyboardInterrupt:
        channel.stop_consuming()
    finally:
        connection.close()