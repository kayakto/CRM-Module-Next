package org.bitebuilders.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"org.bitebuilders.telegram"})
public class TelegramBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }
}
