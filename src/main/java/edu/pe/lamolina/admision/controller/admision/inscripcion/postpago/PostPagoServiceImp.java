package edu.pe.lamolina.admision.controller.admision.inscripcion.postpago;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.controller.admision.evaluacion.EvaluacionService;
import edu.pe.lamolina.admision.dao.academico.AlumnoDAO;
import edu.pe.lamolina.admision.dao.academico.CicloAcademicoDAO;
import edu.pe.lamolina.admision.dao.academico.EventoCicloAcademicoDAO;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.academico.RecorridoIngresanteDAO;
import edu.pe.lamolina.admision.dao.examen.EncuestaPostulanteDAO;
import edu.pe.lamolina.admision.dao.examen.ExamenInteresadoDAO;
import edu.pe.lamolina.admision.dao.examen.hibernate.EncuestaCicloDAOH;
import edu.pe.lamolina.admision.dao.finanzas.DeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.finanzas.ItemDeudaInteresadoDAO;
import edu.pe.lamolina.admision.dao.general.ColegioDAO;
import edu.pe.lamolina.admision.dao.general.ContenidoCartaDAO;
import edu.pe.lamolina.admision.dao.general.UniversidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.IngresanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.OpcionCarreraDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PrelamolinaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.SolicitudCambioCarreraDAO;
import edu.pe.lamolina.admision.dao.inscripcion.SolicitudCambioInfoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TurnoEntrevistaObuaeDAO;
import edu.pe.lamolina.admision.dao.seguridad.TokenIngresanteDAO;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import static pe.edu.lamolina.model.enums.DeudaEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.DeudaEstadoEnum.PAG;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OrigenTokenEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.enums.SolicitudCambioInfoEstadoEnum;
import pe.edu.lamolina.model.enums.TipoCicloEnum;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.finanzas.ItemDeudaInteresado;
import pe.edu.lamolina.model.finanzas.SolicitudCambioCarrera;
import pe.edu.lamolina.model.finanzas.SolicitudCambioInfo;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Ingresante;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.seguridad.TokenIngresante;

