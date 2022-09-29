package edu.pe.lamolina.admision.movil.resultados;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.inscripcion.Evaluado;

import java.util.List;
import pe.albatross.zelpers.miscelanea.PhobosException;

@RestController
@RequestMapping("movil/resultados")
public class ResultadosRest {

    @Autowired
    ResultadosRestService service;

    @RequestMapping(method = RequestMethod.GET, value = "byCarrera")
    public ArrayNode allByCarrera(@RequestParam Long idCarrera) {
        List<Evaluado> evaluados = service.allByCarrera(idCarrera);
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Evaluado evaluado : evaluados) {
            array.add(JsonHelper.createJson(evaluado, JsonNodeFactory.instance));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "byPostulante")
    public ObjectNode findByPostulante(@RequestParam Long idPostulante) {
        Evaluado evaluado = service.findByPostulante(idPostulante);
        return JsonHelper.createJson(evaluado, JsonNodeFactory.instance, new String[]{
            "*",
            "postulante.ingresante",
            "postulante.persona.avatar",
            "postulante.persona.rutaFotoPostulante",
            "postulante.persona.nombrePaterno",
            "postulante.modalidadIngreso.nombre",
            "carreraIngreso.nombre"
        });
    }

    @RequestMapping(method = RequestMethod.GET, value = "byDNI")
    public ObjectNode findByDNI(@RequestParam String dni) {
        Evaluado evaluado = service.findByDNI(dni);

        if (evaluado == null) {
            throw new PhobosException("Postulante No Encontrado");
        }

        return JsonHelper.createJson(evaluado, JsonNodeFactory.instance);
    }

}
