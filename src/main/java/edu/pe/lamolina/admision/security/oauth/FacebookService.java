package edu.pe.lamolina.admision.security.oauth;

import javax.servlet.http.HttpSession;
import org.springframework.social.facebook.api.User;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;

public interface FacebookService {

    void loginManually(User userFace, HttpSession session);

    void updateInteresado(Interesado interesado);

    Interesado crearInteresado(User user, CicloPostula ciclo);

    void authFromTotoro(Long idPostulante, Long user, HttpSession session);

}
