package edu.pe.lamolina.admision.dao.examen.hibernate;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import edu.pe.lamolina.admision.dao.examen.BloquePreguntasDAO;
import pe.edu.lamolina.model.examen.SubTituloExamen;

@Repository
public class BloquePreguntasDAOH extends AbstractDAO<BloquePreguntas> implements BloquePreguntasDAO {

    public BloquePreguntasDAOH() {
        super();
        setClazz(BloquePreguntas.class);
    }

    @Override
    public List<BloquePreguntas> allByTituloExamenVirtual(SubTituloExamen subTituloExamen) {
        Criteria criteria = getCurrentSession().createCriteria(BloquePreguntas.class);
        criteria.add(Restrictions.eq("subTituloExamen", subTituloExamen));
        return criteria.list();
    }

}
