package com.example.sqs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;

@SpringBootApplication
public class SqsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqsApplication.class, args);
    }

}
