package com.novadox.bigdata.rabbit.receiver;

import com.novadox.bigdata.common.model.Constants;
import com.novadox.bigdata.common.model.Person;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SerializationUtils;

import java.io.IOException;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class ReceiverApp {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public SimpleMessageListenerContainer container() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(this.connectionFactory);
        Object listener = new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                Person someone = (Person) SerializationUtils.deserialize(message.getBody());
                System.out.println("received: " + someone.toString());
            }
        };

        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);
        container.setMessageListener(adapter);
        container.setQueueNames(Constants.PERSON_HAWQ_TO_GEMFIRE_QUEUE);
        return container;
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ReceiverApp.class, args);
    }


}
