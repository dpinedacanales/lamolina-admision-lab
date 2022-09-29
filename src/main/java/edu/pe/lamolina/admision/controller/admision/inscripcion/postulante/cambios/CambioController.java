package edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.cambios;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import edu.pe.lamolina.admision.controller.general.comun.BuscarService;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.NPOST;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Controller
@RequestMapping("inscripcion/postulante/cambios")
public class CambioController {

    @Autowired
    CambioService service;
    @Autowired
    BuscarService buscarService;
    @Autowired
    PostulanteService postulanteService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesado = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();

        Postulante postulante = service.findPostulanteActivoByInteresado(interesado);

        if (postulante == null) {
            ds.setPasoInscripcion(0);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
            return "redirect:/inscripcion/postulante/0/paso";
        }

        if (interesado.getEstadoEnum() == NPOST) {
            model.addAttribute("ciclo", ciclo);
            model.addAttribute("interesado", interesado);
            return "admision/postulante/postulante/postulantePotencial";
        }

        DeudaInteresado deudaPagada = service.findDeudaPagada(ds.getPostulante(), ds.getCicloPostula());
        Postulante postulanteDB = service.findPostulante(postulante);
        List<SolicitudCambioInfo> solicitudes = service.allSolicitudesByPostulante(ds.getPostulante());
        ObjectNode nodeDeuda = JsonHelper.createJson(deudaPagada, JsonNodeFactory.instance, true,
                new String[]{
                    "id",
                    "monto",
                    "abono",
                    "conceptoPrecio.conceptoPago.tipo"
                });

        ObjectNode nodePostulante = JsonHelper.createJson(postulanteDB, JsonNodeFactory.instance, true,
                new String[]{
                    "id",
                    "importeDescuento",
                    "tipoInstitucion",
                    "modalidadIngreso.id",
                    "descuentoExamen.id",
                    "descuentoExamen.estado",
                    "descuentoExamen.gestionColegio",
                    "descuentoExamen.porcentaje",
                    "modalidadIngresoCiclo.id",
                    "modalidadIngresoCiclo.requiereColegioUniversidad",
                    "modalidadIngresoCiclo.requiereSoloColegio",
                    "modalidadIngresoCiclo.requiereSoloUniversidad",
                    "modalidadIngresoCiclo.tieneDescuentoCoar",
                    "colegioProcedencia.id",
                    "colegioProcedencia.esCoar",
                    "colegioExtranjero",
                    "universidadProcedencia.id",
                    "universidadExtranjera",
                    "cicloPostula.id"
                });

        model.addAttribute("ciclo", ds.getCicloPostula());
        model.addAttribute("persona", postulante.getPersona());
        model.addAttribute("deudaPagada", nodeDeuda == null ? null : nodeDeuda.toString());
        model.addAttribute("postulante", nodePostulante == null ? null : nodePostulante.toString());
        model.addAttribute("solicitudes", solicitudes);

