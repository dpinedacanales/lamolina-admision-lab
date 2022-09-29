package edu.pe.lamolina.admision.controller.admision.inscripcion.hojarecorrido;

import edu.pe.lamolina.admision.dao.academico.ActividadIngresanteDAO;
import edu.pe.lamolina.admision.dao.academico.AlumnoDAO;
import edu.pe.lamolina.admision.dao.academico.CicloAcademicoDAO;
import edu.pe.lamolina.admision.dao.academico.ConfigRecorridoIngresanteDAO;
import edu.pe.lamolina.admision.dao.academico.EventoCicloAcademicoDAO;
import edu.pe.lamolina.admision.dao.academico.FichaSocioeconomicaDAO;
import edu.pe.lamolina.admision.dao.academico.MatriculaBloqueoIngresanteDAO;
import edu.pe.lamolina.admision.dao.academico.OficinaRecorridoDAO;
import edu.pe.lamolina.admision.dao.academico.RecorridoIngresanteDAO;
import edu.pe.lamolina.admision.dao.aporte.AporteAlumnoCicloDAO;
import edu.pe.lamolina.admision.dao.examen.AlumnoTestSicologicoDAO;
import edu.pe.lamolina.admision.dao.finanzas.DeudaAlumnoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ItemDeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.general.ContenidoCartaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.DocumentoModalidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.IngresanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDocumentoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TipoCambioInfoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TurnoEntrevistaObuaeDAO;
import edu.pe.lamolina.admision.dao.medico.ConceptoExamenMedicoDAO;
import edu.pe.lamolina.admision.dao.medico.ConceptoMedicoCicloDAO;
import edu.pe.lamolina.admision.dao.medico.ExamenMedicoDAO;
import edu.pe.lamolina.admision.dao.obu.DocumentoBienestarDAO;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import edu.pe.lamolina.admision.zelper.pdf.PDFFormatoEnum;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ConfigRecorridoIngresante;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;
import pe.edu.lamolina.model.academico.OficinaRecorrido;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.bienestar.DocumentoBienestar;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.EstadoTestSicologicoEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.enums.FichaSocioeconomicaEstadoEnum;
import pe.edu.lamolina.model.enums.PostulanteDocumentoEstadoEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.enums.RecorridoIngresanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.enums.TipoCambioInfoEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.ValidadorDocumentoEnum;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.ESTIMADO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NOMBRE_PERSONA;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NRO_DOCUMENTO;
import pe.edu.lamolina.model.examen.AlumnoTestSicologico;
import pe.edu.lamolina.model.finanzas.DeudaAlumno;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.DocumentoModalidad;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Ingresante;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.PostulanteDocumento;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.ConceptoExamenMedico;
import pe.edu.lamolina.model.medico.ExamenMedico;
import pe.edu.lamolina.model.socioeconomico.FichaSocioeconomica;

@Service
@Transactional(readOnly = false)
public class HojaRecorridoServiceImp implements HojaRecorridoService {

    @Autowired
    RecorridoIngresanteDAO recorridoIngresanteDAO;

    @Autowired
    ConfigRecorridoIngresanteDAO configRecorridoIngresanteDAO;

    @Autowired
    OficinaRecorridoDAO oficinaRecorridoDAO;

    @Autowired
    ActividadIngresanteDAO actividadIngresanteDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    ExamenMedicoDAO examenMedicoDAO;

    @Autowired
    ConceptoExamenMedicoDAO conceptoExamenMedicoDAO;

    @Autowired
    ConceptoMedicoCicloDAO conceptoMedicoCicloDAO;

    @Autowired
    PostulanteDAO postulanteDAO;

    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;

    @Autowired
    PostulanteDocumentoDAO postulanteDocumentoDAO;

    @Autowired
    DocumentoModalidadDAO documentoModalidadDAO;

    @Autowired
    TipoCambioInfoDAO tipoCambioInfoDAO;

    @Autowired
    ItemDeudaInteresadoDAO itemDeudaInteresadoDAO;

    @Autowired
    DocumentoBienestarDAO documentoBienestarDAO;

