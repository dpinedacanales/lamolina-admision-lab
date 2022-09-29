package edu.pe.lamolina.admision.config;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import edu.pe.lamolina.admision.controller.website.WebsiteService;

@ControllerAdvice(basePackages = {"edu.pe.lamolina.admision.controller"})
public class WebsiteAdvice {

    @Autowired
    WebsiteService service;

    @Autowired
    DespliegueConfig despliegueConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ModelAttribute
    public void allModel(Model model) {

        model.addAttribute("service", service);
    }

    @ExceptionHandler
    public String handleError(HttpServletRequest req, Exception ex) throws Exception {
        logger.error("\nGENERAL ERROR :: {} {}", req.getRequestURL(), ex.getLocalizedMessage(), ex);
        return "redirect:/";
    }

}
