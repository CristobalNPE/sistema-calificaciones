package dev.cnpe.m6finalsc;

import org.springframework.boot.SpringApplication;

public class TestM6finalScApplication {

    public static void main(String[] args) {
        SpringApplication.from(M6finalScApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
