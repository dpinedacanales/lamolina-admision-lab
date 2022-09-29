package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;

public interface TipoActividadIngresanteDAO extends EasyDAO<TipoActividadIngresante> {

    public TipoActividadIngresante findByCodigo(TipoActividadIngresanteEnum tipoActividadIngresanteEnum);

}
