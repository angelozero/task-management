# Event Handler with RabbitMQ

Establishing a connection to RabbitMQ and sets up a "topic" exchange named simple_events.

The publish method sends a message containing an item and its status to this exchange using the routing key update.

The consume method creates a temporary queue, binds it to the simple_events exchange with the update routing key (meaning it will receive messages with this key), and then waits for incoming messages. When a message arrives, it prints the received message and simulates processing.

This example illustrates the core concepts of publishing and consuming events via a message broker in a straightforward manner.

```xml
<!-- https://mvnrepository.com/artifact/com.rabbitmq/amqp-client -->
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>5.25.0</version>
</dependency>
```

```java
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class SimpleEventDemo {

    private static final String EXCHANGE = "simple_events";
    private static final String KEY = "update";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE, "topic");

        publish(channel, "Produto ABC", "atualizado");
        consume(channel);
    }

    private static void publish(Channel channel, String item, String status) throws Exception {
        String message = String.format("Item: %s, Status: %s", item, status);
        channel.basicPublish(EXCHANGE, KEY, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("[Producer] Sent: " + message);
    }

    private static void consume(Channel channel) throws Exception {
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, EXCHANGE, KEY);

        System.out.println("[Consumer] Waiting for messages...");

        DeliverCallback callback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[Consumer] Received: " + message);
            System.out.println("[Consumer] Processing: " + message);
        };

        channel.basicConsume(queue, true, callback, consumerTag -> {});
    }
}
```