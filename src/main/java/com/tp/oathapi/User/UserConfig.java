package com.tp.oathapi.User;

import com.tp.oathapi.Counter.Counter;
import com.tp.oathapi.Counter.CounterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean
    CommandLineRunner commandLineRunner (UserRepository repository, CounterRepository repositoryCounter) {
        return args -> {
            repository.save(new User("siik@siiksounds.com"));
            if (repositoryCounter.findAll().isEmpty()){
                repositoryCounter.save(new Counter(1));
            }

        };
    }
}
