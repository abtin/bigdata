package com.novadox.bigdata.gemfire.service;

import com.gemstone.gemfire.admin.SystemMembershipEvent;
import com.gemstone.gemfire.admin.SystemMembershipListener;
import com.gemstone.gemfire.cache.Region;
import com.novadox.bigdata.common.model.Constants;
import com.novadox.bigdata.common.model.Person;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;

@Service
@Configuration
public class RabbitReader implements SystemMembershipListener {
    private static Logger log = LoggerFactory.getLogger(RabbitReader.class);

//    @Autowired
//    private AmqpTemplate amqpTemplate;

    @Resource(name= Constants.PERSON_REGION)
    private Region<String, Person> personRegion;

    private SimpleMessageListenerContainer container;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public SimpleMessageListenerContainer container() {
        log.info("Registering queue listener connecting to rabbit on [{}:{}] virtualHost [{}]",
                this.connectionFactory.getHost(), connectionFactory.getPort(), connectionFactory.getVirtualHost());

        container = new SimpleMessageListenerContainer(this.connectionFactory);
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


    @Override
    public void memberJoined(SystemMembershipEvent systemMembershipEvent) {
        log.info("memberJoined memberId=[{}]", systemMembershipEvent.getMemberId());
    }

    @Override
    public void memberLeft(SystemMembershipEvent systemMembershipEvent) {
        log.info("memberLeft memberId=[{}]", systemMembershipEvent.getMemberId());
        container.stop();
    }

    @Override
    public void memberCrashed(SystemMembershipEvent systemMembershipEvent) {
        log.info("memberLeft memberId=[{}]", systemMembershipEvent.getMemberId());
        container.stop();
    }
}
