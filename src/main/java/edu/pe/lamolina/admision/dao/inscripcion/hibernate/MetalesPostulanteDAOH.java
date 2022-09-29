package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.MetalesPostulanteDAO;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.inscripcion.MetalesPostulante;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class MetalesPostulanteDAOH extends AbstractDAO<MetalesPostulante> implements MetalesPostulanteDAO {

    public MetalesPostulanteDAOH() {
        super();
        setClazz(MetalesPostulante.class);
    }

    @Override
    public MetalesPostulante findByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query(MetalesPostulante.class)
                .join("postulante pos")
                .filter("pos.id", postulante);
        return (MetalesPostulante) sql.find(getCurrentSession());
    }

}
