package edu.pe.lamolina.admision.movil.security;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.controller.admision.evaluacion.EvaluacionService;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.InscripcionService;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.seguridad.UsuarioDAO;
import edu.pe.lamolina.admision.movil.boletas.BoletasRestService;
import edu.pe.lamolina.admision.movil.complemento.ComplementoRestService;
import edu.pe.lamolina.admision.movil.inscripcion.InscripcionRestService;
import edu.pe.lamolina.admision.security.oauth.FacebookService;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.inscripcion.Postulante;

import org.springframework.social.facebook.api.User;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.PROS;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;

@Service
@Transactional(readOnly = true)
public class SecurityRestServiceImp implements SecurityRestService {

    @Autowired
    CicloPostulaDAO cicloPostulaDAO;

    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    InteresadoDAO interesadoDAO;

    @Autowired
    PostulanteDAO postulanteDAO;

    @Autowired
    FacebookService facebookService;

    @Autowired
    InscripcionService inscripcionService;

    @Autowired
    InscripcionRestService inscripcionRestService;

    @Autowired
    EvaluacionService evaluacionService;

    @Autowired
    BoletasRestService boletasRestService;

    @Autowired
    ComplementoRestService complementoRestService;

    @Autowired
    EventoCicloDAO eventoCicloDAO;

    @Override
    @Transactional
    public DataSessionAdmision login(User user) {

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(modalidad);

        Interesado interesado = interesadoDAO.findByFacebookAndCiclo(user.getId(), ciclo);

        Postulante postulante = null;

        if (interesado == null) {

            List<Interesado> interesados = interesadoDAO.allByFacebook(user.getId());

            if (interesados.isEmpty()) {
                interesado = facebookService.crearInteresado(user, ciclo);

            } else {
                interesados.get(0).setFacebookLink("https://www.facebook.com/search/top/?q=" + user.getName());
                interesado = clonarInteresado(interesados.get(0), ciclo);
            }

        } else {

            this.registerPlayerCelular(interesado, user.getThirdPartyId());
            postulante = postulanteDAO.findActivoByInteresadoSimple(interesado);
        }

        DataSessionAdmision dataSession = new DataSessionAdmision();
        dataSession.setCicloPostula(ciclo);
        dataSession.setInteresado(interesado);
        dataSession.setPostulante(postulante);

        return dataSession;
    }

    private Interesado clonarInteresado(Interesado interesadoBD, CicloPostula ciclo) {
        Interesado interesado = new Interesado();

        interesado.setPaterno(interesadoBD.getPaterno());
        interesado.setMaterno(interesadoBD.getMaterno());
        interesado.setNombres(interesadoBD.getNombres());

        interesado.setFacebook(interesadoBD.getFacebook());
        interesado.setEmail(interesadoBD.getEmail());
        interesado.setCelular(interesadoBD.getCelular());
        interesado.setTelefono(interesadoBD.getTelefono());
        interesado.setFacebookLink(interesadoBD.getFacebookLink());
        interesado.setFechaRegistro(new Date());
        interesado.setCicloPostula(ciclo);
        interesado.setEstado(InteresadoEstadoEnum.CRE);
        return interesado;
    }

    @Override
    @Transactional
    public DataSessionAdmision register(ObjectNode payload) {

        User user = (User) JsonHelper.fromJson(payload.get("user").toString(), User.class);

        Interesado interesadoForm = (Interesado) JsonHelper.fromJson(payload.get("interesado").toString(), Interesado.class);

        CicloPostula ciclo = complementoRestService.findCicloPostulaActivo();

        Interesado interesado = interesadoDAO.findByFacebookAndCiclo(user.getId(), ciclo);

        Postulante postulante = null;

        if (interesado == null) {
            interesado = facebookService.crearInteresado(user, ciclo);
            interesado.setEmail(interesadoForm.getEmail());
            interesado.setCelular(interesadoForm.getCelular());
            interesado.setNumeroDocIdentidad(interesadoForm.getNumeroDocIdentidad());
            interesado.setTipoDocumento(interesadoForm.getTipoDocumento());
            interesado.setCarreraInteres(interesadoForm.getCarreraInteres());

            if (interesadoForm.getCarreraNueva().getId() == -1l) {
                interesado.setOtraCarrera(interesadoForm.getOtraCarrera());
            } else {
                interesado.setCarreraNueva(interesadoForm.getCarreraNueva());
            }

            inscripcionService.updateInteresado(interesado);
            
            this.registerPlayerCelular(interesado, interesadoForm.getPlayerCelular());
            postulante = postulanteDAO.findActivoByInteresadoSimple(interesado);
        }

        DataSessionAdmision dataSession = new DataSessionAdmision();
        dataSession.setCicloPostula(ciclo);
        dataSession.setInteresado(interesado);
        dataSession.setPostulante(postulante);

        return dataSession;
    }

    @Override
    public ObjectNode findPermisos(Interesado interesado, Postulante postulante) {
        ObjectNode permisos = new ObjectNode(JsonNodeFactory.instance);

        if (interesado.getId() == null || postulante.getId() == null) {
            return permisos;
        }

        Boolean verBoletas = boletasRestService.tienePermiso(interesado);
        Boolean verResultados = inscripcionRestService.tienePermisoVerResultados(postulante);

        permisos.put("verBoletas", verBoletas);
        permisos.put("verSimulacion", evaluacionService.tienePermiso(interesado));
        permisos.put("verExamen", inscripcionRestService.tienePermisoVerExamen(postulante));
        permisos.put("verResultados", verResultados);

        Boolean yaPostulo = postulante.isInscrito() || postulante.isEstadoPreInscrito();

        Long pagosPendientes = boletasRestService.countPendientes(interesado);

        Boolean resultados = verResultados;
        Boolean postular = !verResultados && (postulante.isEstadoCreado() || postulante.getEstadoEnum() == PROS);
        Boolean boletas = !postular && (yaPostulo && pagosPendientes.compareTo(0L) > 0);
        Boolean examen = !boletas && (yaPostulo && pagosPendientes.compareTo(0L) == 0);

        permisos.put("accionResultados", resultados);
        permisos.put("accionPostular", postular);
        permisos.put("accionBoletas", boletas);
        permisos.put("accionExamen", examen);

        return permisos;
    }

    @Override
    public EventoCiclo findEvento(CicloPostula ciclo, EventoEnum evento) {
        return eventoCicloDAO.findByCicloEvento(ciclo, evento);
    }

    @Transactional
    private void registerPlayerCelular(Interesado interesado, String playerCelular) {

        if (StringUtils.isEmpty(playerCelular)) {
            return;
        }

        interesadoDAO.cleanPlayerCelular(playerCelular);

        interesado.setPlayerCelular(playerCelular);
        interesadoDAO.update(interesado);
    }

}
