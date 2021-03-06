package com.novadox.bigdata.rabbit.sender;

import com.novadox.bigdata.common.model.Constants;
import com.novadox.bigdata.common.model.Person;
import org.fluttercode.datafactory.impl.DataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;

@ComponentScan
@EnableAutoConfiguration
public class SenderApp implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SenderApp.class);

    @Autowired
    AnnotationConfigApplicationContext context;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private DataFactory df = new DataFactory();
    private Date minAge = df.getDate(2000, 1, 1);
    private Date maxAge = df.getDate(1970, 1, 1);

    private Person fakePerson() {
        Person faked = new Person();
        faked.setFirstName(df.getName());
        faked.setLastName(df.getLastName());
        faked.setDateOfBirth(df.getDateBetween(minAge, maxAge));
        faked.setAddress(df.getAddress());

        return faked;
    }


    @Override
    public void run(String... args) throws Exception {
        log.info("Springboot app started!");

        int count = 1;
        if (args != null && args.length > 0) {
            count = Integer.valueOf(args[0]);
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            rabbitTemplate.convertAndSend(Constants.PERSON_HAWQ_TO_GEMFIRE_QUEUE, fakePerson());
        }
        long totalTime = System.currentTimeMillis() - startTime;
        log.info(String.format("sent [%d] messages in [%d] milliseconds", count, totalTime));
        context.close();
    }

    public static void main(String[] args) {
        SpringApplication.run(SenderApp.class, args);
    }
}
