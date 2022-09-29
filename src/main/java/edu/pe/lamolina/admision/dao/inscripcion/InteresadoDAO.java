package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;

public interface InteresadoDAO extends EasyDAO<Interesado> {

    Interesado findByFacebookAndCiclo(String idFacebook, CicloPostula cicloPostulaActivo);

    Interesado findByEmailAndCiclo(String email, CicloPostula cicloPostulaActivo);

    List<Interesado> allByFacebook(String id);

    void cleanPlayerCelular(String playerCelular);

    public Interesado findByDocumento(String numeroDocIdentidad, CicloPostula ciclo);

    public List<Interesado> allByDocumento(String documentoContingencia);

}
