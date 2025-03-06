package org.example.skymatesconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class SkymatesConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkymatesConfigServerApplication.class, args);
    }

}
