package dev.cnpe.m6finalsc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class M6finalScApplicationTests {

    @Test
    void contextLoads() {
    }

}
