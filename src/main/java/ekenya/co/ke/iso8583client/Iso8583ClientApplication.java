package ekenya.co.ke.iso8583client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Iso8583ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(Iso8583ClientApplication.class, args);
    }

}
