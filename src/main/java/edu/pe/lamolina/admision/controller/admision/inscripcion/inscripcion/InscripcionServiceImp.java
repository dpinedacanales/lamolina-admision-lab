package edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import edu.pe.lamolina.admision.dao.academico.CarreraDAO;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.general.ContenidoCartaDAO;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.general.TipoDocIdentidadDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CarreraNuevaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InscritoTallerDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.OpcionCarreraDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PrelamolinaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.TallerDAO;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.Random;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.BG_COLORS;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.EstadoInscritoEnum;
import static pe.edu.lamolina.model.enums.InteresadoEstadoEnum.REG;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.ModalidadIngresoEnum.CEPRE;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.InscritoTaller;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.OpcionCarrera;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;
import pe.edu.lamolina.model.inscripcion.Taller;

@Service
@Transactional(readOnly = true)
public class InscripcionServiceImp implements InscripcionService {

    @Autowired
    InteresadoDAO interesadoDAO;
    @Autowired
    CicloPostulaDAO cicloPostulaDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;
    @Autowired
    PrelamolinaDAO prelamolinaDAO;
    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    PostulanteDAO postulanteDAO;
    @Autowired
    ModalidadIngresoDAO modalidadIngresoDAO;
    @Autowired
    OpcionCarreraDAO opcionCarreraDAO;
    @Autowired
    EventoCicloDAO eventoCicloDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    CarreraNuevaDAO carreraNuevaDAO;
    @Autowired
    TallerDAO tallerDAO;
    @Autowired
    InscritoTallerDAO inscritoTallerDAO;
    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;

    @Autowired
    PostulanteService postulanteService;

    @Autowired
    Random globalRandom;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TipoDocIdentidad> allTiposDocIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    @Transactional
    public void updateInteresado(Interesado interesado) {

        ObjectUtil.eliminarAttrSinId(interesado, "tipoDocumento");
        ObjectUtil.eliminarAttrSinId(interesado, "carreraNueva");
        ObjectUtil.eliminarAttrSinId(interesado, "carreraInteres");
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula cicloPostula = cicloPostulaDAO.findActivo(modalidadEstudio);
        Interesado interesadoDB = interesadoDAO.findByFacebookAndCiclo(interesado.getFacebook(), cicloPostula);

        interesado.setEstado(REG);
        interesado.setExoneradoExtemporaneo(Boolean.FALSE);
        interesado.setFechaInteresado(new Date());

        if (interesadoDB == null) {

            interesado.setAvatarColor(BG_COLORS.get(globalRandom.nextInt(BG_COLORS.size())));
            logger.info("face: <<" + interesado.getFacebook() + ">>");
            logger.info("face-link: <<" + interesado.getFacebookLink() + ">>");

            interesadoDAO.save(interesado);
        } else {
            interesado.setId(interesadoDB.getId());
//            interesadoDAO.update(interesado);
        }
//        
        Postulante postulante = postulanteDAO.findActivoByInteresadoSimple(interesado);

        if (postulante == null) {
            Persona persona = personaDAO.findByDocumento(TipoDocIdentidadEnum.DNI, AdmisionConstantine.CODE_POSTULANTE_DUMMY);
            persona.setEstadoEnum(PersonaEstadoEnum.ACT);
            personaDAO.update(persona);

            postulante = new Postulante();
            postulante.setEstado(PostulanteEstadoEnum.CRE);
            postulante.setFechaRegistro(new Date());
            postulante.setCicloPostula(interesado.getCicloPostula());
            postulante.setInteresado(interesado);
            postulante.setImportePagar(BigDecimal.ZERO);
            postulante.setImporteAbonado(BigDecimal.ZERO);
            postulante.setImporteDescuento(BigDecimal.ZERO);
            postulante.setImporteTotal(BigDecimal.ZERO);
            postulante.setImporteUtilizado(BigDecimal.ZERO);
            postulante.setPersona(persona);
            postulante.setCodigo("SOLO-INTERESADO");
            postulanteDAO.save(postulante);
        }

    }

    @Override
    public Prelamolina buscarComoCepre(Interesado interesado, CicloPostula ciclo, Integer loop) {
        logger.debug("iniciando busqueda en cepre");
        Prelamolina cepre = prelamolinaDAO.findByInteresado(interesado, ciclo);
        if (cepre != null) {
            return cepre;
        }

        logger.debug("busqueda del {} lugar", loop);
        Interesado tempo = new Interesado();
        tempo.setPaterno(interesado.getPaterno());
        tempo.setMaterno(interesado.getMaterno());

        int bucle = 0;
        String[] nombresArray = interesado.getNombres().split(" ");
        for (String nom : nombresArray) {
            tempo.setNombres("%" + nom + "%");
            logger.debug("nombres::{}", tempo.getNombres());
            cepre = prelamolinaDAO.findByInteresado(tempo, ciclo);
            if (cepre != null) {
                bucle++;
                logger.debug("CEPRE encontrado bucle={} loop={}", bucle, loop);
                if (bucle == loop) {
                    return cepre;
                }
            }
        }
        return null;
    }

