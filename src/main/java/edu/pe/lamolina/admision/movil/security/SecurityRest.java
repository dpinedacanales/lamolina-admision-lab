package edu.pe.lamolina.admision.movil.security;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.movil.complemento.ComplementoRestService;
import edu.pe.lamolina.admision.movil.evaluacion.EvaluacionRestService;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;

@Slf4j
@RestController
@RequestMapping("movil/security")
public class SecurityRest {

    @Autowired
    SecurityRestService service;

    @Autowired
    EvaluacionRestService evaluacionRestService;

    @Autowired
    ComplementoRestService complementoRestService;

    @RequestMapping(method = RequestMethod.POST, value = "login")
    public ObjectNode login(@RequestBody User user) {
        log.info("Autenticación desde Móvil");

        DataSessionAdmision ds = service.login(user);
        return this.getJsonData(ds);
    }

    @RequestMapping(method = RequestMethod.POST, value = "registro")
    public ObjectNode registro(@RequestBody ObjectNode payload) {

        log.info("Registro desde Móvil");
        DataSessionAdmision ds = service.register(payload);
        return this.getJsonData(ds);
    }

    private ObjectNode getJsonData(DataSessionAdmision ds) {

        ObjectNode data = JsonHelper.createJson(ds, JsonNodeFactory.instance, new String[]{
            "interesado.*",
            "postulante.id",
            "postulante.estado",
            "postulante.estadoEnum",
            "postulante.persona.*",
            "postulante.persona.tipoDocumento.*",
            "cicloPostula.cicloAcademico.*"

        });

        if (ds.getPostulante() != null && ds.getInteresado() != null) {
            ObjectNode permisos = service.findPermisos(ds.getInteresado(), ds.getPostulante());
            data.set("permisos", permisos);
        }

        if (ds.getCicloPostula() != null) {
            EventoCiclo ev = service.findEvento(ds.getCicloPostula(), EventoEnum.EXAM);

            data.put("fechaExamenUnix", ev.getFechaInicio().toInstant().toEpochMilli());
            data.put("fechaExamenText", TypesUtil.getStringDate(ev.getFechaInicio(), "dd 'de' MMMM YYYY", "es"));
        }

        return data;
    }

}
