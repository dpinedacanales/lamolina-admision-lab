/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.pe.lamolina.admision.controller.admision.inscripcion.loginContingencia;

import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import edu.pe.lamolina.admision.dao.general.PersonaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.CicloPostulaDAO;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import edu.pe.lamolina.admision.dao.inscripcion.InteresadoDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PostulanteDAO;
import edu.pe.lamolina.admision.dao.inscripcion.PrelamolinaDAO;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.enums.InteresadoEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.PostulanteEstadoEnum.ING;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;

/**
 *
 * @author dpinedac
 */
@Service
public class LoginContingenciaServiceImpl implements LoginContingenciaService {

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    InteresadoDAO interesadoDAO;

    @Autowired
    PostulanteDAO postulanteDAO;

    @Autowired
    CicloPostulaDAO cicloPostulaDAO;

    @Autowired
    EventoCicloDAO eventoCicloDAO;

    @Autowired
    PrelamolinaDAO prelamolinaDAO;

    @Override
    public Boolean loginManuallyContingencia(Interesado interesadoForm, HttpSession session) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        CicloPostula ciclo = cicloPostulaDAO.findActivo(modalidad);
        Interesado interesado = interesadoDAO.findByFacebookAndCiclo(interesadoForm.documentoContingencia(), ciclo);

        Postulante postulante = null;

        Boolean existe = true;
        if (interesado == null) {
            interesado = interesadoDAO.findByDocumento(interesadoForm.getNumeroDocIdentidad(), ciclo);
            if (interesado == null) {
                List<Interesado> interesados = interesadoDAO.allByFacebook(interesadoForm.documentoContingencia());
                if (interesados.isEmpty()) {
                    interesados = interesadoDAO.allByDocumento(interesadoForm.getNumeroDocIdentidad());
                    if (interesados.isEmpty()) {
                        interesado = crearInteresado(interesadoForm, ciclo);
                        existe = false;
                        
                    } else {
                        Postulante postulanteING = existeIngresante(interesados, ciclo);
                        if (postulanteING == null) {
                            interesados.get(0).setFacebookLink(interesadoForm.documentoContingencia());
                            interesado = clonarInteresado(interesados.get(0), ciclo);
                        } else {
                            postulante = postulanteING;
                            interesado = postulanteING.getInteresado();
                        }
                    }
                } else {
                    Postulante postulanteING = existeIngresante(interesados, ciclo);
                    if (postulanteING == null) {
                        interesados.get(0).setFacebookLink(interesadoForm.documentoContingencia());
                        interesado = clonarInteresado(interesados.get(0), ciclo);

                        existe = false;
                    } else {
                        postulante = postulanteING;
                        interesado = postulanteING.getInteresado();
                    }
                }
            } else {
                interesado.setCodigoVerificacion(interesadoForm.getCodigoVerificacion());
                interesadoDAO.update(interesado);
            }

        }

        if (interesado.getId() != null) {
            interesado.setFacebookLink(interesadoForm.documentoContingencia());
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
        
        return existe;
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

    private Interesado crearInteresado(Interesado interesadoForm, CicloPostula ciclo) {
        Interesado interesado = new Interesado();

        interesado.setFacebook(interesadoForm.documentoContingencia());
        interesado.setNombres(interesadoForm.getNombres());
        interesado.setEmail(interesadoForm.getEmail());
        interesado.setFechaRegistro(new Date());
        interesado.setCicloPostula(ciclo);
        interesado.setEstado(InteresadoEstadoEnum.CRE);

        if (interesadoForm.documentoContingencia() != null) {
            interesado.setFacebookLink("https://www.facebook.com/search/top/?q=" + interesadoForm.documentoContingencia());
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
    public Boolean verificarFechaInscripcion(CicloPostula cicloPostula, Interesado interesadoForm) {

        List<EventoCiclo> eventosCiclos = eventoCicloDAO.allByFechaCiclo(new Date(), cicloPostula);

        EventoCiclo evento = eventosCiclos.stream().filter(x -> x.getEventoEnum() == EventoEnum.CEPRE_EXTM).findAny().orElse(null);

        if (evento != null) {
            Persona persona = new Persona();
            persona.setNumeroDocIdentidad(interesadoForm.getNumeroDocIdentidad());
            Prelamolina prelamolina = prelamolinaDAO.findByInteresadoAndCiclo(interesadoForm, cicloPostula);

            Assert.isNotNull(prelamolina, "La inscripción a culminado.");
            Assert.isTrue(prelamolina.getDescuento() == 50 || prelamolina.getDescuento() == 30 || prelamolina.getEsIngresante() == 1, "La inscripción a culminado.");

            return true;
        }
        evento = eventosCiclos.stream().filter(x -> Arrays.asList(EventoEnum.INSC, EventoEnum.EXTM).contains(x.getEventoEnum())).findAny().orElse(null);
        Assert.isNotNull(evento, "La inscripción a culminado.");

        return true;
    }
}
