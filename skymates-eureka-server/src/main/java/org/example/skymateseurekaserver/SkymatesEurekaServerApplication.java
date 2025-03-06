package org.example.skymateseurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SkymatesEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkymatesEurekaServerApplication.class, args);
    }

}