        return "admision/postulante/postulante/cambios/solicitud";
    }

    @ResponseBody
    @RequestMapping("list")
    public JsonResponse cambiosList(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

            logger.debug("Get lista de conceptos por cambios");
            List<ConceptoPrecio> cambios = service.allConceptoPrecioCambiosByCiclo(ds.getPostulante(), ds.getCicloPostula());
            logger.debug("Existn {} conceptos para este postulante", cambios.size());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ConceptoPrecio cambio : cambios) {
                logger.debug("Concepto {} con monto {}", cambio.getConceptoPago().getDescripcion(), cambio.getMonto());
                ObjectNode node = JsonHelper.createJson(cambio, JsonNodeFactory.instance, true,
                        new String[]{
                            "id",
                            "monto",
                            "conceptoPago.id",
                            "conceptoPago.descripcion",
                            "conceptoPago.cuentaBancaria.id",
                            "tipoCambioInfo.id",
                            "tipoCambioInfo.codigo",
                            "tipoCambioInfo.nombre"
                        });

                array.add(node);
            }

            response.setData(array);
            response.setMessage("Cambios");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("saveSolicitud")
    public JsonResponse saveSolicitud(@RequestBody List<ConceptoPrecio> conceptos, HttpSession session) {

        logger.debug("Conceptos {}", conceptos.size());

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

            service.saveCambioSolicitud(ds.getPostulante(), conceptos);
            postulanteService.actualizarImportes(ds.getPostulante(), ds.getCicloPostula());
            response.setMessage("Cambios");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("modalidades")
    public JsonResponse modalidades(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            List<ModalidadIngreso> modalidades = service.allModalidadesByCiclo(ds.getCicloPostula());
            logger.debug("modalidades {}", modalidades.size());
            Postulante postu = service.findPostulante(ds.getPostulante());
            checkModalidadesPostulante(modalidades, postu);

            List<ModalidadIngresoCiclo> modalidadesCiclo = service.allModalidadesCicloByCicloModalidades(ds.getCicloPostula(), modalidades);
            logger.debug("modalidadesCiclo {}", modalidadesCiclo.size());
            Map<Long, ModalidadIngresoCiclo> mapModalidades = TypesUtil.convertListToMap("modalidadIngreso.id", modalidadesCiclo);
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
            response.setData(array);
            response.setMessage("Modas");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("modalidades2")
    public JsonResponse modalidades2(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            List<ModalidadIngreso> modalidades = service.allModalidadesByCiclo(ds.getCicloPostula());
            Postulante postulante = service.findPostulante(ds.getPostulante());
            ModalidadIngreso modalidad = postulante.getModalidadIngreso();
            for (int i = 0; i < modalidades.size(); i++) {
                if (modalidades.get(i).getId() == modalidad.getId().longValue()) {
                    modalidades.remove(i);
                    break;
                }
            }

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ModalidadIngreso moda : modalidades) {
                ObjectNode node = JsonHelper.createJson(moda, JsonNodeFactory.instance, true,
                        new String[]{
                            "id",
                            "codigo",
                            "nombre",
                            "nombreCorto",
                            "nombreInscripcion",
                            "tipo",
                            "participanteLibre",
                            "quintoSecundaria",
                            "primerosPuestos",
                            "graduadosTituladosUniversitarios",
                            "preLaMolina"
                        });
                array.add(node);
            }
            response.setData(array);
            response.setMessage("Modas2");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("grados")
    public JsonResponse grados(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            List<GradoSecundaria> grados = buscarService.allGradoSecundaria();

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (GradoSecundaria grado : grados) {
                ObjectNode node = JsonHelper.createJson(grado, JsonNodeFactory.instance, true,
                        new String[]{
                            "id",
                            "orden",
                            "nombre"
                        });
                array.add(node);
            }
            response.setData(array);
            response.setMessage("Grados");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("universidades")
    public JsonResponse universidades(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            List<Universidad> universidades = buscarService.allUniversidadByEstadoPais(nombre, EstadoEnum.ACT, AdmisionConstantine.CODIGO_PERU);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (Universidad uni : universidades) {
                ObjectNode node = JsonHelper.createJson(uni, JsonNodeFactory.instance, true,
                        new String[]{
                            "*"
                        });
                array.add(node);
            }
            response.setData(array);
            response.setMessage("Universidades");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("findPrecio")
    public JsonResponse findConceptoPrecio(
            @RequestParam("modalidad") Long modalidadId,
            @RequestParam("colegio") Long colegioId,
            @RequestParam("colegioNombre") String colegioNombre,
            @RequestParam("universidad") Long universidadId,
            @RequestParam("universidadNombre") String universidadNombre,
            @RequestParam("paisCole") Long paisCole,
            @RequestParam("paisUni") Long paisUni,
            HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

            Postulante postulante = service.findPostulante(ds.getPostulante());
            Colegio colegio = colegioId != null ? new Colegio(colegioId) : postulante.getColegioProcedencia();
            Universidad universidad = universidadId != null ? new Universidad(universidadId) : postulante.getUniversidadProcedencia();
            Pais paisCo = paisCole != null ? new Pais(paisCole) : postulante.getPaisColegio();
            Pais paisUn = paisUni != null ? new Pais(paisUni) : postulante.getPaisUniversidad();
            String colegioExtranjero = StringUtils.isBlank(colegioNombre) ? postulante.getColegioExtranjero() : colegioNombre;
            String universidadExtranjera = StringUtils.isBlank(universidadNombre) ? postulante.getUniversidadExtranjera() : universidadNombre;
            ConceptoPrecio conceptoPrecio = service.findPrecioByDataPostulantePreview(postulante, new ModalidadIngreso(modalidadId),
                    ds.getCicloPostula(), colegio, colegioExtranjero, universidad, universidadExtranjera, paisCo, paisUn);

            ObjectNode node = JsonHelper.createJson(conceptoPrecio, JsonNodeFactory.instance, true,
                    new String[]{
                        "id",
                        "monto"
                    });

            response.setData(node);
            response.setMessage("Precio");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("anular")
    public JsonResponse anularSolicitud(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

            service.anularSolicitud(ds.getPostulante());
            postulanteService.actualizarImportes(ds.getPostulante(), ds.getCicloPostula());
            response.setMessage("Los cambios fueron anulados");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @RequestMapping("cambios")
    public String cambios(Model model, HttpSession session) {
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        List<SolicitudCambioInfo> solicitudes = service.allSolicitudesByPostulanteEstado(ds.getPostulante(), SolicitudCambioInfoEstadoEnum.PEND);

        Postulante postulante = service.findPostulante(ds.getPostulante());
        List<CarreraPostula> carrerasPostula = service.allCarreras(postulante.getModalidadIngreso(), ds.getCicloPostula());
        List<String> cambios = solicitudes.stream().map(d -> d.getTipoCambioInfo().getCodigo()).collect(Collectors.toList());
        ArrayNode aCarreras = new ArrayNode(JsonNodeFactory.instance);
        for (CarreraPostula carre : carrerasPostula) {
            ObjectNode carreNode = JsonHelper.createJson(carre, JsonNodeFactory.instance, true,
                    new String[]{
                        "id",
                        "carrera.codigo",
                        "carrera.nombre"
                    });
            aCarreras.add(carreNode);
        }
        ObjectNode nodePost = JsonHelper.createJson(postulante, JsonNodeFactory.instance, true,
                new String[]{
                    "id",
                    "persona.id",
                    "persona.numeroDocIdentidad",
                    "persona.tipoDocumento.simbolo",
                    "persona.primerNombre",
                    "persona.segundoNombre",
                    "persona.sexo",
                    "persona.paterno",
                    "persona.materno",
                    "persona.fechaNacer",
                    "persona.telefono",
                    "persona.celular",
                    "persona.email",
                    "persona.direccion",
                    "persona.paisNacer.*",
                    "persona.paisDomicilio.*",
                    "persona.nacionalidad.*",
                    "persona.ubicacionDomicilio.*",
                    "persona.ubicacionNacer.*",
                    "modalidadIngresoCiclo.id",
                    "modalidadIngresoCiclo.opciones"
                });
        logger.debug("postulante.getModalidadIngresoCiclo() {}", postulante.getModalidadIngresoCiclo().getOpciones());

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("ciclo", ds.getCicloPostula());
        model.addAttribute("persona", postulante.getPersona());
        model.addAttribute("carreras", aCarreras.toString());
        model.addAttribute("postulante", nodePost.toString());
        model.addAttribute("cambios", cambios.toString());
        return "admision/postulante/postulante/cambios/cambios";
    }

    @ResponseBody
    @RequestMapping("saveCambios")
    public JsonResponse saveCambios(@RequestBody Postulante postulante, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            service.saveCambios(postulante);

            response.setMessage("Datos actualizados");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

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

    @RequestMapping("solicitud")
    public String solicitud(Model model, HttpSession session) {
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesado = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();

        Postulante postulante = service.findPostulanteActivoByInteresado(interesado);

        if (postulante == null) {
            ds.setPasoInscripcion(0);
            session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);
            return "redirect:/inscripcion/postulante/0/paso";
        }

        if (interesado.getEstadoEnum() == NPOST) {
            model.addAttribute("ciclo", ciclo);
            model.addAttribute("interesado", interesado);
            return "admision/postulante/postulante/postulantePotencial";
        }

        DeudaInteresado deudaPagada = service.findDeudaPagada(postulante, ds.getCicloPostula());
        Postulante postulanteDB = service.findPostulante(postulante);
        List<SolicitudCambioInfo> solicitudes = service.allSolicitudesByPostulante(ds.getPostulante());
        List<CarreraPostula> carrerasPostula = service.allCarreras(postulante.getModalidadIngreso(), ds.getCicloPostula());
        ArrayNode carrerasJson = new ArrayNode(JsonNodeFactory.instance);
        for (CarreraPostula carre : carrerasPostula) {
            ObjectNode carreNode = JsonHelper.createJson(carre, JsonNodeFactory.instance, true,
                    new String[]{
                        "id",
                        "carrera.codigo",
                        "carrera.nombre"
                    });
            carrerasJson.add(carreNode);
        }
        ObjectNode nodeDeuda = JsonHelper.createJson(deudaPagada, JsonNodeFactory.instance, true,
                new String[]{
                    "id",
                    "monto",
                    "abono"
                });

        ObjectNode nodePostulante = JsonHelper.createJson(postulanteDB, JsonNodeFactory.instance, true,
                new String[]{
                    "id",
                    "importeDescuento",
                    "tipoInstitucion",
                    "yearEgresoColegio",
                    "gradoTitulo",
                    "gradoSecundaria.id",
                    "gradoSecundaria.orden",
                    "gradoSecundaria.nombre",
                    "modalidadIngreso.id",
                    "modalidadIngreso.tipo",
                    "modalidadIngreso.codigo",
                    "modalidadIngreso.participanteLibre",
                    "modalidadIngreso.quintoSecundaria",
                    "modalidadIngreso.primerosPuestos",
                    "modalidadIngreso.graduadosTituladosUniversitarios",
                    "modalidadIngreso.preLaMolina",
                    "descuentoExamen.id",
                    "descuentoExamen.estado",
                    "descuentoExamen.gestionColegio",
                    "descuentoExamen.porcentaje",
                    "modalidadIngresoCiclo.id",
                    "modalidadIngresoCiclo.requiereColegioUniversidad",
                    "modalidadIngresoCiclo.requiereSoloColegio",
                    "modalidadIngresoCiclo.requiereSoloUniversidad",
                    "modalidadIngresoCiclo.requiereColegio",
                    "modalidadIngresoCiclo.requiereUniversidad",
                    "modalidadIngresoCiclo.soloColegioPeruano",
                    "modalidadIngresoCiclo.soloColegioExtranjero",
                    "modalidadIngresoCiclo.verColegiosExtranjeros",
                    "modalidadIngresoCiclo.id",
                    "modalidadIngresoCiclo.opciones",
                    "colegioProcedencia.id",
                    "colegioProcedencia.esCoar",
                    "colegioExtranjero",
                    "universidadProcedencia.id",
                    "universidadExtranjera",
                    "cicloPostula.id",
                    "cicloPostula.cicloAcademico.year",
                    "persona.id",
                    "persona.numeroDocIdentidad",
                    "persona.tipoDocumento.simbolo",
                    "persona.primerNombre",
                    "persona.segundoNombre",
                    "persona.sexo",
                    "persona.paterno",
                    "persona.materno",
                    "persona.fechaNacer",
                    "persona.telefono",
                    "persona.celular",
                    "persona.email",
                    "persona.direccion",
                    "persona.paisNacer.*",
                    "persona.paisDomicilio.*",
                    "persona.nacionalidad.*",
                    "persona.ubicacionDomicilio.*",
                    "persona.ubicacionNacer.*"
                });

        model.addAttribute("ciclo", ds.getCicloPostula());
        model.addAttribute("persona", postulante.getPersona());
        model.addAttribute("deudaPagada", nodeDeuda == null ? null : nodeDeuda.toString());
        model.addAttribute("postulante", nodePostulante == null ? null : nodePostulante.toString());
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("carreras", carrerasJson.toString());

        return "admision/postulante/postulante/cambios/solicitud2";
    }

    @ResponseBody
    @RequestMapping("opciones")
    public JsonResponse opciones(@RequestParam("modalidad") Long idModalidad, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            ModalidadIngreso modIngreso = idModalidad != null ? new ModalidadIngreso(idModalidad) : ds.getPostulante().getModalidadIngreso();
            List<CarreraPostula> carrerasPostula = service.allCarreras(modIngreso, ds.getCicloPostula());
            ArrayNode carrerasJson = new ArrayNode(JsonNodeFactory.instance);
            for (CarreraPostula carre : carrerasPostula) {
                ObjectNode carreNode = JsonHelper.createJson(carre, JsonNodeFactory.instance, true,
                        new String[]{
                            "id",
                            "carrera.codigo",
                            "carrera.nombre"
                        });
                carrerasJson.add(carreNode);
            }
            response.setData(carrerasJson);
            response.setMessage("Carreras");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("dataForCambios")
    public JsonResponse dataForCambios(HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            Postulante postulante = service.findPostulante(ds.getPostulante());
            CicloPostula ciclo = ds.getCicloPostula();

            List<ConceptoPrecio> cambios = service.allConceptoPrecioCambiosByCiclo(postulante, ciclo);
            List<ModalidadIngreso> modalidades = service.allModalidadesByCiclo(ds.getCicloPostula());
            List<ModalidadIngreso> modalidadesCambio = service.allModalidadesCambioForPostulante(postulante, modalidades);
            List<ModalidadIngreso> modalidadesSimulacion = service.allModalidadesCambioForSimulacion(modalidades, ciclo);
            List<ModalidadIngresoCiclo> modalidadesCiclo = service.allModalidadesCicloByCicloModalidades(ciclo, modalidadesCambio);
            List<GradoSecundaria> grados = buscarService.allGradoSecundaria();

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.set("cambios", createConceptosJson(cambios));
            node.set("modalidadesCambio", createModalidadesJson(modalidadesCambio));
            node.set("modalidadesSimulacion", createModalidadesJson(modalidadesSimulacion));
            node.set("modalidadesRestricciones", createModalidadesCicloJson(modalidadesCiclo));
            node.set("grados", createGradosJson(grados));

            response.setData(node);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    private ArrayNode createConceptosJson(List<ConceptoPrecio> conceptos) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (ConceptoPrecio concepto : conceptos) {
            ObjectNode node = JsonHelper.createJson(concepto, JsonNodeFactory.instance, true,
                    new String[]{
                        "id",
                        "monto",
                        "conceptoPago.id",
                        "conceptoPago.descripcion",
                        "conceptoPago.cuentaBancaria.id",
                        "tipoCambioInfo.id",
                        "tipoCambioInfo.codigo",
                        "tipoCambioInfo.nombre"
                    });

            array.add(node);
        }
        return array;
    }

    private ArrayNode createModalidadesJson(List<ModalidadIngreso> modalidades) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (ModalidadIngreso modalidad : modalidades) {
            ObjectNode node = JsonHelper.createJson(modalidad, JsonNodeFactory.instance, true,
                    new String[]{
                        "id",
                        "codigo",
                        "nombre",
                        "nombreCorto",
                        "nombreInscripcion",
                        "tipo",
                        "participanteLibre",
                        "quintoSecundaria",
                        "primerosPuestos",
                        "graduadosTituladosUniversitarios",
                        "preLaMolina"
                    });
            array.add(node);
        }
        return array;
    }

    private ArrayNode createModalidadesCicloJson(List<ModalidadIngresoCiclo> modalidadesCiclo) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (ModalidadIngresoCiclo moda : modalidadesCiclo) {
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

    private ArrayNode createGradosJson(List<GradoSecundaria> grados) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (GradoSecundaria grado : grados) {
            ObjectNode node = JsonHelper.createJson(grado, JsonNodeFactory.instance, true,
                    new String[]{
                        "id",
                        "orden",
                        "nombre"
                    });
            array.add(node);
        }
        return array;
    }

}
