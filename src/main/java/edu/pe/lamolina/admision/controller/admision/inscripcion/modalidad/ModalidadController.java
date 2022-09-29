package edu.pe.lamolina.admision.controller.admision.inscripcion.modalidad;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.CarreraWeb;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteHelper;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Controller
@RequestMapping("inscripcion/modalidad")
public class ModalidadController {

    @Autowired
    ModalidadService service;

    @Autowired
    SpringTemplateEngine springHtml;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");

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
    @RequestMapping("modalidadCiclo")
    public JsonResponse modalidadCiclo(@RequestParam("modalidad") Long idModalidad, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            CicloPostula ciclo = ds.getCicloPostula();
            Postulante postulanteSess = ds.getPostulante();
            Postulante postulante = service.findPostulante(postulanteSess);
            CarreraPostula carrera = service.findCarreraIngreso(postulante);
            Colegio colegio = service.findColegio(postulante);
            List<ModalidadIngreso> modalidades = service.allModalidadesByCiclo(ciclo);
            for (int i = 0; i < modalidades.size(); i++) {
                if (modalidades.get(i).getId() == idModalidad.longValue()) {
                    modalidades.remove(i);
                }
            }

            ModalidadIngreso modalidad = service.getModalidad(idModalidad);
            List<Universidad> universidades = service.allUniversidadPeru();
            List<GradoSecundaria> grados = service.allGrado();
            ModalidadIngresoCiclo modalidadIngresoCiclo = service.getModalidadIngresoCiclo(modalidad, ciclo);
            Pais peru = service.findPeru();
            logger.debug("CICLO POSTULA year {}", ciclo.getCicloAcademico().getYear());

            List<CarreraPostula> carrerasPostula = service.allCarreras(modalidad, ciclo);
            List<CarreraWeb> carreraz = new ArrayList();
            for (CarreraPostula carr : carrerasPostula) {
                carreraz.add(new CarreraWeb(carr));
            }

            PostulanteHelper helper = new PostulanteHelper();
            Context ctx = new Context();
            ctx.setVariable("helper", new PostulanteHelper());
            ctx.setVariable("postulante", postulante);
            ctx.setVariable("modalidades", modalidades);
            ctx.setVariable("modalidad", modalidad);
            ctx.setVariable("carrera", carrera);
            ctx.setVariable("modalidadCiclo", modalidadIngresoCiclo);
            ctx.setVariable("colegio", colegio);
            ctx.setVariable("ciclo", ciclo);
            ctx.setVariable("peru", peru);
            ctx.setVariable("grados", grados);
            ctx.setVariable("universidades", universidades);
            ctx.setVariable("carrerasPostula", carrerasPostula);
            ctx.setVariable("carreraz", carreraz);
            ctx.setVariable("codigo", postulante.getPaisColegio() != null ? helper.showCodigoPais(postulante.getPaisColegio()) : "");
            String htmlContent = springHtml.process("admision/postulante/modalidad/detallemodalidad", ctx);

            service.testCovenio(postulante, modalidad);

            ds.setModalidad(modalidad);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

            response.setData(htmlContent);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    @ResponseBody
    @RequestMapping("institucionProcedencia")
    public JsonResponse institucionProcedencia(@RequestParam("tipo") String tipo, @RequestParam("modalidad") Long idModalidad, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            CicloPostula ciclo = ds.getCicloPostula();
            Postulante postulanteSess = ds.getPostulante();
            Postulante postulante = service.findPostulante(postulanteSess);
            Colegio colegio = service.findColegio(postulante);

            ModalidadIngreso modalidad = service.getModalidad(idModalidad);
            List<Universidad> universidades = service.allUniversidadPeru();
            ModalidadIngresoCiclo modalidadCiclo = service.getModalidadIngresoCiclo(modalidad, ciclo);
            List<CarreraPostula> carrerasPostula = service.allCarreras(modalidad, ciclo);
            Pais peru = service.findPeru();
            logger.debug("CICLO POSTULA year {}", ciclo.getCicloAcademico().getYear());

            PostulanteHelper helper = new PostulanteHelper();
            Context ctx = new Context();
            ctx.setVariable("helper", new PostulanteHelper());
            ctx.setVariable("postulante", postulante);
            ctx.setVariable("modalidad", modalidad);
            ctx.setVariable("modalidadCiclo", modalidadCiclo);
            ctx.setVariable("colegio", colegio);
            ctx.setVariable("ciclo", ciclo);
            ctx.setVariable("peru", peru);
            ctx.setVariable("universidades", universidades);
            ctx.setVariable("carrerasPostula", carrerasPostula);
            ctx.setVariable("codigo", postulante.getPaisColegio() != null ? helper.showCodigoPais(postulante.getPaisColegio()) : "");

            String htmlContent = springHtml.process("admision/postulante/modalidad/" + tipo.toLowerCase(), ctx);

            ds.setModalidad(modalidad);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

            response.setData(htmlContent);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }

    }

    @RequestMapping("change")
    public String change(Model model, HttpServletRequest request, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        CicloPostula cicloPostula = ds.getCicloPostula();
        Postulante postulanteSess = ds.getPostulante();
        Postulante postulante = service.findPostulante(postulanteSess);

        model.addAttribute("ciclo", cicloPostula);
        model.addAttribute("postulante", postulante);

        return "admision/postulante/modalidad/cambiarmodalidad";
    }

    @RequestMapping("accept")
    public String accept(Model model, HttpServletRequest request, @RequestParam("motivo") String motivo, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Postulante postulanteSess = ds.getPostulante();
        postulanteSess.setMotivoCambio(motivo);
        service.aceptarCambioModalidad(postulanteSess, session);
        return "redirect:/inscripcion/postulante/1/paso";
    }
}
