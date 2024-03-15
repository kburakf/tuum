package org.example.messaging.publisher;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MessagePublisher {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    DirectExchange directExchange;

    public <T, R> R publishMessage(String routingKey, T message, Class<R> responseType) {
        Object response = rabbitTemplate.convertSendAndReceive(directExchange.getName(), routingKey, message);

        if (response == null) {
            throw new IllegalArgumentException("No response received for routing key: " + routingKey);
        }

        if (!responseType.isInstance(response)) {
            throw new IllegalArgumentException("Response received is not of the expected type: " + responseType.getName());
        }

        return responseType.cast(response);
    }
}
