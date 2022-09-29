package edu.pe.lamolina.admision.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;

@Component
public class ThymeleafConfig {

    @Autowired
    ThymeleafViewResolver viewResolver;

    @Autowired
    DespliegueConfig despliegueConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void loadStaticVariables() {
        Map<String, String> adicionales = new HashMap();
        adicionales.put("S3_LINK", AdmisionConstantine.S3_URL_ADMISION);
        adicionales.put("S3", despliegueConfig.getStorage() == true ? "TRUE" : "FALSE");
        adicionales.put("socketURL", despliegueConfig.getWebsocket());

        viewResolver.setStaticVariables(adicionales);
    }

}
