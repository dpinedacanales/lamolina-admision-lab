package edu.pe.lamolina.admision.movil.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import org.springframework.social.facebook.api.User;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface SecurityRestService {

    DataSessionAdmision login(User user);

    DataSessionAdmision register(ObjectNode payload);

    ObjectNode findPermisos(Interesado interesado, Postulante postulante);

    EventoCiclo findEvento(CicloPostula cicloPostula, EventoEnum eventoEnum);

}
