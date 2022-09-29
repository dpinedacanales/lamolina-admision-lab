package edu.pe.lamolina.admision.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.zelpers.dao.SqlUtil;
import edu.pe.lamolina.admision.dao.academico.GradoSecundariaDAO;
import pe.edu.lamolina.model.general.GradoSecundaria;

@Repository
public class GradoSecundariaDAOH extends AbstractDAO<GradoSecundaria> implements GradoSecundariaDAO {

    public GradoSecundariaDAOH() {
        super();
        setClazz(GradoSecundaria.class);
    }

    @Override
    public List<GradoSecundaria> all() {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("gra")
                .orderBy("gra.orden asc");
        return this.all(sqlUtil);

    }

}
