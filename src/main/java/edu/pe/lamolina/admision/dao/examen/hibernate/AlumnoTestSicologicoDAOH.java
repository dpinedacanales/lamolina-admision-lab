package edu.pe.lamolina.admision.dao.examen.hibernate;

import org.springframework.stereotype.Repository;
import pe.edu.lamolina.model.examen.AlumnoTestSicologico;
import edu.pe.lamolina.admision.dao.examen.AlumnoTestSicologicoDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;

@Repository
public class AlumnoTestSicologicoDAOH extends AbstractEasyDAO<AlumnoTestSicologico> implements AlumnoTestSicologicoDAO {

    public AlumnoTestSicologicoDAOH() {
        super();
        setClazz(AlumnoTestSicologico.class);
    }

    @Override
    public AlumnoTestSicologico findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(AlumnoTestSicologico.class, "ats")
                .join("alumno a", "cicloAcademico cic")
                .filter("a.id", alumno)
                .filter("cic.id", cicloAcademico);
        return find(sql);
    }

}
