package edu.pe.lamolina.admision.controller.admision.taller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import edu.pe.lamolina.admision.zelper.misc.DateFormat;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import edu.pe.lamolina.admision.controller.website.WebsiteService;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import static pe.edu.lamolina.model.enums.TallerTitulosEnum.CARR;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.InscritoTaller;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Taller;

@Controller
@RequestMapping("taller")
public class TallerController {

    @Autowired
    TallerService service;

    @Autowired
    WebsiteService websiteService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        List<Taller> talleres = service.allTaller();
        List<Archivo> archivos = service.allArchivosTaller(talleres);

        model.addAttribute("talleres", talleres);
        model.addAttribute("dt", new DateFormat());
        model.addAttribute("archivos", archivos);

        return "admision/taller/index";
    }

    @RequestMapping("mostrar")
    public String mostrar(Long taller, Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        List<TipoDocIdentidad> documentos = service.allTiposDocIdentidad();
        List<Carrera> carreras = service.allCarreraByEstado(EstadoEnum.ACT);
        List<CarreraNueva> carrerasNuevas = service.allCarreraNuevaByEstado(EstadoEnum.ACT);

        if (ds != null) {
            if (ds.getInteresado().getId() != null) {
                InscritoTaller inscrito = service.findInscritoTallerByInteresadoTaller(ds.getInteresado(), taller);
                Interesado interesado = service.findInteresado(ds.getInteresado());

                model.addAttribute("inscrito", inscrito);
                model.addAttribute("interesado", interesado);
                model.addAttribute("documentos", documentos);
                model.addAttribute("carreras", carreras);
                model.addAttribute("carrerasNuevas", carrerasNuevas);
            }
        }

        Taller tallerInfo = service.findTaller(taller);

        boolean lleno = tallerInfo.getInscritos() >= tallerInfo.getAforo();
        model.addAttribute("lleno", lleno);
        model.addAttribute("taller", tallerInfo);
        model.addAttribute("dt", new DateFormat());

        return "admision/taller/modal";
    }

    @ResponseBody
    @RequestMapping("inscribirse")
    public JsonResponse inscribirse(Interesado interesado, Long idTaller, HttpSession session) {

        logger.debug("IDTALLER {}", idTaller);

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();

        try {
            service.saveInscritoTaller(interesado, idTaller, ds, json);
            json.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        }
        return json;
    }

    @RequestMapping("download/{tallerId}")
    public void download(@PathVariable Long tallerId, HttpServletResponse response) throws Exception {

        Taller taller = service.findTaller(tallerId);
        String fileNameRoot = taller.getBanner();

        File filex = new File(fileNameRoot);
        if (!filex.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.reset();
        response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileNameRoot.substring(fileNameRoot.lastIndexOf("/"), fileNameRoot.length()) + "\"");

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            input = new BufferedInputStream(new FileInputStream(filex), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            output = new BufferedOutputStream(response.getOutputStream(), GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            IOUtils.copy(input, output);
            response.flushBuffer();

        } finally {

            close(output);
            close(input);

        }
    }

    @ResponseBody
    @RequestMapping("topTalleres")
    public JsonResponse topTalleres(int top, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        DateFormat dt = new DateFormat();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Taller> talleres = service.allTopTalleres(top);
            for (Taller taller : talleres) {
                ObjectNode json = new ObjectNode(jsonFactory);

                String descDB = taller.getDescripcion();
                if (descDB.length() > 100) {
                    descDB = descDB.substring(0, 100);
                    int sp = descDB.lastIndexOf(" ");
                    descDB = descDB.substring(0, sp).concat(" ...");
                }

                json.put("id", taller.getId());
                json.put("descripcion", descDB);
                json.put("titulo", taller.getTituloEnum().getValue());
                json.put("estilo", taller.getTituloEnum().equals(CARR) ? "special" : "");
                json.put("carrera", taller.getTituloEnum().equals(CARR) ? taller.getCarrera().getNombre() : "");
                json.put("hora", taller.getHora());
                json.put("ubicacion", taller.getUbicacion());
                json.put("diaNum", dt.format(taller.getFecha(), "dd", "es"));
                json.put("mes", StringUtils.capitalize(dt.format(taller.getFecha(), "MMM", "es")));
                json.put("dia", StringUtils.capitalize(dt.format(taller.getFecha(), "EEEE", "es")));
                json.put("banner", taller.getBanner());
                json.put("estado", taller.getEstado());

                jsonList.add(json);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("sliders")
    public JsonResponse sliders(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            List<Archivo> sliders = service.allArchivosTallerByInstanciaEnum(InstanciaEnum.TALLERSLIDER);
            for (Archivo slider : sliders) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", slider.getId());
                json.put("ruta", slider.getRuta());

                jsonList.add(json);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("gallery")
    public JsonResponse gallery(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            List<Archivo> gallery = service.allArchivosTallerByInstanciaEnum(InstanciaEnum.TALLER);
            for (Archivo gal : gallery) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", gal.getId());
                json.put("ruta", gal.getRuta());

                jsonList.add(json);
            }
            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
