package edu.pe.lamolina.admision.movil.complemento;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.movil.model.EventoCalendarioMovilDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;

@RestController
@RequestMapping("movil")
public class ComplementoRest {

    @Autowired
    ComplementoRestService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET, value = "facultades")
    public ArrayNode facultades() {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        List<Facultad> facultades = service.allFacultades();
        for (Facultad facultad : facultades) {
            array.add(JsonHelper.createJson(facultad, JsonNodeFactory.instance, new String[]{
                "id",
                "nombre",
                "simbolo"
            }));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "calendario/{idInteresado}")
    public List<EventoCalendarioMovilDTO> actividades(@PathVariable Long idInteresado) {

        return service.allCalendarioItem(new Interesado(idInteresado));
    }

    @RequestMapping(method = RequestMethod.GET, value = "proceso")
    public ObjectNode proceso() {
        CicloPostula cp = service.findCicloPostulaActivo();

        ObjectNode node = JsonHelper.createJson(cp, JsonNodeFactory.instance, new String[]{
            "id",
            "cicloAcademico.id",
            "cicloAcademico.descripcion",
            "cicloAcademico.year"
        });

        return node;
    }

    @RequestMapping(method = RequestMethod.GET, value = "tipoDocIdentidad")
    public ArrayNode tipoDocIdentidad() {
        List<TipoDocIdentidad> list = service.allTipoDocIdentidad();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (TipoDocIdentidad tipoDocIdentidad : list) {
            array.add(JsonHelper.createJson(tipoDocIdentidad, JsonNodeFactory.instance));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "paises")
    public ArrayNode paises() {
        List<Pais> list = service.allPaises();
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Pais item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{"*"}));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "distritos")
    public ArrayNode distritos() {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;

        List<Ubicacion> ubicaciones = service.allDistritos();
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

        return jsonList;
    }

    @RequestMapping("ubicacion/{tipo}/{parent}")
    public ArrayNode allUbigeo(@PathVariable String tipo, @PathVariable Long parent) {

        List<Ubicacion> items = service.allUbicacion(tipo, parent);

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Ubicacion item : items) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "id",
                "nombre",
                "tipoUbicacion.simbolo",
                "distrito"
            }));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "colegiosByUbicacion")
    public ArrayNode colegiosByUbicacion(@RequestParam Long idUbicacion) {

        List<Colegio> list = service.allColegiosByUbicacion(new Ubicacion(idUbicacion));

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Colegio item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "id",
                "nombre"
            }));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "colegiosCOAR")
    public ArrayNode colegiosCOAR() {

        List<Colegio> list = service.allColegiosCOAR();

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Colegio item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "id",
                "nombre",
                "ubicacion.id",
                "ubicacion.distrito"
            }));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "universidades")
    public ArrayNode universidades() {

        List<Universidad> list = service.allUniversidadesPeru();

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Universidad item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "id",
                "nombre"
            }));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "eventos")
    public ObjectNode eventos() {
        return service.eventos();
    }

    @RequestMapping(method = RequestMethod.GET, value = "gradoSecundaria")
    public ArrayNode gradoSecundaria() {

        List<GradoSecundaria> list = service.allGradoSecundaria();

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (GradoSecundaria item : list) {
            array.add(JsonHelper.createJson(item, JsonNodeFactory.instance));
        }
        return array;
    }

    @RequestMapping(method = RequestMethod.GET, value = "carrerasNuevas")
    public ArrayNode carrerasNuevas() {
        
        List<CarreraNueva> carreras = service.allCarreraNueva();
        return JaneHelper.from(carreras).array();
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "carrerasInteres")
    public ArrayNode carrerasInteres() {
        
        List<Carrera> carreras = service.allCarreraActiva();
        return JaneHelper.from(carreras).array();
    }
}
