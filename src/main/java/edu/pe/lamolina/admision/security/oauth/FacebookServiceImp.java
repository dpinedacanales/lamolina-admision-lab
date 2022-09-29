package edu.pe.lamolina.admision.security.oauth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.seguridad.TokenIngresanteDAO;
import edu.pe.lamolina.admision.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.Locale;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.ING;
import pe.edu.lamolina.model.enums.TokenEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.seguridad.TokenIngresante;
import pe.edu.lamolina.model.seguridad.Usuario;

@Service
public class FacebookServiceImp implements FacebookService {

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
    TokenIngresanteDAO tokenDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void loginManually(User user, HttpSession session) {

        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(modalidad);
        Interesado interesado = interesadoDAO.findByFacebookAndCiclo(user.getId(), ciclo);
        Postulante postulante = null;

        if (interesado == null) {
            List<Interesado> interesados = interesadoDAO.allByFacebook(user.getId());
            if (interesados.isEmpty()) {
                interesado = crearInteresado(user, ciclo);
            } else {
                Postulante postulanteING = existeIngresante(interesados, ciclo);
                if (postulanteING == null) {
                    interesados.get(0).setFacebookLink("https://www.facebook.com/search/top/?q=" + user.getName());
                    interesado = clonarInteresado(interesados.get(0), ciclo);
                } else {
                    postulante = postulanteING;
                    interesado = postulanteING.getInteresado();
                }
            }
        }

        if (interesado.getId() != null) {
            interesado.setFacebookLink("https://www.facebook.com/search/top/?q=" + user.getName());
            postulante = postulanteDAO.findActivoByInteresadoSimple(interesado);
        }

        SecurityContext ctx = SecurityContextHolder.getContext();
        Collection<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ADMINISTRADOR"));

        Authentication authentication = new UsernamePasswordAuthenticationToken(interesado.getNombres(), interesado.getNombres(), authorities);
        ctx.setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);

        DataSessionAdmision dataSession = new DataSessionAdmision();
        dataSession.setCicloPostula(ciclo);
        dataSession.setInteresado(interesado);
        dataSession.setPostulante(postulante);

        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, dataSession);
    }

    private Postulante existeIngresante(List<Interesado> interesados, CicloPostula ciclo) {

        Postulante postulante = existeCicloAnterior(interesados, ciclo);
        if (postulante != null) {
            return postulante;
        }

        return existeYearAnterior(interesados, ciclo);

    }

    private Postulante existeYearAnterior(List<Interesado> interesados, CicloPostula ciclo) {
        CicloAcademico cicloAcad = ciclo.getCicloAcademico();
        String nroCiclo = cicloAcad.getNumeroCiclo();
        Integer year = cicloAcad.getYear();

        List<String> codigosCiclos = new ArrayList();
        if (nroCiclo.equals("2")) {
            codigosCiclos.add("2");
            codigosCiclos.add("1");
        } else {
            codigosCiclos.add("1");
        }
        for (String nroCicloLoop : codigosCiclos) {
            Postulante postulante = findPostulanteAnterior(interesados, year - 1, nroCicloLoop);
            if (postulante != null) {
                ModalidadIngreso modalidad = postulante.getModalidadIngreso();
                if (modalidad.isQuintoSecundaria()) {
                    return postulante;
                }
            }
        }
        return null;
    }

    private Postulante existeCicloAnterior(List<Interesado> interesados, CicloPostula ciclo) {
        CicloAcademico cicloAcad = ciclo.getCicloAcademico();
        String nroCiclo = cicloAcad.getNumeroCiclo();
        Integer year = cicloAcad.getYear();

        if (nroCiclo.equals("2")) {
            nroCiclo = "1";
        } else if (ciclo.getCicloAcademico().getCodigo().equals("202110")) {
            year = year - 1;
            nroCiclo = "2";
            Postulante postulante = findPostulanteAnterior(interesados, year, nroCiclo);
            if (postulante == null) {
                nroCiclo = "1";
                return findPostulanteAnterior(interesados, year, nroCiclo);
            }
        } else {
            year = year - 1;
            nroCiclo = "2";
        }

        return findPostulanteAnterior(interesados, year, nroCiclo);
    }

    private Postulante findPostulanteAnterior(List<Interesado> interesados, Integer year, String nroCiclo) {
        Interesado interesadoING = null;
        CicloPostula cicloAntes = cicloPostulaDAO.findRegularByYearNumeroCiclo(year, Integer.valueOf(nroCiclo));
        for (Interesado interesado : interesados) {
            if (interesado.getCicloPostula().getId() == cicloAntes.getId().longValue()) {
                interesadoING = interesado;
            }
        }
        if (interesadoING == null) {
            return null;
        }
        Postulante postulante = postulanteDAO.findByInteresado(interesadoING);
        if (postulante == null) {
            return null;
        }
        if (postulante.getEstadoEnum() != ING) {
            return null;
        }

        return postulante;

    }

    @Override
    public Interesado crearInteresado(User user, CicloPostula ciclo) {
        Interesado interesado = new Interesado();

        if (user.getLastName() != null) {
            String[] apells = user.getLastName().split(" ");
            if (apells.length < 2) {
                interesado.setPaterno(user.getLastName());
            } else {
                interesado.setPaterno(apells[0]);
                apells = Arrays.copyOfRange(apells, 1, apells.length);
                interesado.setMaterno(String.join(" ", apells));
            }
        }

        interesado.setFacebook(user.getId());
        interesado.setNombres(user.getFirstName());
        interesado.setEmail(user.getEmail());
        interesado.setFechaRegistro(new Date());
        interesado.setCicloPostula(ciclo);
        interesado.setEstado(InteresadoEstadoEnum.CRE);

        if (user.getName() != null) {
            interesado.setFacebookLink("https://www.facebook.com/search/top/?q=" + user.getName());
        }

        return interesado;
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
    public void updateInteresado(Interesado interesado) {
        interesadoDAO.update(interesado);
    }

    @Override
    public void authFromTotoro(Long idPostulante, Long user, HttpSession session) {
        Postulante postulante = postulanteDAO.find(idPostulante);
        Assert.isNotNull(postulante, "No existe postulante");

        Usuario usuarioBD = usuarioDAO.find(user);
        boolean esMismaPersona = postulante.getPersona().getId() == usuarioBD.getPersona().getId().longValue();
        Assert.isTrue(esMismaPersona, "Usuario no Autorizado");

        List<TokenIngresante> tokens = tokenDAO.allActivos(postulante.getPersona(), usuarioBD);
        Assert.isFalse(tokens.isEmpty(), "Usuario no Autenticado");

        tokens.get(0).setEstadoEnum(TokenEstadoEnum.USO);
        tokens.get(0).setFechaUso(new Date());
        tokenDAO.update(tokens.get(0));

        SecurityContext ctx = SecurityContextHolder.getContext();
        Collection<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ADMINISTRADOR"));

        Interesado interesado = postulante.getInteresado();
        CicloPostula ciclo = postulante.getCicloPostula();

        Authentication authentication = new UsernamePasswordAuthenticationToken(interesado.getNombres(), interesado.getNombres(), authorities);
        ctx.setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, ctx);

        DataSessionAdmision dataSession = new DataSessionAdmision();
        dataSession.setCicloPostula(ciclo);
        dataSession.setInteresado(interesado);
        dataSession.setPostulante(postulante);

        session.setAttribute(AdmisionConstantine.SESSION_USUARIO, dataSession);
    }
}
