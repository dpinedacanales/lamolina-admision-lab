package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.EventoCicloDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;

@Repository
public class EventoCicloDAOH extends AbstractEasyDAO<EventoCiclo> implements EventoCicloDAO {

    public EventoCicloDAOH() {
        super();
        setClazz(EventoCiclo.class);
    }

    @Override
    public List<EventoCiclo> allByFechaCiclo(Date fecha, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .filter("evci.fechaInicio", "<=", fecha)
                .filter("evci.fechaFin", ">=", fecha);

        return all(sql);
    }

    @Override
    public List<EventoCiclo> allEventoInscripcionesByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .in("ev.codigo", Arrays.asList("INSC", "OTR", "EXTM"))
                .orderBy("evci.fechaFin desc");

        return all(sql);
    }

    @Override
    public List<EventoCiclo> allByEventoCiclo(Evento evento, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .filter("ev.id", evento)
                .orderBy("evci.fechaFin asc");

        return all(sql);
    }

    @Override
    public List<EventoCiclo> allByCodesEventosCiclo(List<String> codesEventos, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(EventoCiclo.class, "evci")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .in("ev.codigo", codesEventos)
                .orderBy("evci.fechaFin desc");

        return all(sql);
    }

    @Override
    public EventoCiclo findActive(CicloPostula ciclo, Date today, EventoEnum eventoEnum) {
        Octavia sql = Octavia.query(EventoCiclo.class, "ec")
                .join("evento ev", "cicloPostula cp")
                .filter("cp.id", ciclo)
                .filter("ev.codigo", eventoEnum)
                .filter("ec.fechaFin", ">", today);
        return (EventoCiclo) sql.find(getCurrentSession());
    }

    @Override
    public List<EventoCiclo> allVisibleMovil(CicloPostula ciclo) {
        Octavia sql = Octavia.query(EventoCiclo.class, "ec")
                .join("evento ev", "cicloPostula cp")
                .filter("cp.id", ciclo)
                .filter("ec.visibleMovil", true)
                .orderBy("ec.fechaFin desc");

        return sql.all(getCurrentSession());
    }

    @Override
    public EventoCiclo findByCicloEvento(CicloPostula ciclo, EventoEnum eventoEnum) {
        Octavia sql = Octavia.query(EventoCiclo.class, "ec")
                .join("evento ev", "cicloPostula ci")
                .filter("ci.id", ciclo)
                .filter("ev.codigo", eventoEnum)
                .orderBy("ec.fechaFin desc");

        return (EventoCiclo) sql.find(getCurrentSession());
    }

}
