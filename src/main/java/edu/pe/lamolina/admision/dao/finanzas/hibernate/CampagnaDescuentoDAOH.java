package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.CampagnaDescuentoDAO;
import java.util.Date;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.finanzas.CampagnaDescuento;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

@Repository
public class CampagnaDescuentoDAOH extends AbstractEasyDAO<CampagnaDescuento> implements CampagnaDescuentoDAO {

    public CampagnaDescuentoDAOH() {
        super();
        setClazz(CampagnaDescuento.class);
    }

    @Override
    public CampagnaDescuento findByCiclo(CicloPostula ciclo, ModalidadIngreso modalidad, Date hoy, EstadoEnum estadoEnum) {
        Octavia sql = Octavia.query(CampagnaDescuento.class, "cd")
                .join("cicloPostula cp")
                .leftJoin("modalidadIngreso mi")
                .filter("mi.id", modalidad)
                .filter("cp.id", ciclo)
                .filter("cd.fechaInicio", "<=", hoy)
                .filter("cd.fechaFin", ">=", hoy)
                .filter("cd.estado", estadoEnum.name());
        return find(sql);
    }

}
