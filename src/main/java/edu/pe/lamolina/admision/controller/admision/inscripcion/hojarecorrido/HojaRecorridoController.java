package edu.pe.lamolina.admision.controller.admision.inscripcion.hojarecorrido;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postpago.PostPagoService;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import edu.pe.lamolina.admision.zelper.pdf.PdfHtmlView;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.OficinaRecorrido;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.bienestar.DocumentoBienestar;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.examen.AlumnoTestSicologico;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.PostulanteDocumento;
import pe.edu.lamolina.model.socioeconomico.FichaSocioeconomica;

@Controller
@RequestMapping("inscripcion/hojarecorrido")
public class HojaRecorridoController {

    @Autowired
    HojaRecorridoService service;

    @Autowired
    PostPagoService postPagoService;

    @Autowired
    PdfHtmlView pdfHtmlView;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session, RedirectAttributes redirectAttr) {
        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

            model.addAttribute("ciclo", ds.getCicloPostula());
            model.addAttribute("postulante", ds.getPostulante());
            model.addAttribute("verNivelacion", service.verNivelacion(ds));
            model.addAttribute("verInscritoNivelacion", service.verInscritoNivelacion(ds));
            Alumno alumno = service.findAlumnoByPostulante(ds.getPostulante());

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, redirectAttr);
            return "redirect:/";

        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/";
        }

        return "admision/inscripcion/hojarecorrido/hojarecorrido";
    }

    @ResponseBody
    @RequestMapping("loadHojaRecorrido")
    public JsonResponse loadHojaRecorrido(Model model, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
            List<OficinaRecorrido> oficinasRecorrido = service.allOficinasRecorridoResumen(ds.getPostulante(), ds.getCicloPostula());

            ActividadIngresante actividadCareo = service.findActividadCareo(ds.getPostulante(), ds.getCicloPostula().getCicloAcademico());
            FichaSocioeconomica ficha = service.findFichaSocioeconomica(ds.getPostulante(), ds.getCicloPostula().getCicloAcademico());
            AlumnoTestSicologico test = service.findTestSicologico(ds.getPostulante(), ds.getCicloPostula().getCicloAcademico());
            List<AporteAlumnoCiclo> aportes = service.allAportesByAlumnoCiclo(ds.getPostulante(), ds.getCicloPostula().getCicloAcademico());

            actividadCareo = (actividadCareo == null) ? new ActividadIngresante() : actividadCareo;
            ObjectNode jCareo = JsonHelper.createJson(actividadCareo, JsonNodeFactory.instance, true, new String[]{"*"});
            ObjectNode jEducacion = createEducacionJson(ds.getPostulante());
            ObjectNode jEmail = createEmailJson(service.findAlumnoByPostulante(ds.getPostulante()));

            JsonNodeFactory nc = JsonNodeFactory.instance;
            ArrayNode jOficinasRecorrido = new ArrayNode(JsonNodeFactory.instance);
            for (OficinaRecorrido oficinaRecorrido : oficinasRecorrido) {
                ObjectNode jOficinaRecorrido = JsonHelper.createJson(oficinaRecorrido, nc, false, new String[]{
                    "*",
                    "oficina.*",
                    "actividadesCompletadas"
                });

                String[] mapperActividadesIngresange = new String[]{
                    "*",
                    "estadoInc",
                    "estadoAct",
                    "estadoPend",
                    "tipoActividadIngresante.*",
                    "tipoActividadIngresante.tipoRPAGOADM",
                    "tipoActividadIngresante.tipoDOCS",
                    "tipoActividadIngresante.tipoENTREV",
                    "tipoActividadIngresante.tipoMATRI",
                    "tipoActividadIngresante.tipoFISOEC",
                    "tipoActividadIngresante.tipoTESTPSIC",
                    "tipoActividadIngresante.tipoPAGOMATRI",
                    "tipoActividadIngresante.tipoPAGOEXAMED"};

                ArrayNode jActividadesIngresante = new ArrayNode(nc);
                for (ActividadIngresante actividadIngresante : oficinaRecorrido.getActividadesIngresante()) {
                    ObjectNode jActividadIngresante = JsonHelper.createJson(actividadIngresante, nc, true, mapperActividadesIngresange);
                    if (actividadIngresante.getTipoActividadIngresante().isTipoDOCS()) {
                        jActividadIngresante.set("postulanteDocumentos", this.allJDocumentosPostulante(ds.getPostulante()));
                    }
                    if (actividadIngresante.getTipoActividadIngresante().isTipoENTREV()) {
                        jActividadIngresante.set("documentoBienestar", this.allJDocumentosSocioEconomicos(ds.getCicloPostula().getCicloAcademico()));
                    }
                    if (actividadIngresante.getTipoActividadIngresante().isTipoFISOEC()) {
                        jActividadIngresante.set("fichaSocioeconomica", this.createFichaJson(ficha));
                    }
                    if (actividadIngresante.getTipoActividadIngresante().isTipoTESTPSIC()) {
                        jActividadIngresante.set("testPsicologico", this.createTestJson(test));
                    }
                    if (actividadIngresante.getTipoActividadIngresante().isTipoPAGOMATRI()) {
                        jActividadIngresante.put("tieneAportes", !aportes.isEmpty());
                    }
                    jActividadesIngresante.add(jActividadIngresante);
                }

                jOficinaRecorrido.set("actividadesIngresante", jActividadesIngresante);
                jOficinaRecorrido.put("mesTurno", "");
                jOficinaRecorrido.put("diaTurno", "");

                if (oficinaRecorrido.getFechaTurno() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMMM", new Locale("es", "ES"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(oficinaRecorrido.getFechaTurno());

                    int dia = +calendar.get(Calendar.DAY_OF_MONTH);
                    String mes = sdf.format(oficinaRecorrido.getFechaTurno());
                    jOficinaRecorrido.put("mesTurno", mes);
                    jOficinaRecorrido.put("diaTurno", dia);
                }
                jOficinasRecorrido.add(jOficinaRecorrido);
            }

            ObjectNode data = new ObjectNode(nc);
            data.set("oficinasRecorrido", jOficinasRecorrido);
            data.set("careo", jCareo);
            data.set("educacion", jEducacion);
            data.set("email", jEmail);
            response.setData(data);

            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        } finally {
            return response;
        }
    }

    public ArrayNode allJDocumentosPostulante(Postulante postulante) {
        List<PostulanteDocumento> documentos = service.allDocumentosByPostulante(postulante);
        JsonNodeFactory jc = JsonNodeFactory.instance;
        ArrayNode jDocumentos = new ArrayNode(jc);
        for (PostulanteDocumento doc : documentos) {
            ObjectNode jDoc = JsonHelper.createJson(doc, jc, true, new String[]{
                "*",
                "documentoModalidad.id",
                "documentoModalidad.documentoRequisito.id",
                "documentoModalidad.documentoRequisito.nombre",
                "postulante.id"
            });
            jDocumentos.add(jDoc);
        }
        return jDocumentos;
    }

    public ArrayNode allJDocumentosSocioEconomicos(CicloAcademico cicloAcademico) {
        List<DocumentoBienestar> documentos = service.allDocumentosSocioEconomicosByCiclo(cicloAcademico);
        JsonNodeFactory jc = JsonNodeFactory.instance;
        ArrayNode jDocumentos = new ArrayNode(jc);
        for (DocumentoBienestar documento : documentos) {
            ObjectNode jDocumento = JsonHelper.createJson(documento, jc, true, new String[]{
                "*",
                "tipoSubvencion.*",
                "tipoTramite.*"
            });
            jDocumentos.add(jDocumento);
        }
        return jDocumentos;
    }

    private ObjectNode createEducacionJson(Postulante postulante) {
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        node.put("confirmar", postulante.getFechaConfirmaEducacion() != null);

        boolean tieneCambioColegio = service.verificarCambioColegio(postulante);
        node.put("tieneCambioActivo", tieneCambioColegio);

        if (postulante.getPaisColegio() != null) {
            ObjectNode colegioJson = null;
            Colegio colegio = postulante.getColegioProcedencia();

            if (colegio != null) {
                colegioJson = JsonHelper.createJson(colegio, JsonNodeFactory.instance, true, new String[]{
                    "id", "nombre", "codigoModular", "anexo", "codigoLocal", "direccion",
                    "referencia", "localidad", "centroPoblado", "nombreLargo",
                    "caracteristica.nombre",
                    "formaAtencion.nombre",
                    "gestion.nombre",
                    "gestionDependencia.nombre",
                    "nivelModalidad.nombre",
                    "ubicacion.id",
                    "ubicacion.distrito"
                });

            } else {
                colegioJson = new ObjectNode(JsonNodeFactory.instance);
                colegioJson.put("nombre", postulante.getColegioExtranjero());
            }

            node.set("colegio", colegioJson);
            node.set("paisColegio", createPaisJson(postulante.getPaisColegio()));
            node.put("mostrarColegio", true);
            node.put("mostrarUniversidad", false);
        }

        if (postulante.getPaisUniversidad() != null) {
            ObjectNode universidadJson = null;
            Universidad universidad = postulante.getUniversidadProcedencia();

            if (universidad != null) {
                universidadJson = JsonHelper.createJson(universidad, JsonNodeFactory.instance, true, new String[]{
                    "id", "codigo", "nombre", "siglas"
                });

            } else {
                universidadJson = new ObjectNode(JsonNodeFactory.instance);
                node.put("universidadNombre", postulante.getUniversidadExtranjera());
            }

            node.set("universidad", universidadJson);
            node.set("paisUniversidad", createPaisJson(postulante.getPaisUniversidad()));
            node.put("mostrarColegio", false);
            node.put("mostrarUniversidad", true);
        }
        return node;
    }

    private ObjectNode createPaisJson(Pais pais) {
        return JsonHelper.createJson(pais, JsonNodeFactory.instance, new String[]{
            "id", "codigo", "nombre"
        });
    }

    private ObjectNode createFichaJson(FichaSocioeconomica ficha) {
        return JsonHelper.createJson(ficha, JsonNodeFactory.instance, new String[]{
            "id", "estado"
        });
    }

    private ObjectNode createTestJson(AlumnoTestSicologico test) {
        return JsonHelper.createJson(test, JsonNodeFactory.instance, new String[]{
            "id", "estado"
        });
    }

    private ObjectNode createEmailJson(Alumno alumno) {
        return JsonHelper.createJson(alumno, JsonNodeFactory.instance, new String[]{
            "emailIngresante", "claveEmailIngresante", "codigo"
        });
    }

    @RequestMapping("generarBoletaExamenMedico")
    public ModelAndView generarBoletaExamenMedico(Model model, HttpSession session, HttpServletResponse response) throws Exception {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        if (ds == null) {
            return new ModelAndView("redirect:/");
        }
        service.generarBoletaExamenMedico(model, ds);

        return new ModelAndView(pdfHtmlView);
    }

    @RequestMapping("boletaMatricula")
    public ModelAndView boletaMatricula(HttpSession session, HttpServletResponse respons, RedirectAttributes redirectAttr, Model model) {
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        service.generarBoletaIngreso(model, ds);
        return new ModelAndView(pdfHtmlView);
    }
    
    @ResponseBody
    @RequestMapping(value = "inscripcion", method = RequestMethod.POST)
    public JsonResponse actualizarMatricula(HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {
            DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.inscripcion(ds);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

}