    @Autowired
    EventoCicloDAO eventoCicloDAO;

    @Autowired
    TurnoEntrevistaObuaeDAO turnoEntrevistaObuaeDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    DeudaAlumnoDAO deudaAlumnoDAO;

    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;

    @Autowired
    FichaSocioeconomicaDAO fichaSocioeconomicaDAO;

    @Autowired
    AlumnoTestSicologicoDAO alumnoTestSicologicoDAO;

    @Autowired
    AporteAlumnoCicloDAO aporteAlumnoCicloDAO;

    @Autowired
    MatriculaBloqueoIngresanteDAO matriculaBloqueoIngresanteDAO;

    @Autowired
    IngresanteDAO ingresanteDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    CicloPostulaDAO cicloPostulaDAO;

    @Override
    public List<OficinaRecorrido> allOficinasRecorridoResumen(Postulante postulante, CicloPostula cicloPostula) {

        Alumno alumno = alumnoDAO.findByPostulante(postulante);
        List< OficinaRecorrido> oficinasRecorrido = oficinaRecorridoDAO.allOrderByOrdenAsc();
        List<ConfigRecorridoIngresante> actividadesConfiguradas = configRecorridoIngresanteDAO.allByCicloAcademico(cicloPostula.getCicloAcademico(), "cri.ordenActividad");
        if (postulante.isCepre()) {
            actividadesConfiguradas.removeIf(x -> x.getTipoActividadIngresante().isTipoDOCS() || x.getTipoActividadIngresante().isTipoCAREO());
        }
        Map<Long, List<ConfigRecorridoIngresante>> mapActividadesConfiguradas
                = TypesUtil.convertListToMapList("tipoActividadIngresante.oficinaRecorrido.id", actividadesConfiguradas);

        RecorridoIngresante recorridoIngresante = recorridoIngresanteDAO.findAlumnoAndCiclo(alumno, cicloPostula.getCicloAcademico());
        List<ActividadIngresante> actividadesByIngresante = actividadIngresanteDAO.allByRecorridoIngresante(recorridoIngresante);

        EventoCiclo entregaDocumentos = eventoCicloDAO.findByCicloEvento(cicloPostula, EventoEnum.ENTDOC);
        if (entregaDocumentos == null) {
            throw new PhobosException("No se configurado entrega de documentos");
        }
        TurnoEntrevistaObuae turnoEntrevista = recorridoIngresante.getTurnoEntrevistaObuae();
        EventoCicloAcademico matriculaIngresantesPre = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloPostula.getCicloAcademico(), EventoAcademicoEnum.MAT_ING);

