package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.InscritoTaller;
import pe.edu.lamolina.model.inscripcion.Interesado;

public interface InscritoTallerDAO extends EasyDAO<InscritoTaller> {

    InscritoTaller findByInteresadoTaller(Interesado interesado, Long taller);

    List<InscritoTaller> allByInteresado(Interesado interesado);

}
