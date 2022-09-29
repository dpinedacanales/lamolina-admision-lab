package edu.pe.lamolina.admision.movil.inscripcion;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.controller.admision.inscripcion.guiapostulante.GuiaPostulanteService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.InscripcionService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.modalidad.ModalidadService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.postulante.PostulanteService;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadIngresoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.movil.complemento.ComplementoRestService;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.MetalesPostulante;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Service
@Transactional(readOnly = true)
public class InscripcionRestServiceImp implements InscripcionRestService {

    @Autowired
    GuiaPostulanteService guiaPostulanteService;

    @Autowired
    CicloPostulaDAO cicloPostulaDAO;

    @Autowired
    ModalidadService modalidadService;

    @Autowired
    ModalidadIngresoDAO modalidadIngresoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    InteresadoDAO interesadoDAO;

    @Autowired
    EventoDAO eventoDAO;

    @Autowired
    EventoCicloDAO eventoCicloDAO;

    @Autowired
    ModalidadIngresoCicloDAO modalidadIngresoCicloDAO;

    @Autowired
    PostulanteService postulanteService;

    @Autowired
    InscripcionService inscripcionService;

    @Autowired
    PostulanteDAO postulanteDAO;

    @Autowired
    ComplementoRestService complementoRestService;

    @Override
    public List<CarreraPostula> allCarreraPostulaByModalidad(ModalidadIngreso modalidadIngreso) {
        ModalidadIngreso miBD = modalidadIngresoDAO.find(modalidadIngreso.getId());
        CicloPostula cicloPostula = cicloPostulaDAO.findActivo(miBD.getModalidadEstudio());
        return modalidadService.allCarreras(miBD, cicloPostula);
    }

    @Override
    public void verifyNumeroDocumento(Postulante postulanteForm) {
        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(me);

        postulanteService.validarDocumentoAndVerificarPersona(postulanteForm, postulanteForm.getPersona(), ciclo);
    }

    @Override
    @Transactional
    public Postulante savePostulante(Postulante postulanteForm) {

        Interesado interesadoForm = postulanteForm.getInteresado();
        Persona personaForm = postulanteForm.getPersona();
        MetalesPostulante metalesForm = postulanteForm.getMetalesPostulante();

        ObjectUtil.eliminarAttrSinId(postulanteForm);

        postulanteForm.setInteresado(interesadoForm);
        postulanteForm.setPersona(personaForm);
        postulanteForm.setMetalesPostulante(metalesForm);

        Assert.isNotNull(postulanteForm.getModalidadIngresoCiclo(), "Debe seleccionar la modalidad de ingreso");

        postulanteForm.setModalidadIngreso((postulanteForm.getModalidadIngresoCiclo().getModalidadIngreso()));

        CicloPostula ciclo = complementoRestService.findCicloPostulaActivo();

        Interesado interesado = interesadoDAO.find(postulanteForm.getInteresado().getId());
        postulanteForm.setInteresado(interesado);

        Persona persona = postulanteForm.getPersona();

        persona.setPrimerNombre(getPrimerNombre(interesado.getNombres()));
        persona.setSegundoNombre(getSegundoNombre(interesado.getNombres()));
        persona.unirNombres();

        Postulante postulante = postulanteService.saveDatosPersonales(postulanteForm, postulanteForm.getPersona(), interesado, ciclo);
        postulanteForm.setId(postulante.getId());

        postulanteService.saveDatosAcademicos(postulanteForm, ciclo);
        postulanteService.saveCerrarInscripcion(postulanteForm, ciclo);
        postulanteService.actualizarImportes(postulanteForm, ciclo);

        postulanteForm.getMetalesPostulante().setPostulante(postulante);
        postulanteService.saveMetalesPostulante(postulanteForm.getMetalesPostulante());

        return postulanteService.findPostulante(postulante);
    }

    @Override
    public List<ModalidadIngresoCiclo> allModalidad() {
        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(me);
        List<ModalidadIngresoCiclo> modalidadCiclo = modalidadIngresoCicloDAO.allByCiclo(ciclo);
        modalidadCiclo = modalidadCiclo.stream().filter(mi -> !mi.getModalidadIngreso().isPreLaMolina()).collect(Collectors.toList());
        return modalidadCiclo;
    }

    @Override
    public Postulante findPostulante(Postulante postulante) {
        return postulanteService.findPostulante(postulante);
    }

    @Override
    public ModalidadIngreso findModalidadCepre() {
        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(me);
        ModalidadIngreso mi = modalidadIngresoDAO.findByCodeCliclo(AdmisionConstantine.CODE_MODALIDAD_CEPRE, ciclo);
        ModalidadIngresoCiclo mic = modalidadIngresoCicloDAO.findByModalidadIngresoCiclo(mi, ciclo);
        mi.setModalidadIngresoCicloActual(mic);
        return mi;
    }

