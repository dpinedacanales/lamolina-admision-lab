package edu.pe.lamolina.admision.dao.academico.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.academico.EventoCicloAcademicoDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;

@Repository
public class EventoCicloAcademicoDAOH extends AbstractEasyDAO<EventoCicloAcademico> implements EventoCicloAcademicoDAO {

    public EventoCicloAcademicoDAOH() {
        super();
        setClazz(EventoCicloAcademico.class);
    }

    @Override
    public List<EventoCicloAcademico> findByCicloTipo(CicloAcademico cicloActivo, String tipoEvento) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("cicloAcademico ca", "eventoAcademico ea")
                .filter("ea.tipo", tipoEvento);
        return sql.all(getCurrentSession());
    }

    @Override
    public EventoCicloAcademico findActivoByCicloTipoEvento(CicloAcademico cicloAcademico, EventoAcademicoEnum eventoAcademicoEnum) {
        Octavia sql = Octavia.query()
                .from(EventoCicloAcademico.class, "eca")
                .join("eventoAcademico ea", "cicloAcademico ca")
                .filter("ea.codigo", eventoAcademicoEnum)
                .filter("ca.id", cicloAcademico);
        return find(sql);
    }

}
