package edu.pe.lamolina.admision.dao.vacantes.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.vacantes.ConfiguraVacanteModalidadDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.vacantes.ConfiguraVacanteModalidad;

@Repository
public class ConfiguraVacanteModalidadDAOH extends AbstractDAO<ConfiguraVacanteModalidad> implements ConfiguraVacanteModalidadDAO {

    public ConfiguraVacanteModalidadDAOH() {
        super();
        setClazz(ConfiguraVacanteModalidad.class);
    }

    @Override
    public List<ConfiguraVacanteModalidad> allByCiclo(CicloPostula ciclo) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cbm")
                .parents("cicloPostula cp", "modalidadIngreso mi", "_cp.cicloAcademico")
                .filter("cp.id", ciclo)
                .orderBy("mi.codigo");
        return this.all(sqlUtil);
    }
}
