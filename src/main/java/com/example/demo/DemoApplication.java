package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws IOException {
        Service service = new Service();
        service.findCommonEmployees();
        SpringApplication.run(DemoApplication.class, args);
    }

}
