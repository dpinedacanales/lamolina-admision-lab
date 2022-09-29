package edu.pe.lamolina.admision.dao.examen.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.examen.RespuestaInteresadoDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.ExamenInteresado;
import pe.edu.lamolina.model.inscripcion.RespuestaInteresado;

@Repository
public class RespuestaInteresadoDAOH extends AbstractDAO<RespuestaInteresado> implements RespuestaInteresadoDAO {

    public RespuestaInteresadoDAOH() {
        super();
        setClazz(RespuestaInteresado.class);
    }

    @Override
    public List<RespuestaInteresado> allByInteresado(ExamenInteresado ei) {

        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ri")
                .parents("examenInteresado ei", "left opcion op", "pregunta pre")
                .filter("ei.id", ei);
        return all(sqlUtil);
    }
}
