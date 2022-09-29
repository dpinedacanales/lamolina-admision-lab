package edu.pe.lamolina.admision.config;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfig {

    @Bean
    public Random getGlobalRandom() {
        return new Random();
    }

}
