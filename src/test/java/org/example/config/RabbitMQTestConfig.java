package org.example.config;

import org.example.properties.RabbitMQProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"test"})
public class RabbitMQTestConfig {

    @Bean
    Queue createAccountQueue() {
        return new Queue(RabbitMQProperties.CREATE_ACCOUNT_QUEUE, true);
    }

    @Bean
    Queue createTransactionQueue() {
        return new Queue(RabbitMQProperties.CREATE_TRANSACTION_QUEUE, true);
    }

    @Bean
    DirectExchange directExchange() {
        return new DirectExchange(RabbitMQProperties.DIRECT_EXCHANGE_NAME);
    }

    @Bean
    Binding bindingAccountCreation(Queue createAccountQueue, DirectExchange exchange) {
        return BindingBuilder.bind(createAccountQueue)
                .to(exchange)
                .with(RabbitMQProperties.CREATE_ACCOUNT_ROUTING_KEY);
    }

    @Bean
    Binding bindingTransactionCreation(Queue createTransactionQueue, DirectExchange exchange) {
        return BindingBuilder.bind(createTransactionQueue)
                .to(exchange)
                .with(RabbitMQProperties.CREATE_TRANSACTION_ROUTING_KEY);
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory factory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(messageConverter());

        return rabbitTemplate;
    }
}