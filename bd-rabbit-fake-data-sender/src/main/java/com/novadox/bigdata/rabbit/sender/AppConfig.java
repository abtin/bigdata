package com.novadox.bigdata.rabbit.sender;

import com.novadox.bigdata.common.model.Constants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AppConfig {

    @Value("${rabbitServerHost}")
    private String rabbitSererHost;

    @Value("${rabbitUserName}")
    private String rabbitUser;

    @Value("${rabbitPassword}")
    private String rabbitPassword;

    @Value("${rabbitVirtualHost}")
    private String rabbitVirtualHost;

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory cf = new CachingConnectionFactory(rabbitSererHost);
        cf.setVirtualHost(rabbitVirtualHost);
        cf.setUsername(rabbitUser);
        cf.setPassword(rabbitPassword);
        return cf;
    }
    @Bean
    Queue queue() {
        return new Queue(Constants.PERSON_HAWQ_TO_GEMFIRE_QUEUE, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("spring-boot-exchange");
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(Constants.PERSON_HAWQ_TO_GEMFIRE_QUEUE);
    }
}
