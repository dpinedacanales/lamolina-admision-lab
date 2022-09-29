package edu.pe.lamolina.admision.security.oauth;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.controller.admision.fichasocioeconomica.DatosGeneralesService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.InscripcionService;
import edu.pe.lamolina.admision.zelper.constant.Constantine;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.POST;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.REG;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.Interesado;

@Controller
public class FacebookController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    private Facebook facebook;

    @Autowired
    private FacebookService service;

    @Autowired
    private InscripcionService inscripcionService;

    @Autowired
    private DatosGeneralesService datosGeneralesService;

    @Autowired
    private ConnectionRepository connectionRepository;

    @RequestMapping("facebook/callback")
    public String callBackFacebook(Model model, HttpSession session) {

        try {
            Connection conn = connectionRepository.findPrimaryConnection(Facebook.class);

            if (conn == null) {
                return "redirect:/connect/facebook";
            }

            String[] fields = {"id", "name", "first_name", "middle_name", "last_name", "birthday", "email", "gender", "link", "picture"};
            User user = facebook.fetchObject("me", User.class, fields);

            service.loginManually(user, session);

            return "redirect:/rev66";

        } catch (Exception e) {
            logger.error("Error extremo al autenticar: {}", e.getLocalizedMessage());
            e.printStackTrace();
            return "redirect:/";
        }

    }

    @RequestMapping(value = "lagunas/{idfacebook:.*}", method = RequestMethod.GET)
    public String lagunas(@PathVariable String idfacebook, HttpSession session, Model model) {

        try {

            Interesado interesado = null;
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(Constantine.SESSION_USUARIO);
            if (ds != null) {
                interesado = ds.getInteresado();
            }
            if (!despliegueConfig.getLagunas()) {
                if (!(interesado != null && interesado.getEsAdministrador() == 1)) {
                    return "redirect:/";
                }
            }

            User userFace = new User(idfacebook, "", "", "", "", Locale.US);
            service.loginManually(userFace, session);

            return "redirect:/rev66";

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error extremo al autenticar: {}", e.getLocalizedMessage());
            return "redirect:/";
        }

    }

    @RequestMapping("rev66")
    public String rev66(HttpSession session) {
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(Constantine.SESSION_USUARIO);
        if (ds == null) {
            return "redirect:/";
        }

        return "admision/security/rev66";
    }

    @RequestMapping("route66")
    public String route66(@RequestParam(name = "page", required = false) String page,
            @RequestParam(name = "objectid", required = false) String objectid,
            HttpSession session, Model model) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(Constantine.SESSION_USUARIO);
        if (ds == null) {
            return "redirect:/";
        }

        logger.debug("PAGE {}", page);
        logger.debug("OBJECTID {}", objectid);

        Boolean esFinalInscripciones = inscripcionService.esFinalInscripciones(ds.getCicloPostula());

        try {

            Interesado interesado = ds.getInteresado();
            if (!StringUtils.isBlank(page)) {
                return "redirect:/inscripcion/facebook?objId=" + objectid;
            }
            if (interesado.getId() != null) {
                service.updateInteresado(interesado);
            }
            switch (interesado.getEstadoEnum()) {

                case CRE:
                    if (esFinalInscripciones) {
                        return "redirect:/";
                    }
                    return "redirect:/inscripcion/facebook";

                case REG:
                    if (esFinalInscripciones) {
                        model.addAttribute("esVirtual", ds.getCicloPostula().getEsVirtual());
                        model.addAttribute("esSimulacro", ds.getCicloPostula().getEsSimulacro());
                        return "redirect:/";
                    }
                    return "redirect:/inscripcion/facebook/opcion";

                case PROS:
                    return "redirect:/inscripcion/guiapostulante/recurso";

                case POST:
                    return "redirect:/inscripcion/postulante";

            }
            return "redirect:/";

        } catch (Exception e) {
            logger.error("Error extremo en route66s: {}", e.getLocalizedMessage());

            return "redirect:/";
        }
    }

    @RequestMapping("logout")
    public String logout(HttpServletRequest request) throws Exception {

        Connection conn = null;
        try {
            conn = connectionRepository.findPrimaryConnection(Facebook.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.getSession().invalidate();
        return "redirect:/";
    }

    @RequestMapping("{idPostulante}/gomaipi")
    public String goMaipi(
            @PathVariable("idPostulante") Long idPostulante,
            @RequestParam(value = "origen", required = false) String origen,
            Model model, HttpSession session) throws InterruptedException {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(Constantine.SESSION_USUARIO);
        datosGeneralesService.asignarMatricula(idPostulante, ds, session);
        String codigo = datosGeneralesService.goMaipi(ds.getAlumno(), ds.getUsuario());
        Persona persona = ds.getPersona();
        Parametro paramRutaMatricula = datosGeneralesService.findParametroByEnum(ParametrosSistemasEnum.SALTO_HACIA_INTRANET);
        if (paramRutaMatricula != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("redirect:");
            sb.append(paramRutaMatricula.getValor());
            sb.append("/admision/");
            sb.append(persona.getId());
            sb.append("/");
            sb.append(ds.getUsuario().getId());
            logger.debug("********************** goIntranet {} ", sb.toString());

            AmbienteAplicacionEnum ambiente = AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase());
            if (ambiente == AmbienteAplicacionEnum.DESA) {
                session.invalidate();
            }

            return sb.toString();
        }
        return "redirect:/";
    }

    @RequestMapping("totoro/{idPostulante}/{user}")
    public String totoro(
            @PathVariable Long idPostulante,
            @PathVariable Long user, HttpSession session) {

        try {
            service.authFromTotoro(idPostulante, user, session);
            return "redirect:/inscripcion/hojarecorrido";

        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("ERROR", e);
            return "redirect:/";
        }
    }

}
