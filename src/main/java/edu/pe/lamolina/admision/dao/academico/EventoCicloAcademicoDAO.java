package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;

public interface EventoCicloAcademicoDAO extends EasyDAO<EventoCicloAcademico> {

    public List<EventoCicloAcademico> findByCicloTipo(CicloAcademico cicloActivo, String mat);

    EventoCicloAcademico findActivoByCicloTipoEvento(CicloAcademico cicloAcademico, EventoAcademicoEnum eventoAcademicoEnum);

}
