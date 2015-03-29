package com.novadox.bigdata.gemfire.server;

import com.novadox.bigdata.gemfire.function.PersonFunction;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctions;

@Configuration
@EnableGemfireFunctions
@ImportResource("local/server-cache.xml")
@EnableRabbit
public class ServerConfig {

    @Bean
    PersonFunction personFunction() {
        return new PersonFunction();
    }
}
