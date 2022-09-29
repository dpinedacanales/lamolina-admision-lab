package edu.pe.lamolina.admision.dao.examen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

public interface SubTituloExamenDAO extends EasyDAO<SubTituloExamen> {

    List<SubTituloExamen> allByTemaExamenVirtual(TemaExamenVirtual temaExamenVirtual);

}
