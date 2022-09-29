package edu.pe.lamolina.admision.controller.admision.fichasocioeconomica;

import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.dao.academico.ActividadIngresanteDAO;
import edu.pe.lamolina.admision.dao.academico.AlumnoCicloDAO;
import edu.pe.lamolina.admision.dao.academico.AlumnoDAO;
import edu.pe.lamolina.admision.dao.academico.CicloAcademicoDAO;
import edu.pe.lamolina.admision.dao.academico.MatriculaResumenDAO;
import edu.pe.lamolina.admision.dao.academico.RecorridoIngresanteDAO;
import edu.pe.lamolina.admision.dao.academico.SituacionAcademicaDAO;
import edu.pe.lamolina.admision.dao.academico.TipoActividadIngresanteDAO;
import edu.pe.lamolina.admision.dao.finanzas.AcreenciaDAO;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.general.ContenidoCartaDAO;
import edu.pe.lamolina.admision.dao.general.OficinaDAO;
import edu.pe.lamolina.admision.dao.general.ParametroDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.DocumentoModalidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.IngresanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDocumentoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PrelamolinaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TurnoEntrevistaObuaeDAO;
import edu.pe.lamolina.admision.dao.medico.ConceptoExamenMedicoDAO;
import edu.pe.lamolina.admision.dao.medico.ConceptoMedicoCicloDAO;
import edu.pe.lamolina.admision.dao.medico.ExamenMedicoDAO;
import edu.pe.lamolina.admision.dao.seguridad.RolDAO;
import edu.pe.lamolina.admision.dao.seguridad.SistemaDAO;
import edu.pe.lamolina.admision.dao.seguridad.TokenIngresanteDAO;
import edu.pe.lamolina.admision.dao.seguridad.UsuarioDAO;
import edu.pe.lamolina.admision.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.mail.MailerService;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.AlumnoEstadoEnum;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.ExamenMedicoEstadoEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.OrigenTokenEnum;
import pe.edu.lamolina.model.enums.PagoMedicoEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.enums.RecorridoIngresanteEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_8;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.DocumentoModalidad;
import pe.edu.lamolina.model.inscripcion.Ingresante;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.PostulanteDocumento;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.ConceptoExamenMedico;
import pe.edu.lamolina.model.medico.ConceptoMedicoCiclo;
import pe.edu.lamolina.model.medico.ExamenMedico;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Sistema;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;

@Service
@Transactional(readOnly = true)
public class DatosGeneralesServiceImp implements DatosGeneralesService {

    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    CicloPostulaDAO cicloPostulaDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    ModalidadIngresoDAO modalidadIngresoDAO;
    @Autowired
    PrelamolinaDAO prelamolinaDAO;
    @Autowired
    IngresanteDAO ingresanteDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    @Autowired
    TipoActividadIngresanteDAO tipoActividadIngresanteDAO;
    @Autowired
    UsuarioDAO usuarioDAO;
    @Autowired
    ExamenMedicoDAO examenMedicoDAO;
    @Autowired
    ConceptoExamenMedicoDAO conceptoExamenMedicoDAO;
    @Autowired
    ConceptoMedicoCicloDAO conceptoMedicoCicloDAO;
    @Autowired
    OficinaDAO oficinaDAO;
    @Autowired
    RecorridoIngresanteDAO recorridoIngresanteDAO;
    @Autowired
    TurnoEntrevistaObuaeDAO turnoEntrevistaObuaeDAO;
    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;
    @Autowired
    ActividadIngresanteDAO actividadIngresanteDAO;
    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;
    @Autowired
    AcreenciaDAO acreenciaDAO;
    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;
    @Autowired
    ParametroDAO parametroDAO;
    @Autowired
    SistemaDAO sistemaDAO;
    @Autowired
    RolDAO rolDAO;
    @Autowired
    UsuarioRolDAO usuarioRolDAO;
    @Autowired
    DocumentoModalidadDAO documentoModalidadDAO;
    @Autowired
    PostulanteDocumentoDAO postulanteDocumentoDAO;

    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;

