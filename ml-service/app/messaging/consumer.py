import logging
import pika
from pydantic_core._pydantic_core import ValidationError

from app.config import rabbit_config
from app.service.pipeline import process
from app.shemas.shemas import ImgTaskMessage

log = logging.getLogger(__name__)


def to_img_task(body: bytes) -> ImgTaskMessage:
    return ImgTaskMessage.model_validate_json(body)


def publish_result(ch, result) -> None:
    ch.basic_publish(
        exchange=rabbit_config.CHART_EXCHANGE,
        routing_key=rabbit_config.CHART_ROUTING_KEY,
        body=result.model_dump_json(by_alias=True).encode("utf-8"),
        properties=pika.BasicProperties(
            content_type="application/json",
            delivery_mode=pika.DeliveryMode.Persistent,
        ),
    )
    log.info("Результат задачи %s отправлен в %s",
             result.task_id, rabbit_config.CHART_EXCHANGE)


def callback(ch, method, properties, body):
    try:
        message = to_img_task(body)
    except ValidationError:
        log.exception("Невалидное сообщение: %s", body.decode(errors="replace"))
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
        return

    log.info("Получено сообщение: %s", message)

    try:
        result = process(message)      # внутри уже ловит ошибки и возвращает FAILED
        publish_result(ch, result)
        ch.basic_ack(delivery_tag=method.delivery_tag)
    except Exception:
        log.exception("Не удалось обработать или отправить результат задачи %s",
                      message.task_id)
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)


def consumer():
    connection = rabbit_config.get_connection()
    channel = connection.channel()

    channel.exchange_declare(rabbit_config.IMG_EXCHANGE, exchange_type="direct", durable=True)
    channel.exchange_declare(rabbit_config.CHART_EXCHANGE, exchange_type="direct", durable=True)
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