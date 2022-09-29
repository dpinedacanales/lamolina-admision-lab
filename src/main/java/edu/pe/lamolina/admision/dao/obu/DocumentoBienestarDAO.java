package edu.pe.lamolina.admision.dao.obu;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bienestar.DocumentoBienestar;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;

public interface DocumentoBienestarDAO extends EasyDAO<DocumentoBienestar> {

    List<DocumentoBienestar> allByCicloAcademico(CicloAcademico cicloAcademico, TipoTramiteEnum tipoTramiteEnum);

}
