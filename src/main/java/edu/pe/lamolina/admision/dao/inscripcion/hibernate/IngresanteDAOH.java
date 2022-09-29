package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.IngresanteDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Ingresante;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class IngresanteDAOH extends AbstractEasyDAO<Ingresante> implements IngresanteDAO {

    public IngresanteDAOH() {
        super();
        setClazz(Ingresante.class);
    }

    @Override
    public List<Ingresante> allByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(Ingresante.class, "ii")
                .join("postulante po", "po.cicloPostula ci", "po.persona", "po.modalidadIngreso", "carrera")
                .leftJoin("po.aulaExamen aex", "aex.aula au", "au.aulaSuperior", "evaluado", "prelamolina")
                .filter("ci.id", ciclo);

        return all(sql);
    }

    @Override
    public Ingresante findByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(Ingresante.class, "ii")
                .join("postulante po", "po.cicloPostula ci", "po.persona", "po.modalidadIngreso", "carrera")
                .leftJoin("po.aulaExamen aex", "aex.aula au", "au.aulaSuperior", "evaluado", "prelamolina")
                .filter("po.id", postulante);

        return find(sql);
    }
}
