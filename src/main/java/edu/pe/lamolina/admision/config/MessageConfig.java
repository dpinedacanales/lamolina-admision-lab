package edu.pe.lamolina.admision.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:/messages.properties")
public class MessageConfig {

    @Autowired
    Environment env;

    public String getText(Object obj, Integer key) {
        return env.getProperty(obj.getClass().getSimpleName() + "-" + String.format("%03d", key));
    }

}
