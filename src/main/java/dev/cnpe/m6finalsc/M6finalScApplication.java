package dev.cnpe.m6finalsc;

import com.github.javafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.util.Locale;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class M6finalScApplication {


    public static void main(String[] args) {
        SpringApplication.run(M6finalScApplication.class, args);
    }


    @Bean
    Faker faker() {
        return new Faker(new Locale("es-ES"));
    }

}
