import pika

img_queue = 'astroml/img'
chart_queue = 'astroml/chart'
username_rabbit = 'guest'
password_rabbit = 'guest'

credentials = pika.PlainCredentials(username_rabbit, password_rabbit)
parameters = pika.ConnectionParameters(
    host='localhost',
    port=5672,
    credentials=credentials
)

connection = pika.BlockingConnection(parameters)
channel = connection.channel()

channel.queue_declare(queue=img_queue, durable=True)
channel.queue_declare(queue=chart_queue, durable=True)

