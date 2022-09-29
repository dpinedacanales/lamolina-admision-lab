package edu.pe.lamolina.admision.movil.inscripcion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.helger.commons.io.stream.StreamHelper.close;
import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.controller.admision.inscripcion.guiapostulante.GuiaPostulanteService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import edu.pe.lamolina.admision.movil.security.SecurityRestService;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PRE;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PROS;
import pe.edu.lamolina.model.enums.TipoProspectoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

@RestController
@RequestMapping("movil/inscripcion")
public class InscripcionRest {

    @Autowired
    GuiaPostulanteService guiaPostulanteService;

    @Autowired
    InscripcionRestService restService;

    @Autowired
    PostulanteService postulateService;

    @Autowired
    SecurityRestService securityRestService;

    @Autowired
    StorageService storageService;

    @Autowired
    DespliegueConfig despliegue;

    @RequestMapping(method = RequestMethod.GET, value = "iniciarInscripcion")
    public ObjectNode iniciarInscripcion(@RequestParam Long idInteresado) {

        Interesado interesado = restService.findInteresado(idInteresado);

        Postulante postulante = restService.findPostulanteActivoByInteresado(interesado);

        if (postulante != null) {
            if (postulante.getEstadoEnum() == PRE) {
                throw new PhobosException("Interesado ya inscrito");
            }
        }

        if (restService.esFinalInscripciones()) {
            throw new PhobosException("Las isncripciones han terminado");
        }

        if (postulante == null) {
            postulante = new Postulante();
        }

        postulante.setInteresado(interesado);

        Persona persona = null;

        if (postulante.getPersona() != null
                && !postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
            persona = restService.getPersona(postulante.getPersona());
            postulante.setPersona(persona);
        }

        if (postulante.getPersona() != null
                && postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
            postulante.setPersona(null);
        }

        if (persona == null) {
            restService.completarPostulante(postulante, interesado);
        }

        {
            persona = postulante.getPersona();
            persona.setNumeroDocIdentidad(interesado.getNumeroDocIdentidad());
            persona.setTipoDocumento(interesado.getTipoDocumento());
        }

        return JsonHelper.createJson(postulante, JsonNodeFactory.instance, new String[]{
            "id",
            "metalesPostulante.*",
            "modalidadIngresoCiclo.id",
            "interesado.id",
            "modalidadSimulacion.*",
            "paisColegio.*",
            "colegioProcedencia.*",
            "paisUniversidad.*",
            "universidadProcedencia.*",
            "persona.*",
            "persona.tipoDocumento.*",
            "persona.nacionalidad.*",
            "persona.paisNacer.*",
            "persona.ubicacionNacer.*",
            "persona.paisDomicilio.*",
            "persona.ubicacionDomicilio.*",
            "gradoSecundaria.*"
        });

    }

    @RequestMapping(method = RequestMethod.GET, value = "iniciarGuia")
    public ObjectNode iniciaGuia(@RequestParam Long idInteresado) {

        Interesado interesado = restService.findInteresado(idInteresado);

        Postulante postulante = restService.findPostulanteActivoByInteresado(interesado);

        if (postulante != null) {
            if (postulante.getEstadoEnum() == PRE) {
                throw new PhobosException("Interesado ya inscrito");
            }
        }

        if (restService.esFinalInscripciones()) {
            throw new PhobosException("Las isncripciones han terminado");
        }

        if (postulante == null) {
            postulante = new Postulante();
        }

        postulante.setInteresado(interesado);

        Persona persona = null;

        if (postulante.getPersona() != null && !postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
            persona = restService.getPersona(postulante.getPersona());
            postulante.setPersona(persona);
        }

        if (postulante.getPersona() != null && postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
            postulante.setPersona(null);
        }

        if (persona == null) {
            restService.completarPostulante(postulante, interesado);
        }

        {
            persona = postulante.getPersona();
            persona.setNumeroDocIdentidad(interesado.getNumeroDocIdentidad());
            persona.setTipoDocumento(interesado.getTipoDocumento());
        }

        return JsonHelper.createJson(postulante, JsonNodeFactory.instance, new String[]{
            "interesado.id",
            "persona.*",
            "persona.tipoDocumento.*"
        });
    }