    private String getPrimerNombre(String nombres) {
        return nombres.trim().replaceAll("\\s+", " ").split(" ")[0];
    }

    private String getSegundoNombre(String nombres) {
        String[] array = nombres.trim().replaceAll("\\s+", " ").split(" ");
        array = ArrayUtils.remove(array, 0);
        return String.join(" ", array);
    }

    @Override
    public boolean mostrarUbicacion() {
        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(me);
        Evento eventoAulas = eventoDAO.findByCode(EventoEnum.AULA.name());
        EventoCiclo verAulas = null;
        EventoCiclo examen = null;
        List<EventoCiclo> visualizacionAulas = eventoCicloDAO.allByEventoCiclo(eventoAulas, ciclo);
        if (!visualizacionAulas.isEmpty()) {
            verAulas = visualizacionAulas.get(0);
        }

        Date today = new Date();

        return verAulas != null && !today.before(verAulas.getFechaInicio());
    }

    @Override
    public List<ModalidadIngreso> allModalidadParticipanteLibre() {
        List<ModalidadIngresoCiclo> modalidades = this.allModalidad();
        return modalidades
                .stream()
                .map(modalidadCiclo -> modalidadCiclo.getModalidadIngreso())
                .filter(modalidad -> !modalidad.isPreLaMolina() && !modalidad.isParticipanteLibre())
                .collect(Collectors.toList());
    }

    @Override
    public ObjectNode opcionesInscripcionByPostulante(Postulante postulanteForm) {

        ModalidadEstudio me = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(me);

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        List<EventoCiclo> eventosHoy = inscripcionService.allEventosDiaByCiclo(ciclo);

        Boolean esPrelamolinaActiva = inscripcionService.esPrelamolinaActiva(eventosHoy, ciclo);
        Boolean esInscripcionActiva = inscripcionService.esInscripcionActiva(eventosHoy, ciclo);
        Boolean esExtemporaneaActiva = inscripcionService.esExtemporaneaActiva(eventosHoy, ciclo);
        Boolean esFinalInscripciones = inscripcionService.esFinalInscripciones(ciclo);

        node.put("esPrelamolinaActiva", esPrelamolinaActiva);
        node.put("esInscripcionActiva", esInscripcionActiva);
        node.put("esExtemporaneaActiva", esExtemporaneaActiva);
        node.put("esFinalInscripciones", esFinalInscripciones);

        if (postulanteForm.getId() != null) {
            Postulante postulanteBD = postulanteDAO.find(postulanteForm.getId());
            node.put("guiaPostulanteAdquirida", postulanteBD.getEstadoEnum() == PostulanteEstadoEnum.CRE && !postulanteBD.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY));
        }

        return node;
    }

    @Override
    public Boolean tienePermisoVerExamen(Postulante postulante) {
        if (postulante == null) {
            return false;
        }

        Boolean guiaAdquirida = postulante.getEstadoEnum() == PostulanteEstadoEnum.CRE && !postulante.getPersona().getNumeroDocIdentidad().equals(AdmisionConstantine.CODE_POSTULANTE_DUMMY);

        return postulante.isEstadoPreInscrito() || postulante.isInscrito() || postulante.getEstadoEnum() == PostulanteEstadoEnum.PROS || guiaAdquirida;
    }

    @Override
    public Boolean tienePermisoVerResultados(Postulante postulante) {
        if (postulante == null) {
            return false;
        }

        if (!(postulante.isIngresante() || postulante.isSinVacante())) {
            return false;
        }

        CicloPostula ciclo = postulante.getCicloPostula();
        Date today = new Date();
        EventoCiclo verResultados = eventoCicloDAO.findByCicloEvento(ciclo, EventoEnum.VER);

        return verResultados != null && today.after(verResultados.getFechaInicio());

    }

    @Override
    public Postulante findPostulanteActivoByInteresado(Interesado interesado) {
        Interesado interesadoBD = interesadoDAO.find(interesado.getId());
        return postulanteService.findPostulanteActivoByInteresado(interesadoBD);
    }

    @Override
    public boolean esFinalInscripciones() {
        CicloPostula cicloPostula = complementoRestService.findCicloPostulaActivo();
        return postulanteService.esFinalInscripciones(cicloPostula);
    }

    @Override
    public Persona getPersona(Persona persona) {
        return postulanteService.getPersona(persona);
    }

    @Override
    public void completarPostulante(Postulante postulante, Interesado interesado) {
        CicloPostula cicloPostula = complementoRestService.findCicloPostulaActivo();
        postulanteService.completarPostulante(postulante, interesado, cicloPostula);
    }

    @Override
    public Interesado findInteresado(Long idInteresado) {
        return interesadoDAO.find(idInteresado);
    }

    @Override
    public ModalidadIngresoCiclo findModalidad(Long modalidadIngreso) {

        return modalidadIngresoCicloDAO.find(modalidadIngreso);
    }

}
