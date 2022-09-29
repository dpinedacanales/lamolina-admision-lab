package edu.pe.lamolina.admision.controller.admision.inscripcion.numeroidentidad;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.InscripcionService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
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
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Controller
@RequestMapping("inscripcion/numeroidentidad")
public class NumeroIdentidadController {

    @Autowired
    NumeroIdentidadService service;

    @Autowired
    InscripcionService inscripcionService;

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

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        CicloPostula cicloPostula = inscripcionService.findCicloPostula(ds.getCicloPostula());
        Postulante postulanteSession = ds.getPostulante();
        Postulante postulante = service.findPostulante(postulanteSession);
        List<TipoDocIdentidad> tiposDocIdentidad = service.allTiposDocIdentidad();
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        for (TipoDocIdentidad tipoDoc : tiposDocIdentidad) {
            arrayNode.add(JsonHelper.createJson(tipoDoc, JsonNodeFactory.instance, true, new String[]{"*"}));
        }

        model.addAttribute("tiposDocIdentidad", tiposDocIdentidad);
        model.addAttribute("postulante", postulante);
        model.addAttribute("persona", postulante.getPersona());
        model.addAttribute("tiposDocJson", arrayNode.toString());
        model.addAttribute("personaJson", createPersonaJson(postulante.getPersona()).toString());
        model.addAttribute("ciclo", ds.getCicloPostula());
        model.addAttribute("esVirtual", cicloPostula.getEsVirtual());
        return "admision/postulante/numeroidentidad/numeroIdentidad";
    }

    private ObjectNode createPersonaJson(Persona persona) {
        ObjectNode node = JsonHelper.createJson(persona, JsonNodeFactory.instance, true, new String[]{
            "id", "paterno", "materno", "nombres", "nombreCompleto", "numeroDocIdentidad",
            "tipoDocumento.*", "email"
        });
        return node;
    }

    @ResponseBody
    @RequestMapping("cambiarDocumento")
    public JsonResponse cambiarDocumento(
            @RequestParam("numero") String numero,
            @RequestParam("tipoDoc") Long tipoDoc,
            @RequestParam("email") String email,
            HttpSession session) {

        logger.debug("IDTIPODOC {}", tipoDoc);
        logger.debug("numero {}", numero);

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

            service.saveCambioDni(ds.getPostulante(), tipoDoc, numero, email, ds.getCicloPostula());
            response.setMessage("Documento Actualizado");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

}