    @Override
    public void verificarCepre(FormCepre formCepre, HttpSession session) {
        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);
        Interesado interesado = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();

        int bucle = 2;
        Prelamolina cepre = ds.getPrelamolina();
        while (cepre != null) {
            if (!compararDatos(formCepre, cepre)) {
                cepre = buscarComoCepre(interesado, ciclo, bucle);
                bucle++;
            } else {
                break;
            }

            if (bucle >= 20) {
                cepre = null;
                break;
            }
        }
        if (cepre == null) {
            throw new PhobosException("Lo sentimos, pero tus credenciales no son correctas");
        }

        cepre.setTipoDocIdentidad(formCepre.getTipoDocumento());
        cepre.setVerificado(true);
        ds.setPrelamolina(cepre);
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

    }

    private boolean compararDatos(FormCepre formCepre, Prelamolina cepre) {
        String dni = limpiarValor(formCepre.getNumeroDocIdentidad());
        return dni.equals(cepre.getNumeroDocIdentidad());
    }

    @Override
    @Transactional
    public void updateCepre(HttpSession session) {

        DataSessionAdmision ds = (DataSessionAdmision) session.getAttribute(AdmisionConstantine.SESSION_USUARIO);

        Interesado interesadoSession = ds.getInteresado();
        CicloPostula ciclo = ds.getCicloPostula();
        Prelamolina cepre = ds.getPrelamolina();
        Persona persona = crearPersona(cepre);

        Postulante postulante = postulanteService.findPostulanteActivoByInteresado(interesadoSession);
        if (postulante != null && !postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
            postulante.setEstado(PostulanteEstadoEnum.ANU);
            postulanteDAO.update(postulante);
            postulante = null;
        }

        ModalidadIngreso modalidadIngreso = modalidadIngresoDAO.findByCodeCliclo(AdmisionConstantine.CODE_MODALIDAD_CEPRE, ciclo);
        if (postulante != null && postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY)) {
            postulante.setModalidadIngreso(modalidadIngreso);
            postulante.setPersona(persona);
            postulante.setCodigo(persona.getNumeroDocIdentidad());
            postulante.setEmail(persona.getEmail());
            postulante.setEstado(PostulanteEstadoEnum.CRE);
            postulante.setImportePagar(BigDecimal.ZERO);
            postulante.setImporteAbonado(BigDecimal.ZERO);
            postulante.setImporteDescuento(BigDecimal.ZERO);
            postulante.setImporteTotal(BigDecimal.ZERO);
            postulante.setImporteUtilizado(BigDecimal.ZERO);
            postulanteDAO.update(postulante);
        }

        if (postulante == null) {
            postulante = new Postulante();
            postulante.setModalidadIngreso(modalidadIngreso);
            postulante.setPersona(persona);
            postulante.setCodigo(persona.getNumeroDocIdentidad());
            postulante.setEmail(persona.getEmail());
            postulante.setInteresado(interesadoSession);
            postulante.setCicloPostula(ciclo);
            postulante.setEstado(PostulanteEstadoEnum.CRE);
            postulante.setImportePagar(BigDecimal.ZERO);
            postulante.setImporteAbonado(BigDecimal.ZERO);
            postulante.setImporteDescuento(BigDecimal.ZERO);
            postulante.setImporteTotal(BigDecimal.ZERO);
            postulante.setImporteUtilizado(BigDecimal.ZERO);
            postulanteDAO.save(postulante);
        }

        OpcionCarrera opcion = new OpcionCarrera();
        opcion.setCarreraPostula(cepre.getCarreraPostula());
        opcion.setPostulante(postulante);
        opcion.setPrioridad(1);
        opcionCarreraDAO.save(opcion);

        cepre.setPersona(persona);
        cepre.setPostulante(postulante);
        cepre.setFechaValidado(new Date());
        prelamolinaDAO.update(cepre);

        if (cepre.getEsIngresante() == 1) {
            Interesado interesado = interesadoDAO.find(interesadoSession.getId());
            interesado.setExoneradoExtemporaneo(Boolean.TRUE);
            interesadoDAO.update(interesado);
        }

        postulante.setOpcionCarrera(new ArrayList());
        postulante.getOpcionCarrera().add(opcion);

        ds.setPostulante(postulante);
        ds.setPersona(persona);
        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, ds);

    }

    private Persona crearPersona(Prelamolina cepre) {
        Persona persona = cepre.getPersona();
        if (persona != null) {
            return persona;
        }

        cepre.setNumeroDocIdentidad(limpiarValor(cepre.getNumeroDocIdentidad()));
        if (cepre.getTipoDocIdentidad() == null || (cepre.getTipoDocIdentidad() != null && cepre.getTipoDocIdentidad().getId() == null)) {
            throw new PhobosException("Debe indicar el documento de identidad");
        }
        if (cepre.getNumeroDocIdentidad() == null) {
            throw new PhobosException("Debe indicar el número del documento de identidad");
        }
        logger.debug("BUSCAR PERSONA DOC {} NUM  {}", cepre.getTipoDocIdentidad(), cepre.getNumeroDocIdentidad());
        persona = personaDAO.findByDocumento(cepre.getTipoDocIdentidad(), cepre.getNumeroDocIdentidad());
        if (persona != null) {
            persona.setEstadoEnum(PersonaEstadoEnum.ACT);
            personaDAO.update(persona);
            return persona;
        }

        persona = new Persona(cepre.getTipoDocIdentidad(), cepre.getNumeroDocIdentidad());
        persona.setPaterno(cepre.getPaterno());
        persona.setMaterno(cepre.getMaterno());
        persona.setNombres(cepre.getNombres());
        persona.setSexo(cepre.getSexo());
        persona.setEmail(cepre.getEmail());
        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        personaDAO.save(persona);

        return persona;
    }

    private String limpiarValor(String valor) {
        if (valor == null) {
            return null;
        }

        valor = valor.trim();
        if (StringUtils.isEmpty(valor)) {
            return null;
        }
        return valor;
    }

    @Override
    public Boolean esPrelamolinaActiva(List<EventoCiclo> eventos, CicloPostula ciclo) {

        for (EventoCiclo eventoCiclo : eventos) {
            Evento evento = eventoCiclo.getEvento();
            if (evento.getCodigo().equals("CEPRE")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Boolean esInscripcionActiva(List<EventoCiclo> eventos, CicloPostula ciclo) {
        for (EventoCiclo eventoCiclo : eventos) {
            Evento evento = eventoCiclo.getEvento();
            if (evento.getCodigo().equals("INSC")) {
                return true;
            }
            if (evento.getCodigo().equals("OTR")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Boolean esExtemporaneaActiva(List<EventoCiclo> eventos, CicloPostula ciclo) {
        for (EventoCiclo eventoCiclo : eventos) {
            Evento evento = eventoCiclo.getEvento();
            if (evento.getCodigo().equals("EXTM")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<EventoCiclo> allEventosDiaByCiclo(CicloPostula ciclo) {
        return eventoCicloDAO.allByFechaCiclo(new Date(), ciclo);
    }

    @Override
    public List<Carrera> allCarreras() {
        return carreraDAO.allActivos();
    }

    @Override
    public List<CarreraNueva> allCarreraNuevas() {
        return carreraNuevaDAO.allActivo();
    }

    @Override
    public Boolean esFinalInscripciones(CicloPostula ciclo) {
        return postulanteService.esFinalInscripciones(ciclo);
    }

    @Override
    public List<DeudaInteresado> allDeudasInteresado(Interesado interesado) {
        Postulante postulante = postulanteDAO.findByInteresado(interesado);
        return postulante != null ? postulanteService.allDeudaActivaByPostulante(postulante) : new ArrayList();
    }

    @Override
    public Taller findTaller(Long id) {
        return tallerDAO.find(id);
    }

    @Override
    @Transactional
    public void saveInscritoTaller(Interesado interesado) {
        Taller tallerDB = tallerDAO.find(interesado.getIdTaller());

        InscritoTaller findInsTaller = inscritoTallerDAO.findByInteresadoTaller(interesado, interesado.getIdTaller());

        if (tallerDB == null) {
            return;
        }
        if (findInsTaller != null) {
            return;
        }
        if (tallerDB.getInscritos() >= tallerDB.getAforo()) {
            throw new PhobosException("Este taller ha alcanzado su capacidad máxima.");
        }

        InscritoTaller insTaller = new InscritoTaller();

        insTaller.setEstado(EstadoInscritoEnum.ACT.name());
        insTaller.setFechaInscripcion(new Date());
        insTaller.setInteresado(interesado);
        insTaller.setTaller(new Taller(interesado.getIdTaller()));

        tallerDB.setInscritos(tallerDB.getInscritos() + 1);
        tallerDAO.update(tallerDB);
        inscritoTallerDAO.save(insTaller);
    }

    @Override
    public ContenidoCarta findContenidoCartaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum) {
        return contenidoCartaDAO.findByCodigoEnum(contenidoCartaEnum);
    }

    @Override
    public CicloPostula findCicloPostula(CicloPostula cicloPostula) {
        return cicloPostulaDAO.find(cicloPostula.getId());
    }

    @Override
    public Boolean esIngresantePre(DataSessionAdmision ds) {
        if (ds.getPostulante() == null) {
            return false;
        }
        Postulante postulante = postulanteDAO.findActivoByInteresadoSimple(ds.getInteresado());

        if (postulante.getModalidadIngreso() == null) {
            return false;
        }
        return postulante.getModalidadIngreso().getCodigo().equals(CEPRE.getCode());
    }

    @Override
    public CicloPostula getCicloPostulaActivo() {

        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);

        return cicloPostulaDAO.findActivo(modalidadEstudio);
    }

}
