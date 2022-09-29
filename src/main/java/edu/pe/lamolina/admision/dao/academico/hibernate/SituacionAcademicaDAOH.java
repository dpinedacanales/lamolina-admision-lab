package edu.pe.lamolina.admision.dao.academico.hibernate;

import edu.pe.lamolina.admision.dao.academico.SituacionAcademicaDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.SituacionAcademica;

@Repository
public class SituacionAcademicaDAOH extends AbstractEasyDAO<SituacionAcademica> implements SituacionAcademicaDAO {

    public SituacionAcademicaDAOH() {
        super();
        setClazz(SituacionAcademica.class);
    }

    @Override
    public SituacionAcademica findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(SituacionAcademica.class, "sa")
                .filter("sa.codigo", codigo);
        return find(sql);
    }

}
