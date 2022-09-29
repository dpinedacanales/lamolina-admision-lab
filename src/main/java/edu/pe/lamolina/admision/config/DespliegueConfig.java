package edu.pe.lamolina.admision.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "despliegue")
public class DespliegueConfig {

    @Value("${mailer}")
    Boolean mailer;

    @Value("${lagunas}")
    Boolean lagunas;

    @Value("${emails}")
    String emails;

    @Value("${copias}")
    String copias;

    @Value("${emailcontacto}")
    String emailcontacto;

    @Value("${storage}")
    Boolean storage;

    @Value("${websocket}")
    String websocket;

    @Value("${ambiente}")
    String ambiente;

    @Value("${sistema}")
    Long sistema;

    public boolean isProduccion() {
        return this.ambiente.equals("prod");
    }

}
