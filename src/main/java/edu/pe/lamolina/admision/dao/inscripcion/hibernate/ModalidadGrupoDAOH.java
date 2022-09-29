package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.ModalidadGrupoDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.AgrupacionModalidades;
import pe.edu.lamolina.model.inscripcion.ModalidadGrupo;

@Repository
public class ModalidadGrupoDAOH extends AbstractDAO<ModalidadGrupo> implements ModalidadGrupoDAO {

    public ModalidadGrupoDAOH() {
        super();
        setClazz(ModalidadGrupo.class);
    }

    @Override
    public List<ModalidadGrupo> allByAgrupaciones(List<AgrupacionModalidades> agrupaciones) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mg")
                .parents("modalidadIngreso mi", "agrupacionModalidades agm")
                .filterIn("agm.id", agrupaciones);
        return all(sqlUtil);
    }

    @Override
    public List<ModalidadGrupo> allByAgrupacion(AgrupacionModalidades agrupa) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("mg")
                .parents("modalidadIngreso mi", "agrupacionModalidades agm")
                .filter("agm.id", agrupa);
        return all(sqlUtil);
    }
}
