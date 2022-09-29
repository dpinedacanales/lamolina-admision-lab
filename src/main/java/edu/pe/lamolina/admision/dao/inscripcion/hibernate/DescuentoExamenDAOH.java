package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.DescuentoExamenDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.inscripcion.DescuentoExamen;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

@Repository
public class DescuentoExamenDAOH extends AbstractDAO<DescuentoExamen> implements DescuentoExamenDAO {

    public DescuentoExamenDAOH() {
        super();
        setClazz(DescuentoExamen.class);
    }

    @Override
    public List<DescuentoExamen> allByModalidad(ModalidadIngreso modalidad, String idGestion) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("de")
                .parents("modalidadIngreso mi")
                .filter("mi.id", modalidad)
                .filter("de.gestionColegio", idGestion);
        return all(sqlUtil);
    }
}