    @RequestMapping(method = RequestMethod.POST, value = "opcionesInscripcion")
    public ObjectNode opcionesInscripcion(@RequestBody Postulante postulanteForm) {
        return restService.opcionesInscripcionByPostulante(postulanteForm);
    }

    @RequestMapping(method = RequestMethod.POST, value = "verifyNumeroDocumento")
    public JsonResponse verifyNumeroDocumento(@RequestBody Postulante postulanteForm) {

        JsonResponse response = new JsonResponse();

        try {
            restService.verifyNumeroDocumento(postulanteForm);
            response.setSuccess(true);
            response.setData(JsonHelper.createJson(postulanteForm, JsonNodeFactory.instance, new String[]{"*", "persona.paterno",
                "persona.materno",
                "persona.nombres",
                "persona.tipoDocumento.*"}));

        } catch (PhobosException e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
        }

        return response;

    }

    @RequestMapping(method = RequestMethod.POST, value = "saveGuiaPostulante")
    public ObjectNode saveGuiaPosutulante(@RequestBody Postulante postulanteForm) {
        postulanteForm.setTipoProspecto(TipoProspectoEnum.regular.name());
        Postulante postulante = guiaPostulanteService.savePostulante(postulanteForm, postulanteForm.getInteresado(), null);
        return this.getPostulanteJson(postulante);
    }

    @RequestMapping(method = RequestMethod.POST, value = "savePostulante")
    public ObjectNode savePostulante(@RequestBody Postulante postulanteForm) {

        Postulante postulante = restService.savePostulante(postulanteForm);
        return this.getPostulanteJson(postulante);

    }

    @RequestMapping(method = RequestMethod.GET, value = "findPostulante/{id}")
    public ObjectNode findPostulante(@PathVariable Long id) {
        Postulante postulante = restService.findPostulante(new Postulante(id));

        return this.getPostulanteJson(postulante);
    }

    private ObjectNode getPostulanteJson(Postulante postulante) {

        ObjectNode node = JsonHelper.createJson(postulante, JsonNodeFactory.instance, new String[]{
            "id",
            "estadoEnum",
            "persona.codigoPago",
            "persona.nombrePaterno",
            "persona.avatar",
            "opcionCarrera.id",
            "opcionCarrera.prioridad",
            "opcionCarrera.carreraPostula.id",
            "opcionCarrera.carreraPostula.carrera.id",
            "opcionCarrera.carreraPostula.carrera.nombre",
            "modalidadIngreso.id",
            "modalidadIngreso.nombreInscripcion",
            "cicloPostula.*"
        });

        if (postulante.getEstadoEnum() != PostulanteEstadoEnum.CRE) {
            if (restService.mostrarUbicacion()) {
                node.put("ubicacion", String.format("%s - Aula %s", (String) ObjectUtil.getParentTree(postulante, "aulaExamen.pabellonExamen.pabellon.nombre"), postulante.getAula()));
            }
            node.put("guiaPostulantePagada", postulante.getEstadoEnum() == PostulanteEstadoEnum.PROS || postulateService.verificarPagoGuiaPostulante(postulante));
        }

        ObjectNode permisos = securityRestService.findPermisos(postulante.getInteresado(), postulante);
        node.set("permisos", permisos);

        return node;
    }

    @RequestMapping(method = RequestMethod.GET, value = "allCarrerasByModalidad")
    public ArrayNode allCarrerasByModalidad(@RequestParam Long idModalidad) {
        ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
        
        List<CarreraPostula> carreraPostulas = restService.allCarreraPostulaByModalidad(new ModalidadIngreso(idModalidad));
        
        for (CarreraPostula cp : carreraPostulas) {
            ObjectNode item = new ObjectNode(JsonNodeFactory.instance);
            item.put("id", cp.getId());
            item.put("nombre", cp.getCarrera().getNombre());
            arr.add(item);
        }
        return arr;
    }

