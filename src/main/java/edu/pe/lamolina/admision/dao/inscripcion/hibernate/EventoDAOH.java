package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.EventoDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.Evento;

@Repository
public class EventoDAOH extends AbstractDAO<Evento> implements EventoDAO {

    public EventoDAOH() {
        super();
        setClazz(Evento.class);
    }

    @Override
    public Evento findByCode(String evento) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ev")
                .filter("ev.codigo", evento);
        return find(sqlUtil);
    }
}
