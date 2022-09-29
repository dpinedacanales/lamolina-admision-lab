package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface ActividadIngresanteDAO extends EasyDAO<ActividadIngresante> {

    public void deleteByCiclo(CicloPostula ciclo);

    public List<ActividadIngresante> allByRecorridoIngresantes(List<RecorridoIngresante> recorridoIngresantes);

    public ActividadIngresante findByRecorridoTipo(TipoActividadIngresante tipoActividadIngresante, RecorridoIngresante recorridoIngresante);

    public ActividadIngresante findByRecorridoCodigoTipo(TipoActividadIngresanteEnum tipoActividadIngresanteEnum, RecorridoIngresante recorridoIngresante);

    List<ActividadIngresante> allByRecorridoIngresante(RecorridoIngresante recorridoIngresante);
}
