package edu.pe.lamolina.admision.dao.examen;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

public interface TemaExamenVirtualDAO extends EasyDAO<TemaExamenVirtual> {

    List<TemaExamenVirtual> allByExamenCodigo(ExamenVirtual evaluacion, List<String> codigoTemas);

}
