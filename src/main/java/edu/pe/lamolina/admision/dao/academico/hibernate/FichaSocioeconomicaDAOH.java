package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.FichaSocioeconomicaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.socioeconomico.FichaSocioeconomica;

@Repository
public class FichaSocioeconomicaDAOH extends AbstractEasyDAO<FichaSocioeconomica> implements FichaSocioeconomicaDAO {

    public FichaSocioeconomicaDAOH() {
        super();
        setClazz(FichaSocioeconomica.class);
    }

    @Override
    public FichaSocioeconomica findByAlumnoCiclo(Alumno alumno, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(FichaSocioeconomica.class, "fsec")
                .join("alumno alu", "cicloAcademico ca")
                .filter("alu.id", alumno)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }
}
