package com.tp.oathapi.User;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    CommandLineRunner commandLineRunner (UserRepository repository) {
        return args -> {
            repository.save(new User("siik","siik@siiksounds.com"));
        };
    }
}
