package edu.pe.lamolina.admision.controller.general.contacto;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.controller.admision.inscripcion.guiapostulante.GuiaPostulanteService;
import edu.pe.lamolina.admision.zelper.asterisk.AsteriskService;
import edu.pe.lamolina.admision.zelper.mail.MailerService;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;

@Controller
@RequestMapping("contactoForm")
public class ContactoFormController {

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    MailerService mailerService;

    @Autowired
    AsteriskService asteriskService;
    @Autowired
    GuiaPostulanteService guiaPostulanteService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @ResponseBody
    @RequestMapping("enviar")
    public JsonResponse enviar(FormContacto mensaje) {

        JsonResponse response = new JsonResponse();

        try {
            mailerService.enviarEmailContacto(mensaje);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Mensaje enviado correctamente");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }

        return response;
    }

    @RequestMapping("llamame")
    public String llamame(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        ContenidoCarta llamameContent = guiaPostulanteService.findContenidoCartaByCodigo(ContenidoCartaEnum.LLAMAME_ADMISION);

        List<String> numeros = new ArrayList();
        if (ds != null) {
            Persona persona = ds.getPersona();
            if (StringUtils.isBlank(persona.getCelular())) {
                numeros.add(persona.getCelular());
            }
            if (StringUtils.isBlank(persona.getTelefono())) {
                numeros.add(persona.getTelefono());
            }
        }

        model.addAttribute("numeros", numeros);
        model.addAttribute("llamameContent", llamameContent);

        return "admision/llamame/llamameModal";
    }

    @ResponseBody
    @RequestMapping("callMeNow")
    public JsonResponse callMeNow(@RequestParam("numero") String numero, Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();

        logger.debug("Numero {}", numero);
        Boolean isOk = validarCelular(numero);

        try {
            if (!isOk) {
                throw new PhobosException("Por favor Ingrese un número correcto");
            }

            asteriskService.loginToAsterisk();

            List<String> anexos = asteriskService.allExtension();

            response.setSuccess(false);
            response.setMessage("Llamada no concretada. Por favor llamar directamente al número de la Universidad.");

            for (String anexo : anexos) {

                if (asteriskService.getExtensionStatus(anexo)) {

                    asteriskService.generateDial(anexo, numero);

                    response.setSuccess(true);
                    response.setMessage("Llamada en Proceso");
                    break;
                }
            }

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }

        return response;
    }

    private Boolean validarCelular(String numero) {

        if (StringUtils.isBlank(numero)) {
            return false;

        } else if (!numero.matches("[0-9]+")) {
            return false;

        }

        return true;
    }
}
