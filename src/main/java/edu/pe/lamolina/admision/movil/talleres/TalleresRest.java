package edu.pe.lamolina.admision.movil.talleres;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.enums.TallerTitulosEnum;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Taller;

@RestController
@RequestMapping("movil/talleres")
public class TalleresRest {

    @Autowired
    TalleresRestService service;

    @RequestMapping(method = RequestMethod.GET)
    public ArrayNode all() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<Taller> talleres = service.all();
        for (Taller taller : talleres) {
            ObjectNode json = JsonHelper.createJson(taller, JsonNodeFactory.instance);
            this.addDataToTaller(taller, json);
            array.add(json);
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "byInteresado")
    public ArrayNode byInteresado(@RequestParam Long idInteresado) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<Taller> talleres = service.allByInteresado(new Interesado(idInteresado));
        for (Taller taller : talleres) {
            ObjectNode json = JsonHelper.createJson(taller, JsonNodeFactory.instance);
            this.addDataToTaller(taller, json);
            array.add(json);
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "find")
    public ObjectNode find(@RequestParam Long idInteresado, @RequestParam Long idTaller) {
        Taller taller = service.find(new Interesado(idInteresado), new Taller(idTaller));
        ObjectNode json = JsonHelper.createJson(taller, JsonNodeFactory.instance);

        this.addDataToTaller(taller, json);
        return json;
    }

    @RequestMapping(method = RequestMethod.POST, value = "inscribirse")
    public JsonResponse inscribirse(@RequestBody ObjectNode payload) {
        Long idInteresado = payload.get("idInteresado").asLong();
        Long idTaller = payload.get("idTaller").asLong();

        JsonResponse json = new JsonResponse();
        service.inscribirse(new Interesado(idInteresado), new Taller(idTaller), json);
        json.setSuccess(true);

        return json;
    }

    private void addDataToTaller(Taller taller, ObjectNode json) {

        json.put("campus", "Campus Universitario");
        json.put("titulo", "Un día en la Agraria");

        if (taller.getTituloEnum() == TallerTitulosEnum.TACA) {
            json.put("titulo", "Taller de Carreras");
        }

        if (taller.getCarrera() != null) {
            json.put("carrera", taller.getCarrera().getNombre());
        }
    }

}