@Service
@Transactional(readOnly = true)
public class PostPagoServiceImp implements PostPagoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DeudaInteresadoDAO deudaInteresadoDAO;
    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    ExamenInteresadoDAO examenInteresadoDAO;
    @Autowired
    IngresanteDAO ingresanteDAO;
    @Autowired
    PrelamolinaDAO prelamolinaDAO;
    @Autowired
    ColegioDAO colegioDAO;
    @Autowired
    UniversidadDAO universidadDAO;
    @Autowired
    TurnoEntrevistaObuaeDAO turnoEntrevistaObuaeDAO;
    @Autowired
    OpcionCarreraDAO opcionCarreraDAO;
    @Autowired
    EventoDAO eventoDAO;
    @Autowired
    EventoCicloDAO eventoCicloDAO;
    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;
    @Autowired
    EncuestaPostulanteDAO encuestaPostulanteDAO;
    @Autowired
    EncuestaCicloDAOH encuestaCicloDAOH;
    @Autowired
    EvaluacionService evaluacionService;
    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;
    @Autowired
    CicloPostulaDAO cicloPostulaDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    @Autowired
    TokenIngresanteDAO tokenIngresanteDAO;
    @Autowired
    SolicitudCambioInfoDAO solicitudCambioInfoDAO;
    @Autowired
    SolicitudCambioCarreraDAO solicitudCambioCarreraDAO;
    @Autowired
    ItemDeudaInteresadoDAO itemDeudaInteresadoDAO;
    @Autowired
    RecorridoIngresanteDAO recorridoIngresanteDAO;

    @Override
    public CicloPostula findCiclo(CicloPostula ciclo) {
        return cicloPostulaDAO.find(ciclo.getId());
    }

    @Override
    public Postulante findPostulante(Postulante postulanteSession) {

        Postulante postulante = postulanteDAO.find(postulanteSession.getId());
        List<ModalidadIngresoCiclo> modalidadesCiclo = modalidadIngresoCicloDAO.allByCiclo(postulante.getCicloPostula());

        Map<Long, ModalidadIngresoCiclo> mapModalidadC = TypesUtil.convertListToMap("modalidadIngreso.id", modalidadesCiclo);

        if (postulante.getModalidadIngreso() != null) {
            ModalidadIngresoCiclo modaing = mapModalidadC.get(postulante.getModalidadIngreso().getId());
            postulante.setModalidadIngresoCiclo(modaing);
        }

        return postulante;
    }

    @Override
    @Transactional
    public void postulanteFinalizar(Postulante postulanteSession, CicloPostula ciclo) {
        Postulante postulante = postulanteDAO.find(postulanteSession.getId());

        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        if (!modalidad.isPreLaMolina()) {
            postulante.setEstado(PostulanteEstadoEnum.INS);
            postulante.setFechaInscripcion(new Date());
            postulanteDAO.update(postulante);
            return;
        }

        postulante.setEstado(PostulanteEstadoEnum.ING);
        postulante.setFechaIngreso(new Date());
        postulanteDAO.update(postulante);

        Prelamolina cepre = prelamolinaDAO.findByPostulante(postulante);

        Ingresante ingresante = ingresanteDAO.findByPostulante(postulante);
        if (ingresante != null) {
            return;
        }

        ingresante.setPostulante(postulante);
        ingresante.setPrelamolina(cepre);
        ingresante.setCarrera(cepre.getCarreraPostula().getCarrera());
        ingresanteDAO.save(ingresante);

    }

    @Override
    public Boolean verificarPagoGuiaPostulante(Postulante postulante) {
        Interesado interesado = postulante.getInteresado();

        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstados(interesado, Arrays.asList(ACT, PAG));
        List<DeudaInteresado> deudasPagadas = deudas.stream().filter(x -> x.isCancelada()).collect(Collectors.toList());
        List<ItemDeudaInteresado> items = itemDeudaInteresadoDAO.allByDeudasInteresadoEstados(deudasPagadas, Arrays.asList(ACT, PAG));
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        boolean verificarPagoGuia = (modalidad == null) ? true : (modalidad.isPreLaMolina() ? false : true);

        if (verificarPagoGuia) {
            for (ItemDeudaInteresado item : items) {
                if (item.getConceptoPrecio() == null) {
                    continue;
                }
                ConceptoPago cpto = item.getConceptoPrecio().getConceptoPago();
                if (cpto.getCodigo().equals(AdmisionConstantine.CODE_PROSPECTO)) {
                    return true;
                }
            }
        } else {
            for (ItemDeudaInteresado item : items) {
                if (item.getConceptoPrecio() == null) {
                    continue;
                }
                ConceptoPago cpto = item.getConceptoPrecio().getConceptoPago();
                ModalidadIngreso modalidadCpto = cpto.getModalidadIngreso();
                if (modalidadCpto == null) {
                    continue;
                }
                if (modalidad.getId() == modalidadCpto.getId().longValue()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Ingresante findIngresanteByPostulante(Postulante postulanteSession) {
        logger.debug("POSTULANTE ID {}", postulanteSession.getId());
        Postulante postulanteBD = postulanteDAO.find(postulanteSession.getId());
        Ingresante ingresante = ingresanteDAO.findByPostulante(postulanteBD);
        logger.debug("INGRESANTE ID {}", ingresante.getId());
        ingresante.setPostulante(postulanteBD);

        Colegio colegio = postulanteBD.getColegioProcedencia();
        if (colegio != null) {
            colegio = colegioDAO.find(colegio.getId());
            postulanteBD.setColegioProcedencia(colegio);
        }

        Universidad uni = postulanteBD.getUniversidadProcedencia();
        if (uni != null) {
            uni = universidadDAO.find(uni.getId());
            postulanteBD.setUniversidadProcedencia(uni);
        }

        return ingresante;
    }

    @Override
    public TurnoEntrevistaObuae findTurnoPostulante(Postulante postulante, CicloPostula ciclo) {
        ModalidadIngreso modalidad = postulante.getModalidadIngreso();
        String grupo = modalidad.isPreLaMolina()
                ? AdmisionConstantine.ENTREVISTA_OBUAE_CEPRE
                : AdmisionConstantine.ENTREVISTA_OBUAE_OTROS;

        if (postulante.getOrdenAtencion() == null) {
            return null;
        }

        return turnoEntrevistaObuaeDAO.findByOrdenGrupoCiclo(postulante.getOrdenAtencion(), grupo, ciclo);
    }

    @Override
    public List<OpcionCarrera> allOpcionCarreraByPostulante(Postulante postulante) {
        List<OpcionCarrera> opciones = opcionCarreraDAO.allByPostulante(postulante);
        return opciones;
    }

    @Override
    public EventoCiclo getEventoAula(CicloPostula ciclo) {
        Evento evento = eventoDAO.findByCode("AULA");
        List<EventoCiclo> examenes = eventoCicloDAO.allByEventoCiclo(evento, ciclo);
        if (!examenes.isEmpty()) {
            return examenes.get(0);
        }
        return null;

    }

    @Override
    public ModalidadIngresoCiclo findModalidadCiclo(ModalidadIngreso modalidadIngreso, CicloPostula ciclo) {
        return modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(modalidadIngreso, ciclo);
    }

    @Override
    public ContenidoCarta findContenidoCartaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum) {
        return contenidoCartaDAO.findByCodigoEnum(contenidoCartaEnum);
    }

    @Override
    @Transactional
    public TokenIngresante findToken(Postulante postulante, CicloPostula ciclo) {
        Alumno alumno = alumnoDAO.findByPostulante(postulante);

        if (alumno == null) {
            throw new PhobosException("Alumno no existente");
        }

        SituacionAcademica situacion = alumno.getSituacionAcademica();
        if (!Arrays.asList("8", "9").contains(situacion.getCodigo())) {
            throw new PhobosException("Situacion Diferente a 8 y 9");
        }

        CicloAcademico cicloAcademico = cicloAcademicoDAO.find(ciclo.getCicloAcademico().getId());
        TokenIngresante token = tokenIngresanteDAO.findUltimoVigente(postulante.getPersona());

        if (token == null) {
            token = new TokenIngresante();
            token.setOrigenEnum(OrigenTokenEnum.ADMISION);
            token.setEstadoEnum(TokenEstadoEnum.ACT);
            token.setFechaRegistro(new Date());
            token.setFechaVencimiento(new DateTime().plusMinutes(10).toDate());
            token.setPersona(postulante.getPersona());

            String valor = RandomStringUtils.randomAlphanumeric(45);
            token.setValor(valor);
            tokenIngresanteDAO.save(token);

            logger.debug("Nuevo token generado :: {}", valor);
        }

        return token;
    }

    @Override
    public TokenIngresante findByPersona(Persona persona) {
        return tokenIngresanteDAO.findUltimoVigente(persona);
    }

    @Override
    public List<SolicitudCambioInfo> allSolicitudCambioInfoByPostulante(Postulante postulante, List<SolicitudCambioInfoEstadoEnum> estados) {
        return solicitudCambioInfoDAO.allByPostulanteEstados(postulante, estados);
    }

    @Override
    public List<SolicitudCambioCarrera> allSolicitudCambioCarreraBySolicitud(SolicitudCambioInfo carreras) {
        if (carreras == null) {
            return new ArrayList();
        }
        List<SolicitudCambioInfo> solicitudes = new ArrayList();
        solicitudes.add(carreras);
        return solicitudCambioCarreraDAO.allBySolicitudes(solicitudes);
    }

    @Override
    public List<DeudaInteresado> allDeudasByPostulante(Postulante postulante, CicloPostula ciclo) {
        List<DeudaInteresado> deudas = deudaInteresadoDAO.allByInteresadoEstado(postulante.getInteresado(), DeudaEstadoEnum.ACT);
        List<DeudaInteresado> deudasNoPagadas = deudas.stream()
                .filter(deuda -> !deuda.isCancelada())
                .collect(Collectors.toList());

        if (deudasNoPagadas.isEmpty()) {
            return deudasNoPagadas;
        }

        List<ItemDeudaInteresado> items = itemDeudaInteresadoDAO.allByDeudasInteresadoEstados(deudasNoPagadas, Arrays.asList(DeudaEstadoEnum.ACT));
        Map<Long, List<ItemDeudaInteresado>> mapItems = TypesUtil.convertListToMapList("deudaInteresado.id", items);
        for (DeudaInteresado deuda : deudas) {
            List<ItemDeudaInteresado> mapItem = mapItems.get(deuda.getId());
            if (mapItem == null) {
                deuda.setItemDeudaInteresado(new ArrayList());
                continue;
            }
            deuda.setItemDeudaInteresado(mapItem);
        }

        return deudasNoPagadas;
    }

    @Override
    public RecorridoIngresante findRecorridoIngresante(Postulante postulante, CicloPostula cicloPostula) {
        Alumno alumno = alumnoDAO.findByPostulante(postulante);
        if (alumno == null) {
            return null;
        }
        RecorridoIngresante recorridoIngresante = recorridoIngresanteDAO.findAlumnoAndCiclo(alumno, cicloPostula.getCicloAcademico());
        return recorridoIngresante;
    }

}
