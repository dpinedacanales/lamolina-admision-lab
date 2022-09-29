package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.Date;
import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.EventoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Evento;
import pe.edu.lamolina.model.inscripcion.EventoCiclo;

public interface EventoCicloDAO extends EasyDAO<EventoCiclo> {

    List<EventoCiclo> allByFechaCiclo(Date fecha, CicloPostula ciclo);

    List<EventoCiclo> allEventoInscripcionesByCiclo(CicloPostula ciclo);

    List<EventoCiclo> allByEventoCiclo(Evento evento, CicloPostula ciclo);

    List<EventoCiclo> allByCodesEventosCiclo(List<String> codeEventos, CicloPostula ciclo);

    EventoCiclo findActive(CicloPostula ciclo, Date today, EventoEnum eventoEnum);

    EventoCiclo findByCicloEvento(CicloPostula ciclo, EventoEnum eventoEnum);

    List<EventoCiclo> allVisibleMovil(CicloPostula ciclo);

}
