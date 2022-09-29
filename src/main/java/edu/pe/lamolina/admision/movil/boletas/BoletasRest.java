package edu.pe.lamolina.admision.movil.boletas;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;

import java.util.List;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

@RestController
@RequestMapping("movil/boletas")
public class BoletasRest {

    private static final Logger logger = LoggerFactory.getLogger(BoletasRest.class);

    @Autowired
    BoletasRestService service;
    @Autowired
    PostulanteService postulanteService;

    @RequestMapping(method = RequestMethod.GET, value = "byPostulante")
    public ObjectNode byPostulante(@RequestParam Long idPostulante) {
        List<DeudaInteresado> deudas = service.allByPostulante(idPostulante);
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

        Postulante postulante = null;
        for (DeudaInteresado deuda : deudas) {
            ObjectNode json = JsonHelper.createJson(deuda, JsonNodeFactory.instance, new String[]{
                "*",
                "conceptoPrecio.*",
                "conceptoPrecio.conceptoPago.*",
                "cuentaBancaria.*"
            });

            if (!CollectionUtils.isEmpty(deuda.getItemDeudaInteresado())) {

                ArrayNode items = new ArrayNode(JsonNodeFactory.instance);
                json.set("items", items);

                for (ItemDeudaInteresado itemDeuda : deuda.getItemDeudaInteresado()) {
                    items.add(JsonHelper.createJson(itemDeuda, JsonNodeFactory.instance, new String[]{
                        "*",
                        "conceptoPrecio.*",
                        "conceptoPrecio.conceptoPago.*"
                    }));
                }
            }
            postulante = deuda.getPostulante();
            array.add(json);
        }
        ContenidoCarta header = postulanteService.findHeaderBoletaWeb(postulante, postulante.getModalidadIngresoCiclo());
        ContenidoCarta footer = postulanteService.findFooterBoletaWeb(postulante, postulante.getModalidadIngresoCiclo());
        obj.put("header", header.getContenido());
        obj.put("footer", footer.getContenido());
        obj.set("deuda", array);
        return obj;
    }

}
