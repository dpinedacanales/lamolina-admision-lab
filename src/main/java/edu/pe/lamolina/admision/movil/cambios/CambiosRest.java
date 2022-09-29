package edu.pe.lamolina.admision.movil.cambios;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

@RestController
@RequestMapping("movil/cambios")
public class CambiosRest {

    @Autowired
    CambiosRestService service;

    @RequestMapping(value = "info", method = RequestMethod.GET)
    public ObjectNode info(@RequestParam Long idPostulante) {
        ObjectNode response = new ObjectNode(JsonNodeFactory.instance);

        Postulante postulante = service.findPostulante(idPostulante);

        ObjectNode postilanteJson = JsonHelper.createJson(postulante, JsonNodeFactory.instance,
                new String[]{
                    "id",
                    "persona.*",
                    "persona.tipoDocumento.id",
                    "persona.tipoDocumento.nombre",
                    "persona.numeroDocIdentidad",
                    "persona.paisNacer.id",
                    "persona.paisNacer.nombre",
                    "persona.paisNacer.codigo",
                    "persona.ubicacionNacer.id",
                    "persona.ubicacionNacer.distrito",
                    "persona.fechaNacer",
                    "persona.nacionalidad.id",
                    "persona.nacionalidad.nombre",
                    "persona.paisDomicilio.id",
                    "persona.paisDomicilio.nombre",
                    "persona.paisDomicilio.codigo",
                    "persona.ubicacionDomicilio.id",
                    "persona.ubicacionDomicilio.distrito",
                    "yearEgresoColegio",
                    "paisColegio.id",
                    "paisColegio.nombre",
                    "paisUniversidad.id",
                    "paisUniversidad.nombre",
                    "importeDescuento",
                    "tipoInstitucion",
                    "modalidadIngreso.id",
                    "modalidadIngreso.nombre",
                    "modalidadIngreso.tipo",
                    "descuentoExamen.id",
                    "descuentoExamen.estado",
                    "descuentoExamen.gestionColegio",
                    "descuentoExamen.porcentaje",
                    "modalidadIngresoCiclo.*",
                    "modalidadIngresoCiclo.modalidadIngreso.id",
                    "modalidadIngresoCiclo.modalidadIngreso.nombreInscripcion",
                    "modalidadIngresoCiclo.modalidadIngreso.tipo",
                    "colegioProcedencia.id",
                    "colegioProcedencia.nombre",
                    "colegioProcedencia.esCoar",
                    "colegioExtranjero",
                    "universidadProcedencia.id",
                    "universidadExtranjera",
                    "cicloPostula.id",
                    "opcionCarrera.id",
                    "opcionCarrera.prioridad",
                    "opcionCarrera.carreraPostula.id",
                    "opcionCarrera.carreraPostula.carrera.id",
                    "opcionCarrera.carreraPostula.carrera.nombre"

                });

        List<SolicitudCambioInfo> solicitudes = service.allSolicitudesByPostulante(postulante);

        ArrayNode solicitudesJson = new ArrayNode(JsonNodeFactory.instance);
        ArrayNode cambiosJson = new ArrayNode(JsonNodeFactory.instance);

        if (solicitudes.isEmpty()) {
            List<ConceptoPrecio> cambios = service.allConceptoPrecioCambiosByCiclo(postulante);

            cambiosJson = JaneHelper.from(cambios)
                    .only("id, monto, estado, esAntesExamen")
                    .join("conceptoPago" )
                    .join("conceptoPago.cuentaBancaria" )
                    .join("tipoCambioInfo")
                    .array();
        }

        solicitudesJson = JaneHelper.from(solicitudes)
                .join("tipoCambioInfo")
                .array();

        response.set("postulante", postilanteJson);
        response.set("solicitudesActivas", solicitudesJson);
        response.set("cambiosDisponibles", cambiosJson);

        return response;
    }

