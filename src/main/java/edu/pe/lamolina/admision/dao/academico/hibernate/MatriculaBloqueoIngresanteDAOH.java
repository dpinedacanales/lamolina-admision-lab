package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.MatriculaBloqueoIngresanteDAO;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaBloqueoIngresante;
import pe.edu.lamolina.model.inscripcion.Ingresante;

@Repository
public class MatriculaBloqueoIngresanteDAOH extends AbstractEasyDAO<MatriculaBloqueoIngresante> implements MatriculaBloqueoIngresanteDAO {

    public MatriculaBloqueoIngresanteDAOH() {
        super();
        setClazz(MatriculaBloqueoIngresante.class);
    }

    @Override
    public List<MatriculaBloqueoIngresante> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(MatriculaBloqueoIngresante.class, "mbi")
                .join("cicloAcademico ca", "ingresante i", "i.postulante p", "i.carrera car", "p.modalidadIngreso mi", "p.cicloPostula cp", "cp.cicloAcademico")
                .filter("ca.id", cicloAcademico)
                .orderBy("mi.nombre","car.nombre");
        return sql.all(getCurrentSession());
    }

    @Override
    public MatriculaBloqueoIngresante findByCicloAcademicoIngresante(CicloAcademico cicloAcademico, Ingresante ingresante) {
        Octavia sql = Octavia.query()
                .from(MatriculaBloqueoIngresante.class, "mbi")
                .join("cicloAcademico ca", "ingresante i", "i.postulante p", "i.carrera car", "p.modalidadIngreso mi", "p.cicloPostula cp", "cp.cicloAcademico")
                .filter("ca.id", cicloAcademico)
                .filter("i.id", ingresante)
                .orderBy("mi.nombre","car.nombre");
        return (MatriculaBloqueoIngresante) sql.find(getCurrentSession());
    }

}