        for (OficinaRecorrido oficinaRecorrido : oficinasRecorrido) {
            oficinaRecorrido.setActividadesIngresante(new ArrayList<>());
            List<ConfigRecorridoIngresante> tipoActividadesConfiguradasByOficina = mapActividadesConfiguradas.get(oficinaRecorrido.getId());
            if (oficinaRecorrido.getOficina().isOficinaCAP()) {
                oficinaRecorrido.setFechaTurno(entregaDocumentos.getFechaInicio());
            }
            if (oficinaRecorrido.getOficina().isOficinaOBUAE() && turnoEntrevista != null) {
                oficinaRecorrido.setFechaTurno(turnoEntrevista.getFecha());
            }
            if (oficinaRecorrido.getOficina().isOficinaOera()) {
                if (matriculaIngresantesPre != null) {
                    oficinaRecorrido.setFechaTurno(matriculaIngresantesPre.getFechaInicio());
                }
            }

            for (ConfigRecorridoIngresante configRecorridoIngresante : tipoActividadesConfiguradasByOficina) {
                ActividadIngresante actividad = actividadesByIngresante.stream()
                        .filter(x -> x.getTipoActividadIngresante().equals(configRecorridoIngresante.getTipoActividadIngresante()))
                        .findFirst().orElse(null);
                if (actividad == null) {
                    actividad = new ActividadIngresante();
                    actividad.setTipoActividadIngresante(configRecorridoIngresante.getTipoActividadIngresante());
                    actividad.setEstadoEnum(RecorridoIngresanteEstadoEnum.PEND);
                }
                oficinaRecorrido.getActividadesIngresante().add(actividad);
            }
            int actividadesCompletadas = (int) oficinaRecorrido.getActividadesIngresante().stream()
                    .filter(x -> x.isEstadoAct())
                    .count();
            oficinaRecorrido.setActividadesCompletadas(actividadesCompletadas);
        }
        return oficinasRecorrido;
    }

    @Override
    public void generarBoletaExamenMedico(Model model, DataSessionAdmision ds) {
        Interesado interesado = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();
        CicloAcademico cicloAcademico = ciclo.getCicloAcademico();

        Alumno alumno = alumnoDAO.findByPostulante(ds.getPostulante());

        ExamenMedico examenMedico = examenMedicoDAO.findByAlumnoAndCiclo(alumno, cicloAcademico);
        List<ConceptoExamenMedico> conceptosExamenMedico = conceptoExamenMedicoDAO.allByExamenMedico(examenMedico);

        Postulante postulante = postulanteDAO.findByInteresado(interesado);

        ContenidoCarta headerPlantilla = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETA_EXMAEN_MEDICO_PDF);
        ContenidoCarta footerPlantilla = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETA_EXMAEN_MEDICO_PDF);

        Persona persona = alumno.getPersona();
        String estimado = persona.esFemenino() ? "Estimada" : "Estimado";

        String header = headerPlantilla.getContenido();

        header = header.replace(NOMBRE_PERSONA.getValue(), persona.getNombreCompleto());
        header = header.replace(ESTIMADO.getValue(), estimado);
        header = header.replace(NRO_DOCUMENTO.getValue(), persona.getNumeroDocIdentidad());

        model.addAttribute("header", header);
        model.addAttribute("footer", footerPlantilla.getContenido());
        model.addAttribute("estimado", estimado);
        model.addAttribute("examenMedico", examenMedico);
        model.addAttribute("conceptosExamenMedico", conceptosExamenMedico);
        model.addAttribute("deudas", conceptosExamenMedico);
        model.addAttribute("formatoEnum", PDFFormatoEnum.BOLETA_EXAMEN_MEDICO);
        model.addAttribute("conceptoMedico", conceptosExamenMedico.get(0).getConceptoMedicoCiclo().getConceptoMedico());
        model.addAttribute("cuentaBancaria", conceptosExamenMedico.get(0).getConceptoMedicoCiclo().getConceptoMedico().getCuentaBancaria());
        model.addAttribute("nombrePdf", "BoletaExamenMedico_" + postulante.getCodigo());
        model.addAttribute("persona", postulante.getPersona());
        model.addAttribute("helper", new NumberFormat());

    }

    @Override
    public List<PostulanteDocumento> allDocumentosByPostulante(Postulante postulante) {
        List<PostulanteDocumento> documentosPostulante = postulanteDocumentoDAO.allByPostulante(postulante);
        Map<Long, PostulanteDocumento> mapDocPostulante = TypesUtil.convertListToMap("documentoModalidad.id", documentosPostulante);
        List<DocumentoModalidad> documentosModalidad = documentoModalidadDAO.allByModalidadCiclo(postulante.getModalidadIngreso(), postulante.getCicloPostula());

        for (DocumentoModalidad docuModal : documentosModalidad) {
            PostulanteDocumento docuPostulante = mapDocPostulante.get(docuModal.getId());
            if (docuPostulante != null) {
                continue;
            }

            docuPostulante = new PostulanteDocumento();
            docuPostulante.setDocumentoModalidad(docuModal);
            docuPostulante.setEntregado(0);
            docuPostulante.setEstadoEnum(PostulanteDocumentoEstadoEnum.SIN_ENTREGAR);
            docuPostulante.setValidadorEnum(ValidadorDocumentoEnum.USER);
            docuPostulante.setPostulante(postulante);
            documentosPostulante.add(docuPostulante);
        }

        return documentosPostulante;
    }

    @Override
    public ActividadIngresante findActividadCareo(Postulante postulante, CicloAcademico cicloAcademico) {
        Alumno alumno = alumnoDAO.findByPostulante(postulante);
        RecorridoIngresante recorridoIngresante = recorridoIngresanteDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        return actividadIngresanteDAO.findByRecorridoCodigoTipo(TipoActividadIngresanteEnum.CAREO, recorridoIngresante);
    }

    @Override
    public Alumno findAlumnoByPostulante(Postulante postulante) {
        Alumno alumno = alumnoDAO.findByPostulante(postulante);
        return alumno;
    }

    @Override
    public boolean verificarCambioColegio(Postulante postulante) {
        TipoCambioInfo tipo = tipoCambioInfoDAO.findByCodigo(TipoCambioInfoEnum.CCOLEUNIDE);
        ItemDeudaInteresado cambioActivo = itemDeudaInteresadoDAO.findActivoByInteresadoTipoSolicitudCambio(postulante.getInteresado(), tipo);
        return cambioActivo != null;
    }

    @Override
    public List<DocumentoBienestar> allDocumentosSocioEconomicosByCiclo(CicloAcademico cicloAcademico) {
        return documentoBienestarDAO.allByCicloAcademico(cicloAcademico, TipoTramiteEnum.SOCIOECONO);
    }

    @Override
    public void generarBoletaIngreso(Model model, DataSessionAdmision ds) {
        Alumno alumno = alumnoDAO.findByPostulante(ds.getPostulante());
        CicloAcademico cicloAcademico = ds.getCicloPostula().getCicloAcademico();
        List<DeudaAlumno> deudasAlumno = deudaAlumnoDAO.allByAlumnoCiclo(alumno, cicloAcademico);

        ContenidoCarta headerPlantilla = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.HEAD_BOLETA_PAGO_PDF_ALUMNO);
        ContenidoCarta footerPlantilla = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.FOOT_BOLETA_PAGO_PDF_ALUMNO);

        Persona persona = alumno.getPersona();
        String estimado = persona.esFemenino() ? "Estimada" : "Estimado";

        String header = headerPlantilla.getContenido();

        header = header.replace(NOMBRE_PERSONA.getValue(), persona.getNombreCompleto());
        header = header.replace(ESTIMADO.getValue(), estimado);
        header = header.replace(NRO_DOCUMENTO.getValue(), persona.getNumeroDocIdentidad());

        model.addAttribute("header", header);
        model.addAttribute("footer", footerPlantilla.getContenido());
        model.addAttribute("estimado", estimado);
        model.addAttribute("persona", persona);
        model.addAttribute("alumno", alumno);
        model.addAttribute("boletas", deudasAlumno);
        model.addAttribute("formatoEnum", PDFFormatoEnum.BOLETA_PAGO_ING);
        model.addAttribute("nombrePdf", "BoletaPago_" + alumno.getCodigo());

    }

    @Override
    public FichaSocioeconomica findFichaSocioeconomica(Postulante postulante, CicloAcademico cicloAcademico) {
        Alumno alumno = alumnoDAO.findByPostulante(postulante);
        FichaSocioeconomica ficha = fichaSocioeconomicaDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        if (ficha != null) {
            return ficha;
        }
        ficha = new FichaSocioeconomica();
        ficha.setEstadoEnum(FichaSocioeconomicaEstadoEnum.PEND);
        return ficha;
    }

    @Override
    public AlumnoTestSicologico findTestSicologico(Postulante postulante, CicloAcademico cicloAcademico) {
        Alumno alumno = alumnoDAO.findByPostulante(postulante);
        AlumnoTestSicologico test = alumnoTestSicologicoDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        if (test != null) {
            return test;
        }
        test = new AlumnoTestSicologico();
        test.setPreguntasContestadas(0);
        test.setTotalPreguntas(200);
        test.setEstadoEnum(EstadoTestSicologicoEnum.ACT);
        return test;
    }

    @Override
    public List<AporteAlumnoCiclo> allAportesByAlumnoCiclo(Postulante postulante, CicloAcademico cicloAcademico) {
        Alumno alumno = alumnoDAO.findByPostulante(postulante);
        return aporteAlumnoCicloDAO.allByAlumnoCiclo(alumno, cicloAcademico);
    }

    @Override
    @Transactional
    public Boolean verNivelacion(DataSessionAdmision ds) {
        List<MatriculaBloqueoIngresante> lista = matriculaBloqueoIngresanteDAO.allByCicloAcademico(ds.getCicloPostula().getCicloAcademico());
        if (lista.isEmpty()) {
            return Boolean.FALSE;
        }

        Ingresante existeIngresante = this.existeIngresante(ds);

        if (existeIngresante == null) {
            return Boolean.FALSE;
        }

        MatriculaBloqueoIngresante ingresanteBloqueo = matriculaBloqueoIngresanteDAO.findByCicloAcademicoIngresante(ds.getCicloPostula().getCicloAcademico(), existeIngresante);

        if (ingresanteBloqueo == null) {
            ingresanteBloqueo = this.creacionIngresante(existeIngresante, ds);
        }

        return !ingresanteBloqueo.getMatricula();
    }

    @Override
    public Boolean verInscritoNivelacion(DataSessionAdmision ds) {
        List<MatriculaBloqueoIngresante> lista = matriculaBloqueoIngresanteDAO.allByCicloAcademico(ds.getCicloPostula().getCicloAcademico());
        if (lista.isEmpty()) {
            return Boolean.FALSE;
        }
        Ingresante existeIngresante = this.existeIngresante(ds);

        if (existeIngresante == null) {
            return Boolean.FALSE;
        }

        MatriculaBloqueoIngresante ingresanteBloqueo = matriculaBloqueoIngresanteDAO.findByCicloAcademicoIngresante(ds.getCicloPostula().getCicloAcademico(), existeIngresante);
        if (!ingresanteBloqueo.getInscrito()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Ingresante existeIngresante(DataSessionAdmision ds) {

        Postulante postulante = postulanteDAO.findActivoByCodigoCiclo(ds.getPostulante().getCodigo(), ds.getCicloPostula());

        if (postulante != null) {
            Ingresante ingresante = ingresanteDAO.findByPostulante(postulante);
            if (ingresante == null) {
                return null;
            }
            return ingresante;
        } else {
            CicloAcademico cicloAnterior = cicloAcademicoDAO.findCicloAnteriorRegular(ds.getCicloPostula().getCicloAcademico());
            CicloPostula cicloPostulaAnterior = cicloPostulaDAO.findByCicloAcademico(cicloAnterior);
            postulante = postulanteDAO.findActivoByCodigoCiclo(ds.getPostulante().getCodigo(), cicloPostulaAnterior);

            if (postulante != null) {
                Ingresante ingresante = ingresanteDAO.findByPostulante(postulante);
                if (ingresante == null) {
                    return null;
                }
                return ingresante;
            }
            return null;
        }

    }

    private Ingresante findIngresante(DataSessionAdmision ds) {
        Postulante postulante = postulanteDAO.findActivoByCodigoCiclo(ds.getPostulante().getCodigo(), ds.getCicloPostula());
        return ingresanteDAO.findByPostulante(postulante);
    }

    private MatriculaBloqueoIngresante creacionIngresante(Ingresante ingresante, DataSessionAdmision ds) {
        BigDecimal notaMinima = new BigDecimal(10.5);

        MatriculaBloqueoIngresante bloqueoIngresante = new MatriculaBloqueoIngresante();
        bloqueoIngresante.setIngresante(ingresante);
        bloqueoIngresante.setCicloAcademico(ds.getCicloPostula().getCicloAcademico());
        bloqueoIngresante.setInscrito(Boolean.FALSE);

        if (ingresante.getPostulante().getModalidadIngreso().getCodigo().equalsIgnoreCase("04")
                || ingresante.getPostulante().getModalidadIngreso().getCodigo().equalsIgnoreCase("05")
                || ingresante.getPostulante().getModalidadIngreso().getCodigo().equalsIgnoreCase("08")) {

            bloqueoIngresante.setRm(ingresante.getEvaluado().getPuntajeRm());
            bloqueoIngresante.setRv(ingresante.getEvaluado().getPuntajeRv());
            bloqueoIngresante.setMatricula(Boolean.FALSE);
            this.validarRvRM(ingresante, bloqueoIngresante, notaMinima);
        } else {

            if (ingresante.getPostulante().getModalidadIngreso().getCodigo().equalsIgnoreCase("03")) {
                bloqueoIngresante.setRm(ingresante.getPrelamolina().getPuntajeRm());
                bloqueoIngresante.setRv(ingresante.getPrelamolina().getPuntajeRv());
                bloqueoIngresante.setMatematica(ingresante.getPrelamolina().getPuntajeMatematicas());
                bloqueoIngresante.setFisica(ingresante.getPrelamolina().getPuntajeFisica());
                bloqueoIngresante.setQuimica(ingresante.getPrelamolina().getPuntajeQuimica());
                bloqueoIngresante.setBiologia(ingresante.getPrelamolina().getPuntajeBiologia());
            } else {
                bloqueoIngresante.setRm(ingresante.getEvaluado().getPuntajeRm());
                bloqueoIngresante.setRv(ingresante.getEvaluado().getPuntajeRv());
                bloqueoIngresante.setMatematica(ingresante.getEvaluado().getPuntajeMatematicas());
                bloqueoIngresante.setFisica(ingresante.getEvaluado().getPuntajeFisica());
                bloqueoIngresante.setQuimica(ingresante.getEvaluado().getPuntajeQuimica());
                bloqueoIngresante.setBiologia(ingresante.getEvaluado().getPuntajeBiologia());
            }
            bloqueoIngresante.setMatricula(Boolean.FALSE);
            this.validarDemasMaterias(ingresante, bloqueoIngresante, notaMinima);

        }

        bloqueoIngresante.setFechaRegistro(new Date());
        bloqueoIngresante.setUsuario(ds.getUsuario());
        matriculaBloqueoIngresanteDAO.save(bloqueoIngresante);
        return bloqueoIngresante;
    }

    private void validarRvRM(Ingresante ingresante, MatriculaBloqueoIngresante bloqueoIngresante, BigDecimal notaMinima) {

        if (ingresante.getEvaluado().getPuntajeRm().compareTo(notaMinima) >= 0
                && ingresante.getEvaluado().getPuntajeRv().compareTo(notaMinima) >= 0) {
            bloqueoIngresante.setMatricula(Boolean.TRUE);
        }
    }

    private void validarDemasMaterias(Ingresante ingresante, MatriculaBloqueoIngresante bloqueoIngresante, BigDecimal notaMinima) {

        if (ingresante.getPrelamolina() != null) {
            if (ingresante.getPrelamolina().getPuntajeRm().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeRv().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeMatematicas().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeFisica().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeQuimica().compareTo(notaMinima) >= 0
                    && ingresante.getPrelamolina().getPuntajeBiologia().compareTo(notaMinima) >= 0) {

                bloqueoIngresante.setMatricula(Boolean.TRUE);

            }
        }
        if (ingresante.getEvaluado() != null) {
            if (ingresante.getEvaluado().getPuntajeRm().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeRv().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeMatematicas().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeFisica().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeQuimica().compareTo(notaMinima) >= 0
                    && ingresante.getEvaluado().getPuntajeBiologia().compareTo(notaMinima) >= 0) {

                bloqueoIngresante.setMatricula(Boolean.TRUE);

            }

        }

    }

    @Override
    @Transactional
    public void inscripcion(DataSessionAdmision ds) {
        Ingresante ingresante = this.findIngresante(ds);

        MatriculaBloqueoIngresante ingresanteBloqueo = matriculaBloqueoIngresanteDAO.findByCicloAcademicoIngresante(ds.getCicloPostula().getCicloAcademico(), ingresante);
        ingresanteBloqueo.setInscrito(Boolean.TRUE);
        matriculaBloqueoIngresanteDAO.update(ingresanteBloqueo);
    }

}
