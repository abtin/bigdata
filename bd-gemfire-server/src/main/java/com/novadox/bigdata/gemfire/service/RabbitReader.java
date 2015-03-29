package com.novadox.bigdata.gemfire.service;

import com.gemstone.gemfire.cache.Region;
import com.novadox.bigdata.common.api.Constants;
import com.novadox.bigdata.common.model.Person;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;

@Service
public class RabbitReader {

//    @Autowired
//    private AmqpTemplate amqpTemplate;

    @Resource(name= Constants.PERSON_REGION)
    private Region<String, Person> personRegion;

    @Autowired
    private ConnectionFactory connectionFactory;

    //todo: uncomment "@Bean" to make the listner start with gemfire!
//    @Bean
    public SimpleMessageListenerContainer container() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(this.connectionFactory);
        Object listener = new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                Person sr = (Person) SerializationUtils.deserialize(message.getBody());
                personRegion.put(sr.getKey(), sr);
            }
        };

        MessageListenerAdapter adapter = new MessageListenerAdapter(listener);

        container.setMessageListener(adapter);
        container.setQueueNames(Constants.PERSON_HAWQ_TO_GEMFIRE_QUEUE);
        return container;
    }


}