    @Autowired
    DespliegueConfig despliegueConfig;
    @Autowired
    MailerService mailerService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void asignarMatricula(Long idPostulante, DataSessionAdmision ds, HttpSession session) {
        CicloPostula cicloPostula = cicloPostulaDAO.find(ds.getCicloPostula().getId());
        Postulante postulante = postulanteDAO.find(idPostulante);
        settingDocumentos(postulante);

        SituacionAcademica sitIngresante = situacionAcademicaDAO.findByCodigo(S_8.getValue());
        Prelamolina cepre = prelamolinaDAO.findByPostulante(postulante);

        Ingresante ingresante = ingresanteDAO.findByPostulante(postulante);

        if (ingresante == null) {
            ingresante = new Ingresante();
            ingresante.setPostulante(postulante);
            ingresante.setPrelamolina(cepre);
            ingresante.setCarrera(cepre.getCarreraPostula().getCarrera());
            ingresanteDAO.save(ingresante);
        }

        CicloAcademico cicloAcad = cicloPostula.getCicloAcademico();
        List<Alumno> alumnos = alumnoDAO.allIngresanteTemporalByCiclo(cicloAcad);

        Long inicio = alumnos.isEmpty() ? 100L : Long.parseLong(alumnos.get(0).getCodigo().substring(1)) + 1;
        String codigo = "P" + inicio;
        Alumno alumno = alumnoDAO.findByPostulante(postulante);

        if (alumno == null) {
            alumno = new Alumno();
            alumno.setCarrera(ingresante.getCarrera());
            alumno.setCicloActivo(cicloAcad);
            alumno.setCicloIngreso(cicloAcad);
            alumno.setEstadoEnum(AlumnoEstadoEnum.ACT);
            alumno.setModalidadEstudio(cicloAcad.getModalidadEstudio());
            alumno.setPersona(postulante.getPersona());
            alumno.setPostulantePregrado(postulante);
            alumno.setSituacionAcademica(sitIngresante);

            alumno.setPromedioAcumulado(BigDecimal.ZERO);
            alumno.setPromedioCarreraAcumulado(BigDecimal.ZERO);
            alumno.setCodigo(codigo);

            alumno.setRetirosCiclos(0);
            alumno.setRetirosCursos(0);
            alumno.setRetirosExtemporaneos(0);

            alumno.setCreditosAprobados(0);
            alumno.setCreditosCarreraAprobados(0);
            alumno.setCreditosCarreraCursados(0);
            alumno.setCreditosCursados(0);

            alumno.setCursosAprobados(0);
            alumno.setCursosCarreraAprobados(0);
            alumno.setCursosCarreraInscritos(0);
            alumno.setCursosInscritos(0);
            alumno.setCiclosEstudiados(0);
            alumnoDAO.save(alumno);
        }

        Usuario usuario = usuarioDAO.findByPersona(postulante.getPersona());
        Rol rol = rolDAO.findCodigoEnum(RolEnum.ALU);
        if (usuario == null) {
            usuario = new Usuario();
            usuario.setEstadoEnum(UserEstadoEnum.ACT);
            usuario.setFechaRegistro(new Date());
            usuario.setPersona(postulante.getPersona());
            usuario.setFacebook(postulante.getInteresado().getFacebook());
            usuarioDAO.save(usuario);

            UsuarioRol usuarioRol = new UsuarioRol();
            usuarioRol.setEstadoEnum(UserEstadoEnum.ACT);
            usuarioRol.setFechaInicio(new Date());
            usuarioRol.setFechaRegistro(new Date());
            usuarioRol.setRol(rol);
            usuarioRol.setUsuario(usuario);
            usuarioRol.setUserRegistro(usuario);
            usuarioRolDAO.save(usuarioRol);

        } else {
            boolean salvar = false;
            UsuarioRol usuarioRol = usuarioRolDAO.findByUserRol(usuario, rol);
            if (usuarioRol == null) {
                usuarioRol = new UsuarioRol();
                salvar = true;
            }

            usuarioRol.setEstadoEnum(UserEstadoEnum.ACT);
            if (salvar) {
                usuarioRol.setFechaInicio(new Date());
                usuarioRol.setFechaRegistro(new Date());
                usuarioRol.setRol(rol);
                usuarioRol.setUsuario(usuario);
                usuarioRol.setUserRegistro(usuario);
                usuarioRolDAO.save(usuarioRol);
            } else {
                usuarioRolDAO.update(usuarioRol);
            }
        }

        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcad);

        if (alumnoCiclo == null) {
            alumnoCiclo = new AlumnoCiclo();
            alumnoCiclo.setAlumno(alumno);
            alumnoCiclo.setCarrera(ingresante.getCarrera());
            alumnoCiclo.setCicloAcademico(cicloAcad);
            alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.NMAT);

