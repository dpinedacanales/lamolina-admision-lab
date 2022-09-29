package edu.pe.lamolina.admision.config;

import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "asterisk")
public class AsteriskConfig {

    @Value("${ahost}")
    String host;

    @Value("${auser}")
    String user;

    @Value("${apassword}")
    String password;

    @Bean
    public ManagerConnection managerConnection() {
        ManagerConnectionFactory factory = new ManagerConnectionFactory(
                host, user, password);

        return factory.createManagerConnection();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
