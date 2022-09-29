/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pe.lamolina.admision.controller.admision.inscripcion.loginContingencia;

import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.InscripcionService;
import edu.pe.lamolina.admision.zelper.constant.Constantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Prelamolina;

/**
 *
 * @author dpinedac
 */
@Controller
@RequestMapping("contingencia/facebook")
public class LoginContingenciaController {

    @Autowired
    InscripcionService service;

    @Autowired
    LoginContingenciaService loginContingenciaService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("logincontingencia")
    public String logincontingencia(Model model, HttpSession session) {

        DataSessionAdmision ds = new DataSessionAdmision();
        System.out.println("INGRESEEEEEEEE --------------->");
        CicloPostula ciclo = service.getCicloPostulaActivo();
        Interesado interesado = new Interesado();
        interesado.setEstado(InteresadoEstadoEnum.CRE);
        interesado.setCicloPostula(ciclo);
        interesado.setContingecia(1);
        List<TipoDocIdentidad> documentos = service.allTiposDocIdentidad();
        List<Carrera> carreras = service.allCarreras();
        List<CarreraNueva> carrerasNuevas = service.allCarreraNuevas();
        ContenidoCarta contenido = null;

        if (ciclo.getEsVirtual()) {
            contenido = service.findContenidoCartaByCodigoEnum(ContenidoCartaEnum.DECLARA_VIRTUAL);
        }
        ds.setCicloPostula(ciclo);
        ds.setInteresado(interesado);
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

        model.addAttribute("contenido", contenido);
        model.addAttribute("interesado", interesado);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("documentos", documentos);
        model.addAttribute("carreras", carreras);
        model.addAttribute("carrerasNuevas", carrerasNuevas);
        model.addAttribute("rutaForm", "/facebook/save");
        model.addAttribute("esContingencia", true);
        return "admision/postulante/interesado/interesado";

    }

    @RequestMapping("verifica/contingenica")
    public String verificaContingencia(Model model, HttpSession session) {
        System.out.println("INGRESEEEEEEEE --------------->");
        Interesado interesado = new Interesado();
        List<TipoDocIdentidad> documentos = service.allTiposDocIdentidad();
        CicloPostula ciclo = service.getCicloPostulaActivo();
        model.addAttribute("documentos", documentos);
        model.addAttribute("esContingencia", true);
        model.addAttribute("esVirtual", ciclo.getEsVirtual());
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("interesado", interesado);
        return "admision/postulante/contingencia/interesadoContingencia";
    }

    @ResponseBody
    @RequestMapping(value = "lagunas/login")
    public JsonResponse lagunas(@ModelAttribute("interesado") Interesado interesado, HttpSession session, Model model) {
        JsonResponse response = new JsonResponse();

        try {
//            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(Constantine.SESSION_USUARIO);
//            if (ds != null) {
//                interesado = ds.getInteresado();
//            }
            Boolean existe = loginContingenciaService.loginManuallyContingencia(interesado, session);
            
            if (!existe) {
                response.setMessage("No te encuestras inscrito aún.");
            }
            
            response.setSuccess(existe);
            response.setData("contingencia/facebook/rev66");
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error extremo al autenticar: {}", e.getLocalizedMessage());
            response.setData("/");
            return response;
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

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@ModelAttribute("interesado") Interesado interesado, Model model, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.TRUE);
        try {
            System.out.println("INGRESEEEEEEEE --------------->");
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Interesado interesadoSession = ds.getInteresado();

            if (interesadoSession.getEstadoEnum() != InteresadoEstadoEnum.CRE && interesado.getIdTaller() == null) {
                response.setData("/");
                return response;
            }

            loginContingenciaService.verificarFechaInscripcion(interesadoSession.getCicloPostula(), interesado);

            interesado.setNumeroDocIdentidad(interesado.getNumeroDocIdentidad().replace(",", ""));
            interesado.setId(interesadoSession.getId());
            interesado.setCicloPostula(interesadoSession.getCicloPostula());
            interesado.setFechaRegistro(new Date());
            interesado.setContingecia(interesadoSession.getContingecia());
            interesado.setFacebook(interesado.getNumeroDocIdentidad() + "-" + interesado.getCodigoVerificacion());
            interesado.setFacebookLink(interesado.getNumeroDocIdentidad() + "-" + interesado.getCodigoVerificacion());
            service.updateInteresado(interesado);

            SecurityContext ctx = SecurityContextHolder.getContext();
            Collection<GrantedAuthority> authorities = new ArrayList();
            authorities.add(new SimpleGrantedAuthority("ADMINISTRADOR"));

            Authentication authentication = new UsernamePasswordAuthenticationToken(interesado.getNombres(), interesado.getNombres(), authorities);
            ctx.setAuthentication(authentication);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);

            ds.setInteresado(interesado);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

            Prelamolina cepre = service.buscarComoCepre(interesado, ds.getCicloPostula(), 1);

            if (interesado.getIdTaller() != null) {
                service.saveInscritoTaller(interesado);

                response.setData("inscripcion/facebook/taller?id=" + interesado.getIdTaller());
                return response;
            }

            if (cepre == null) {
                response.setData("inscripcion/facebook/opcion");
                return response;
            }

            ds.setPrelamolina(cepre);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

            response.setData("inscripcion/facebook/ingresanteCepre");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);

        }

        return response;
    }
}
