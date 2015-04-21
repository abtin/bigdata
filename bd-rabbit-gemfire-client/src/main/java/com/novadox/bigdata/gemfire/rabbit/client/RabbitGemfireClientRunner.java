package com.novadox.bigdata.gemfire.rabbit.client;

import com.novadox.bigdata.common.model.Constants;
import com.novadox.bigdata.common.api.StorePersonFunctionApi;
import com.novadox.bigdata.common.model.Person;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RabbitGemfireClientRunner {
    private static Logger log = LoggerFactory.getLogger(RabbitGemfireClientRunner.class);
    private ApplicationContext context = new AnnotationConfigApplicationContext(ClientConfig.class);
    private List<RabbitReader> spawnedReaders;
    private Channel channel;
    private Connection connection;

    public RabbitGemfireClientRunner() throws IOException {
        this.spawnedReaders = new ArrayList<RabbitReader>();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.queueDeclare(Constants.PERSON_HAWQ_TO_GEMFIRE_QUEUE, false, false, false, null);

    }

    public void spawnRabbitReaders(int count) throws IOException {
        StorePersonFunctionApi api = context.getBean(StorePersonFunctionApi.class);

        for (int i=0; i < count; i++) {
            RabbitReader reader = new RabbitReader(api);
            reader.init();
            Thread rabbitReaderThread = new Thread(reader);
            spawnedReaders.add(reader);
            rabbitReaderThread.start();
        }
    }
    public void shutdownRabbitReaders() throws IOException {
        for (RabbitReader spawned : spawnedReaders) {
            spawned.shutdown();
        }
        this.channel.close();
        connection.close();

    }
    public static void main(String[] args) throws IOException {
        RabbitGemfireClientRunner cr = new RabbitGemfireClientRunner();
        int count = 1;
        if (args != null && args.length > 1) {
            count = Integer.valueOf(args[1]);
        }

        System.out.println("Starting ["+count+"] reader threads");
        cr.spawnRabbitReaders(count);

        System.in.read();

        System.out.println("Shutting down reader threads");
        cr.shutdownRabbitReaders();

    }

    class RabbitReader implements Runnable {
        private StorePersonFunctionApi api;
        private boolean isRunning;
        private QueueingConsumer consumer;

        public RabbitReader(StorePersonFunctionApi api) {
            this.api = api;
        }

        public void init() throws IOException {
            this.isRunning = true;

            consumer = new QueueingConsumer(channel);
            channel.basicConsume(Constants.PERSON_HAWQ_TO_GEMFIRE_QUEUE, true, consumer);
        }

        public void shutdown() throws IOException {
            this.isRunning = false;
        }

        @Override
        public void run() {
            while(isRunning) {
                try {
                    QueueingConsumer.Delivery message = consumer.nextDelivery();
                    Person person = (Person) SerializationUtils.deserialize(message.getBody());
                    api.storeSinglePerson(person);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