            alumnoCiclo.setCreditosAcumulados(0);
            alumnoCiclo.setCreditosAprobadosAcumulados(0);
            alumnoCiclo.setCreditosAprobadosCiclo(0);
            alumnoCiclo.setCreditosCursadosCiclo(0);
            alumnoCiclo.setCreditosConvalidados(0);
            alumnoCiclo.setCursosAprobados(0);
            alumnoCiclo.setCursosInscritos(0);
            alumnoCiclo.setPromedioCiclo(BigDecimal.ZERO);
            alumnoCiclo.setPromedioAcumulado(BigDecimal.ZERO);
            alumnoCiclo.setSituacionInicio(sitIngresante);

            alumnoCiclo.setUserRegistro(usuario);
            alumnoCiclo.setFechaRegistro(new Date());
            alumnoCiclo.setEstaAprobado(0);
            alumnoCicloDAO.save(alumnoCiclo);

            MatriculaResumen matriResumen = new MatriculaResumen();
            matriResumen.setAlumno(alumno);
            matriResumen.setCicloAcademico(cicloAcad);
            matriResumen.setEstado(EstadoMatriculaEnum.NMAT.name());
            matriResumen.setCreditosMatriculados(0);
            matriResumen.setCreditosRetirados(0);
            matriResumen.setCursosMatriculados(0);
            matriResumen.setCursosRetirados(0);
            matriResumen.setCreditosTrikaPagados(0);
            matriResumen.setCreditosTrikaSeparados(0);
            matriResumen.setNotaAcumulada("0");
            matriResumen.setNotaAvance("0");
            matriResumen.setNotaFinal("0");
            matriResumen.setPorcentajeAvance(0);
            matriResumen.setSituacionInicio(sitIngresante);
            matriResumen.setCreditosTrikaPagados(0);
            matriculaResumenDAO.save(matriResumen);
        }

        ingresante.setCodigo(codigo);
        ingresanteDAO.update(ingresante);

        List<TipoActividadIngresante> tiposActividades = tipoActividadIngresanteDAO.all();
        List<ConceptoExamenMedico> pagos = new ArrayList();
        this.generarPagoMedico(alumno, cicloAcad, ds, alumnos, pagos, usuario);
        this.generarHojaRecorrido(alumno, cicloAcad, ds, alumnos, tiposActividades, pagos, usuario);
        ds.setAlumno(alumno);
        ds.setPersona(alumno.getPersona());
        ds.setUsuario(usuario);

        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

    }

    private void settingDocumentos(Postulante postulante) {
        List<DocumentoModalidad> documentosModalidad = documentoModalidadDAO.allRequistosByPostulante(postulante);
        List<PostulanteDocumento> documentosPostulante = postulanteDocumentoDAO.allByPostulante(postulante);
        postulante.setDocumentosCompletos(documentosModalidad.size());
        postulante.setDocumentosRecibidos(documentosPostulante.size());
        postulanteDAO.update(postulante);

    }

    private void generarPagoMedico(Alumno alumno, CicloAcademico ciclo, DataSessionAdmision ds, List<Alumno> alumnos, List<ConceptoExamenMedico> pagos, Usuario usuario) {
        List<ExamenMedico> examenes = examenMedicoDAO.allByCiclo(ciclo);
        ConceptoMedicoCiclo conceptoMedico = conceptoMedicoCicloDAO.findByCicloCodigoEnum(ciclo, PagoMedicoEnum.EXAMED);
        Assert.isNotNull(conceptoMedico, "No se ha configurado el Monto del Examen Médico");
        Oficina obuae = oficinaDAO.findByCodigo(OficinaEnum.OBUAE.name());

        ExamenMedico examen = examenes
                .stream()
                .filter(em -> em.getAlumno().getId() == alumno.getId())
                .findAny()
                .orElse(null);

        this.registrarExamenMedico(alumno, examen, conceptoMedico, ciclo, obuae, ds, pagos, usuario);
    }

    private void registrarExamenMedico(
            Alumno alumno,
            ExamenMedico examen,
            ConceptoMedicoCiclo conceptoMedicoCiclo,
            CicloAcademico academico,
            Oficina obuae,
            DataSessionAdmision ds,
            List<ConceptoExamenMedico> pagos, Usuario usuario) {

        if (examen != null) {
            return;
        }

        examen = new ExamenMedico();
        examen.setAlumno(alumno);
        examen.setAbonoTotal(BigDecimal.ZERO);
        examen.setMontoTotal(conceptoMedicoCiclo.getMonto());
        examen.setCicloAcademico(academico);
        examen.setFechaRegistro(new Date());
        examen.setUserRegistro(usuario);
        examen.setEstadoEnum(ExamenMedicoEstadoEnum.PEND);
        examenMedicoDAO.save(examen);

        ConceptoExamenMedico conceptoExamMedico = new ConceptoExamenMedico();
        conceptoExamMedico.setAbono(BigDecimal.ZERO);
        conceptoExamMedico.setConceptoMedicoCiclo(conceptoMedicoCiclo);
        conceptoExamMedico.setExamenMedico(examen);
        conceptoExamMedico.setFechaRegistro(new Date());
        conceptoExamMedico.setMonto(conceptoMedicoCiclo.getMonto());
        conceptoExamMedico.setUserRegistro(usuario);
        conceptoExamenMedicoDAO.save(conceptoExamMedico);
        pagos.add(conceptoExamMedico);

        DateTime today = new DateTime();
        Acreencia acreencia = new Acreencia();
        acreencia.setCuentaBancaria(conceptoMedicoCiclo.getConceptoMedico().getCuentaBancaria());
        acreencia.setDescripcion(conceptoMedicoCiclo.getConceptoMedico().getNombre());
        acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);
        acreencia.setFechaDocumento(today.toDate());
        acreencia.setFechaVencimiento(today.plusDays(30).toDate());
        acreencia.setMonto(conceptoMedicoCiclo.getMonto());
        acreencia.setOficina(obuae); 
        acreencia.setAbono(BigDecimal.ZERO);
        acreencia.setFechaRegistro(today.toDate());
        acreencia.setTablaEnum(NombreTablasEnum.MED_EXAMEN_MEDICO);
        acreencia.setInstanciaTabla(examen.getId());
        acreencia.setPersona(alumno.getPersona());
        acreenciaDAO.save(acreencia);

    }

    private void generarHojaRecorrido(
            Alumno alumno,
            CicloAcademico ciclo, DataSessionAdmision ds,
            List<Alumno> alumnos,
            List<TipoActividadIngresante> tiposActividades,
            List<ConceptoExamenMedico> pagos, Usuario usuario) {

        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allByCiclo(ciclo);
        List<TurnoEntrevistaObuae> turnos = turnoEntrevistaObuaeDAO.allByCicloAcademico(ciclo);
        List<ModalidadIngresoCiclo> modalidadesCiclo = modalidadIngresoCicloDAO.allByCicloAcademico(ciclo);
        Map<Long, ModalidadIngresoCiclo> mapModalidadCiclo = TypesUtil.convertListToMap("modalidadIngreso.id", modalidadesCiclo);

        Integer atencion = lastNumeroAtencion(recorridos);

        Map<Long, ConceptoExamenMedico> pagosMap = TypesUtil.convertListToMap("examenMedico.alumno.id", pagos);

        logger.debug("********************** inicia generarHojaRecorrido  {}", alumnos.size());

        RecorridoIngresante recorrido = recorridos
                .stream()
                .filter(em -> em.getAlumno().getId() == alumno.getId())
                .findAny()
                .orElse(null);

        this.registrarRecorrido(alumno, recorrido, ciclo, tiposActividades, atencion, turnos, mapModalidadCiclo, ds, pagosMap, usuario);

    }

    private RecorridoIngresante registrarRecorrido(
            Alumno alumno,
            RecorridoIngresante recorrido,
            CicloAcademico ciclo,
            List<TipoActividadIngresante> tiposActividades,
            Integer nroAtencion,
            List<TurnoEntrevistaObuae> turnos,
            Map<Long, ModalidadIngresoCiclo> mapModalidadCiclo,
            DataSessionAdmision ds,
            Map<Long, ConceptoExamenMedico> pagosMap, Usuario usuario) {

        logger.debug(alumno.getCodigo() + " :::: " + alumno.getPostulantePregrado().getId() + " " + alumno.getPersona().getNombreCompleto());

        if (recorrido != null) {
            logger.debug("\tSin recorrido {}", alumno.getCodigo());
            return recorrido;
        }
        logger.debug("\tCon recorrido {}", alumno.getCodigo());

        recorrido = new RecorridoIngresante();
        recorrido.setActividadesEjecutadas(0);
        recorrido.setActividadesPostergadas(0);
        recorrido.setAlumno(alumno);
        recorrido.setCicloAcademico(ciclo);
        recorrido.setEstadoEnum(RecorridoIngresanteEstadoEnum.PEND);
        recorrido.setNumeroAtencion(0);
        recorrido.setTotalActividades(0);

        Map<TipoActividadIngresanteEnum, TipoActividadIngresante> mapTipoActividad = TypesUtil.convertListToMap("codigoEnum", tiposActividades);

        Postulante postulante = alumno.getPostulantePregrado();
        ObjectUtil.printAttr(postulante);
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        ModalidadIngresoCiclo modalidadCiclo = mapModalidadCiclo.get(modalidad.getId());

        int pasos = 0;
        ActividadIngresante actividad1 = null;
        ActividadIngresante actividad2 = null;
        ActividadIngresante actividad3 = null;

        if (modalidadCiclo.getRindeExamenAdmision() == 0) {
            TipoActividadIngresante tipoAct = mapTipoActividad.get(TipoActividadIngresanteEnum.CAREO);
            actividad1 = new ActividadIngresante();
            actividad1.setRecorridoIngresante(recorrido);
            actividad1.setEstadoEnum(RecorridoIngresanteEstadoEnum.ACT);
            actividad1.setUserEjecucion(usuario);
            actividad1.setUserRegistro(usuario);
            actividad1.setTipoActividadIngresante(tipoAct);
            actividad1.setFechaEjecucion(new Date());
            actividad1.setFechaRegistro(new Date());

            pasos++;
            logger.debug("\tSin necesidad de careo");
        }

        if (postulante.getDocumentosRecibidos() == 0) {
        } else {
            TipoActividadIngresante tipoAct = mapTipoActividad.get(TipoActividadIngresanteEnum.DOCS);
            actividad2 = new ActividadIngresante();
            actividad2.setRecorridoIngresante(recorrido);
            actividad2.setUserEjecucion(usuario);
            actividad2.setUserRegistro(usuario);
            actividad2.setTipoActividadIngresante(tipoAct);
            actividad2.setFechaEjecucion(new Date());
            actividad2.setFechaRegistro(new Date());
            actividad2.setEstadoEnum(RecorridoIngresanteEstadoEnum.INC);

            logger.debug("doc-completos: {}", postulante.getDocumentosCompletos());
            logger.debug("doc-recibidos: {}", postulante.getDocumentosRecibidos());

            if (postulante.getDocumentosCompletos().compareTo(postulante.getDocumentosRecibidos()) == 0) {
                actividad2.setEstadoEnum(RecorridoIngresanteEstadoEnum.ACT);
                pasos++;
            }
            System.out.println("\tCon documentos " + actividad2.getEstadoEnum().name());
        }

        List<DeudaInteresado> deudasAdmision = deudaInteresadoDAO.allByInteresado(postulante.getInteresado());
        Boolean deudasPagadas = true;
        for (DeudaInteresado deudaInteresado : deudasAdmision) {
            deudasPagadas = deudasPagadas && deudaInteresado.getEstadoEnum() != DeudaEstadoEnum.ACT;
            if (!deudasPagadas) {
                break;
            }
        }

        if (deudasPagadas) {

            TipoActividadIngresante tipoAct = mapTipoActividad.get(TipoActividadIngresanteEnum.RPAGOADM);
            actividad3 = new ActividadIngresante();
            actividad3.setRecorridoIngresante(recorrido);
            actividad3.setUserEjecucion(usuario);
            actividad3.setUserRegistro(usuario);
            actividad3.setTipoActividadIngresante(tipoAct);
            actividad3.setFechaEjecucion(new Date());
            actividad3.setFechaRegistro(new Date());
            actividad3.setEstadoEnum(RecorridoIngresanteEstadoEnum.ACT);
            pasos++;
        }

        if (pasos == 3) {
            System.out.println("\tCon turno atencion");
            TurnoEntrevistaObuae turno = findTurnoByNroAtencion(turnos, nroAtencion);
            recorrido.setTurnoEntrevistaObuae(turno);
            recorrido.setNumeroAtencion(nroAtencion);
            recorrido.setEstadoEnum(RecorridoIngresanteEstadoEnum.PROC);

        }

        recorridoIngresanteDAO.save(recorrido);
        if (actividad1 != null) {
            actividadIngresanteDAO.save(actividad1);
        }
        if (actividad2 != null) {
            actividadIngresanteDAO.save(actividad2);
        }
        if (actividad3 != null) {
            actividadIngresanteDAO.save(actividad3);
        }

        logger.debug("********************** envio email recorrido  {}", recorrido.getId());

        this.sendEmailRecorridoBienvenidoIngresante(recorrido, turnos);
        if (pasos == 3) {
            this.sendEmailRecorridoPersonalizadoIngresante(recorrido, pagosMap);
        }
        return recorrido;
    }

    private void sendEmailRecorridoBienvenidoIngresante(RecorridoIngresante recorrido, List<TurnoEntrevistaObuae> turnos) {
        Persona persona = recorrido.getAlumno().getPersona();

        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigo(
                ContenidoCartaEnum.RECORRIDO_BIENVENIDO_INGRESANTE.name(),
                new Sistema(5));

        mailerService.enviarEmailRecorridoBienvenidoIngresante(persona, contenidoCarta, turnos);
    }

    private void sendEmailRecorridoPersonalizadoIngresante(RecorridoIngresante recorrido, Map<Long, ConceptoExamenMedico> pagosMap) {
        Persona persona = recorrido.getAlumno().getPersona();

        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigo(
                ContenidoCartaEnum.RECORRIDO_PERSONALIZADO_INGRESANTE.name(),
                new Sistema(5));

        ConceptoExamenMedico pago = pagosMap.get(recorrido.getAlumno().getId());
        if (pago == null) {
            logger.info("alumno {} sin concepto examen medico generado ", recorrido.getAlumno().getCodigo());
            return;
        }
        mailerService.enviarEmailRecorridoPersonalizadoIngresante(persona, contenidoCarta, recorrido, pago);
    }

    private TurnoEntrevistaObuae findTurnoByNroAtencion(List<TurnoEntrevistaObuae> turnos, Integer numeroAtencion) {
        for (TurnoEntrevistaObuae turno : turnos) {
            int inicio = Integer.valueOf(turno.getTurnoInicio());
            int fin = Integer.valueOf(turno.getTurnoFin());
            if (numeroAtencion >= inicio && numeroAtencion <= fin) {
                return turno;
            }
        }
        return null;
    }

    private Integer lastNumeroAtencion(List<RecorridoIngresante> recorridos) {
        Integer atencion = 0;
        for (RecorridoIngresante recorrido : recorridos) {
            if (recorrido.getNumeroAtencion() != null) {
                if (atencion < recorrido.getNumeroAtencion().intValue()) {
                    atencion = recorrido.getNumeroAtencion();
                }
            }
        }
        atencion++;
        return atencion;
    }

    @Override
    @Transactional
    public String goMaipi(Alumno alumno, Usuario usuario) {

        String valor = RandomStringUtils.randomAlphanumeric(45);
        TokenIngresante token = new TokenIngresante();
        token.setOrigenEnum(OrigenTokenEnum.ADMISION);
        token.setEstadoEnum(TokenEstadoEnum.ACT);
        token.setFechaRegistro(new Date());
        token.setFechaVencimiento(new DateTime().plusSeconds(10).toDate());
        token.setPersona(alumno.getPersona());
        token.setValor(valor);
        token.setUserRegistro(usuario);
        tokenIngresanteDAO.save(token);
        return alumno.getCodigo();
    }

    @Override
    public Parametro findParametroByEnum(ParametrosSistemasEnum parametrosSistemasEnum) {
        Sistema sistema = sistemaDAO.find(despliegueConfig.getSistema());
        logger.debug("********************** sistema {}", sistema.getId());
        AmbienteAplicacionEnum ambiente = AmbienteAplicacionEnum.valueOf(despliegueConfig.getAmbiente().toUpperCase());
        logger.debug("********************** ambiente name {}", ambiente.name());
        Parametro paramRutaIntranet = parametroDAO.findBySistemaAmbienteParametrosSistemas(sistema, ambiente, parametrosSistemasEnum);
        logger.debug("********************** paramRutaMatricula {} path {}", paramRutaIntranet.getId(), paramRutaIntranet.getValor());
        return paramRutaIntranet;
    }
}
