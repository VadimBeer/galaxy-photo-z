import json
import logging

from app import config
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
    connection = config.get_connection()
    channel = connection.channel()

    channel.exchange_declare(config.IMG_EXCHANGE, exchange_type="direct", durable=True)
    channel.queue_declare(queue=config.IMG_QUEUE, durable=True)
    channel.queue_bind(
        queue=config.IMG_QUEUE,
        exchange=config.IMG_EXCHANGE,
        routing_key=config.IMG_ROUTING_KEY,
    )

    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue=config.IMG_QUEUE, on_message_callback=callback)

    log.info("Ожидание сообщений...")
    try:
        channel.start_consuming()
    except KeyboardInterrupt:
        channel.stop_consuming()
    finally:
        connection.close()