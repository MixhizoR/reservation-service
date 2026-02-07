package com.omniticket.reservation_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "ticket.purchase.queue";
    public static final String EXCHANGE_NAME = "ticket.exchange";
    public static final String ROUTING_KEY = "ticket.purchase.routing.key";

    @Bean
    public Queue ticketQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue ticketQueue, TopicExchange ticketExchange) {
        return BindingBuilder.bind(ticketQueue).to(ticketExchange).with(ROUTING_KEY);
    }
}