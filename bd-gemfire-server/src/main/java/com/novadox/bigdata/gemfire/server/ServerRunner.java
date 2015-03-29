package com.novadox.bigdata.gemfire.server;

import com.gemstone.gemfire.distributed.ServerLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class ServerRunner {
    private static Logger log = LoggerFactory.getLogger(ServerRunner.class);

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(ServerConfig.class);

        ServerLauncher serverLauncher  = new ServerLauncher.Builder()
                .setMemberName("server1")
                .setServerPort(40404)
                .build();

        ServerLauncher.ServerState status = serverLauncher.start();

        log.info("Server started successfully on gemfire "+status.getGemFireVersion());
        log.info("Server classpath: "+status.getClasspath());
    }
}
