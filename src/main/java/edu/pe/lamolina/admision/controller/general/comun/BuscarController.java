package edu.pe.lamolina.admision.controller.general.comun;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

@Controller
@RequestMapping("comun/buscar")
public class BuscarController {

    @Autowired
    BuscarService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @RequestMapping("allDistritos")
    public JsonResponse allDistritos(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Ubicacion> ubicaciones = service.allDistritosByName(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Ubicacion ubicacion : ubicaciones) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", ubicacion.getId());
                Ubicacion provincia = ubicacion.getUbicacionSuperior();
                Ubicacion departamento = provincia.getUbicacionSuperior();

                json.put("distrito", ubicacion.getNombre());
                json.put("provincia", provincia.getNombre());
                json.put("departamento", departamento.getNombre());
                json.put("nombre", ubicacion.getDistrito());

                jsonList.add(json);

            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allPaises")
    public JsonResponse allPaises(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Pais> paises = service.allPaisesByName(nombre);
            for (Pais pais : paises) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", pais.getId());
                json.put("nombre", pais.getNombre());
                json.put("codigo", pais.getCodigo());

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
    @RequestMapping("allPaisesColegio")
    public JsonResponse allPaisesColegio(@RequestParam("nombre") String nombre, @RequestParam(value = "idModalidad", required = false) Long modalidad, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Pais> paises = service.allPaisesByNameModalidad(nombre, ds, new ModalidadIngreso(modalidad));

            for (Pais pais : paises) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", pais.getId());
                json.put("nombre", pais.getNombre());
                json.put("codigo", pais.getCodigo());

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
    @RequestMapping("colegioProcedencia")
    public JsonResponse colegioProcedencia(
            @RequestParam("nombre") String nombre,
            @RequestParam("distrito") Long idDistrito,
            @RequestParam("modalidad") Long idModalidad, HttpSession session) {

        logger.debug("nombre {}", nombre);
        logger.debug("idDistrito {}", idDistrito);
        logger.debug("idModalidad {}", idModalidad);

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Colegio> colegios = service.allColegioSecundariaByName(nombre, idDistrito, idModalidad);

            for (Colegio colegio : colegios) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", colegio.getId());
                json.put("nombre", colegio.getNombre());
                json.put("codigoModular", colegio.getCodigoModular());
                json.put("anexo", colegio.getAnexo());
                json.put("codigoLocal", colegio.getCodigoLocal());
                json.put("direccion", colegio.getDireccion());
                json.put("referencia", colegio.getReferencia());
                json.put("localidad", colegio.getLocalidad());
                json.put("centroPoblado", colegio.getCentroPoblado());
                json.put("caracteristica", colegio.getCaracteristica().getNombre());
                json.put("formaAtencion", colegio.getFormaAtencion().getNombre());
                json.put("gestion", colegio.getGestion().getNombre());
                json.put("gestionDependencia", colegio.getGestionDependencia().getNombre());
                json.put("nivelModalidad", colegio.getNivelModalidad().getNombre());
                json.put("ubicacion", colegio.getUbicacion().getDistrito());
                json.put("idUbicacion", colegio.getUbicacion().getId());
                json.put("nombreLargo", colegio.getNombreLargo());

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
    @RequestMapping("colegiosCOAR")
    public JsonResponse colegiosCOAR(HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<Colegio> colegios = service.allColegiosCoar();

            for (Colegio colegio : colegios) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", colegio.getId());
                json.put("nombre", colegio.getNombre());
                json.put("codigoModular", colegio.getCodigoModular());
                json.put("anexo", colegio.getAnexo());
                json.put("codigoLocal", colegio.getCodigoLocal());
                json.put("direccion", colegio.getDireccion());
                json.put("referencia", colegio.getReferencia());
                json.put("localidad", colegio.getLocalidad());
                json.put("centroPoblado", colegio.getCentroPoblado());
                json.put("caracteristica", colegio.getCaracteristica().getNombre());
                json.put("formaAtencion", colegio.getFormaAtencion().getNombre());
                json.put("gestion", colegio.getGestion().getNombre());
                json.put("gestionDependencia", colegio.getGestionDependencia().getNombre());
                json.put("nivelModalidad", colegio.getNivelModalidad().getNombre());
                json.put("ubicacion", colegio.getUbicacion().getDistrito());
                json.put("idUbicacion", colegio.getUbicacion().getId());
                json.put("nombreLargo", colegio.getNombreLargo());

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
    @RequestMapping("modalidadIngreso")
    public JsonResponse modalidad(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<ModalidadIngreso> modalidades = service.allModalidadIngresoByName(nombre);

            for (ModalidadIngreso modalidad : modalidades) {
                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", modalidad.getId());
                json.put("nombre", modalidad.getNombre());
                json.put("codigo", modalidad.getCodigo());

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
    @RequestMapping("buscarDocumentoIdentidad")
    public JsonResponse validarDocumentoIdentidad(@RequestParam("id") Long id, HttpSession session) {
        JsonResponse response = new JsonResponse();

        try {

            TipoDocIdentidad ti = service.findById(id);
            response.setData(JsonHelper.createJson(ti, JsonNodeFactory.instance, true, new String[]{"*"}));
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