    @RequestMapping("modalidades")
    public ArrayNode modalidades(@RequestParam Long idPostulante) {

        List<ModalidadIngreso> modalidades = service.allModalidadesByCicloActual();
        Postulante postu = service.findPostulante(idPostulante);
        checkModalidadesPostulante(modalidades, postu);

        List<ModalidadIngresoCiclo> modalidadesIng = service.allModalidadIngresoCicloDisponible(modalidades);
        Map<Long, ModalidadIngresoCiclo> mapModalidades = TypesUtil.convertListToMap("modalidadIngreso.id", modalidadesIng);
        mapModalidades.remove(postu.getModalidadIngreso().getId());

        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (ModalidadIngresoCiclo moda : mapModalidades.values()) {
            ObjectNode node = JsonHelper.createJson(moda, JsonNodeFactory.instance, true,
                    new String[]{
                        "id",
                        "requiereColegioUniversidad",
                        "requiereSoloColegio",
                        "requiereSoloUniversidad",
                        "soloColegioPeruano",
                        "soloColegioExtranjero",
                        "soloUniversidadPeruana",
                        "soloUniversidadExtranjera",
                        "opciones",
                        "requiereColegio",
                        "requiereUniversidad",
                        "verColegiosExtranjeros",
                        "modalidadIngreso.id",
                        "modalidadIngreso.codigo",
                        "modalidadIngreso.participanteLibre",
                        "modalidadIngreso.quintoSecundaria",
                        "modalidadIngreso.primerosPuestos",
                        "modalidadIngreso.graduadosTituladosUniversitarios",
                        "modalidadIngreso.preLaMolina",
                        "modalidadIngreso.nombre",
                        "modalidadIngreso.nombreCorto",
                        "modalidadIngreso.nombreInscripcion",
                        "modalidadIngreso.tipo",
                        "conceptoPrecio.id",
                        "conceptoPrecio.monto",
                        "conceptoPrecio.esAntesExamen",
                        "conceptoPrecio.conceptoPago.id",
                        "conceptoPrecio.conceptoPago.tipo",
                        "conceptoPrecio.conceptoPago.estadoEnum",
                        "conceptoPrecio.conceptoPago.tipoEnum",
                        "conceptoPrecio.conceptoPago.descripcion"
                    });
            array.add(node);
        }

        return array;

    }

    private void checkModalidadesPostulante(List<ModalidadIngreso> modalidades, Postulante postu) {
        if (!postu.getModalidadIngreso().isPreLaMolina()) {
            List<ModalidadIngreso> pres = new ArrayList();
            for (ModalidadIngreso moda : modalidades) {
                if (moda.isPreLaMolina()) {
                    pres.add(moda);
                }
            }
            for (ModalidadIngreso pre : pres) {
                modalidades.remove(pre);
            }
        }
    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse anularSolicitud(Long idPostulante) {

        JsonResponse response = new JsonResponse();

        try {
            service.anularCambios(idPostulante);

            response.setMessage("Los cambios fueron anulados");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping(value = "saveCambioDocumento", method = RequestMethod.POST)
    public ObjectNode saveCambioDocumento(Long idPostulante, Long idTipoDoc, String numeroDocumento) {
        service.saveCambioDocumento(idPostulante, idTipoDoc, numeroDocumento);
        return new ObjectNode(JsonNodeFactory.instance);
    }

    @ResponseBody
    @RequestMapping(value = "saveSolicitud", method = RequestMethod.POST)
    public ObjectNode saveSolicitud(@RequestBody List<ConceptoPrecio> conceptos, @RequestParam Long idPostulante) {
        
        service.saveCambioSolicitud(idPostulante, conceptos);
        return new ObjectNode(JsonNodeFactory.instance);
    }

    @RequestMapping(value = "saveDatosPersonales", method = RequestMethod.POST)
    public ObjectNode saveCambioDatosPersonales(@RequestBody Postulante postulante) {
        
        service.saveCambioDatosPersonales(postulante);
        return new ObjectNode(JsonNodeFactory.instance);

    }
}