    @RequestMapping(method = RequestMethod.GET, value = "allModalidades")
    public ArrayNode allModalidades() {

        ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
        List<ModalidadIngresoCiclo> list = restService.allModalidad();
        for (ModalidadIngresoCiclo item : list) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                "*",
                "modalidadIngreso.*"
            });

            node.put("nombre", item.getModalidadIngreso().getNombreInscripcion());

            arr.add(node);

        }
        return arr;
    }

    @RequestMapping(method = RequestMethod.GET, value = "allModalidadesParticipanteLibre")
    public ArrayNode allModalidadesParticipanteLibre() {
        ArrayNode arr = new ArrayNode(JsonNodeFactory.instance);
        List<ModalidadIngreso> list = restService.allModalidadParticipanteLibre();
        for (ModalidadIngreso item : list) {
            ObjectNode node = JsonHelper.createJson(item, JsonNodeFactory.instance);
            arr.add(node);
        }
        return arr;
    }

    @RequestMapping(method = RequestMethod.GET, value = "findModalidadCepre")
    public ObjectNode findModalidadCepre() {

        ModalidadIngreso modalidadCepre = restService.findModalidadCepre();

        return JsonHelper.createJson(modalidadCepre, JsonNodeFactory.instance, new String[]{
            "*",
            "modalidadIngresoCicloActual.*"
        });

    }

    @RequestMapping(method = RequestMethod.GET, value = "findTerminos")
    public ObjectNode findTerminos() {
        ContenidoCarta carta = postulateService.findContenidoCartaByCodigoEnum(ContenidoCartaEnum.TERMS);
        return JsonHelper.createJson(carta, JsonNodeFactory.instance, new String[]{"*"});
    }

    @RequestMapping("findCarta/{idPostulante}/{idModalidad}")
    public JsonResponse findCarta(@PathVariable String idPostulante, @PathVariable Long idModalidad) {

        Postulante postulante = postulateService.findPostulante(new Postulante(idPostulante));
        ModalidadIngresoCiclo modalidadIC = restService.findModalidad(idModalidad);

        JsonResponse response = new JsonResponse();
        response.setData(postulateService.getContenidoCartaTerms(postulante, modalidadIC));
        response.setSuccess(true);

        return response;

    }

    @RequestMapping(method = RequestMethod.GET, value = "descargarGuiaPostulante")
    public ResponseEntity<Resource> descargarGuiaPostulanteByPostulante(@RequestParam Long idPostulante) {

        Postulante postulanteBD = postulateService.findPostulante(new Postulante(idPostulante));

        boolean pagoGuiaPostulante = postulanteBD.getEstadoEnum() == PROS || postulateService.verificarPagoGuiaPostulante(postulanteBD);

        if (!pagoGuiaPostulante) {
            throw new PhobosException("Debe realizar el pago de la guia de postulante");
        }

        CicloPostula ciclo = postulanteBD.getCicloPostula();

        if (StringUtils.isEmpty(ciclo.getRutaProspecto())) {
            throw new PhobosException("Error, la guía del postulante aun no existe");
        }

        String codeCiclo = ciclo.getCicloAcademico().getCodigo();

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            String rutaGuiaLocal = GlobalConstantine.TMP_DIR + codeCiclo + ".prospecto.pdf";
            InputStream fileStreamLocal;

            File fileGuia = new File(rutaGuiaLocal);
            if (!fileGuia.exists() || !fileGuia.isDirectory()) {

                InputStream fileStreamS3 = storageService.getFile(AdmisionConstantine.S3_BUCKET_ADMISION, GlobalConstantine.S3_TRASH, ciclo.getRutaProspecto());
                
                if (despliegue.isProduccion()) {
                    fileStreamS3 = storageService.getFile(AdmisionConstantine.S3_BUCKET_ADMISION, AdmisionConstantine.S3_PROSPECTO_DIR, ciclo.getRutaProspecto());
                }

                OutputStream outputStream = new FileOutputStream(new File(rutaGuiaLocal));
                int read = 0;
                byte[] bytes = new byte[1024];

                while ((read = fileStreamS3.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }

                fileGuia = new File(rutaGuiaLocal);
                fileStreamLocal = new FileInputStream(fileGuia);

            }

            Resource resource = new UrlResource("file", rutaGuiaLocal);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"guiapostulante" + codeCiclo + "-" + postulanteBD.getCodigo() + ".pdf\"")
                    .body(resource);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(InscripcionRest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InscripcionRest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            close(output);
            close(input);
        }

        return null;

    }

}
