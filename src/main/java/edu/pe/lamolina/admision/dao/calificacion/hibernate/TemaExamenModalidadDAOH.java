package edu.pe.lamolina.admision.dao.calificacion.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.calificacion.TemaExamenModalidadDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.calificacion.TemaExamenModalidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

@Repository
public class TemaExamenModalidadDAOH extends AbstractEasyDAO<TemaExamenModalidad> implements TemaExamenModalidadDAO {

    public TemaExamenModalidadDAOH() {
        super();
        setClazz(TemaExamenModalidad.class);
    }

    @Override
    public List<TemaExamenModalidad> allByCicloModalidad(CicloPostula cicloPostula, ModalidadIngreso modalidadIngreso) {
        Octavia sql = Octavia.query()
                .from(TemaExamenModalidad.class, "te")
                .join("cicloPostula cp", "modalidadIngreso mi")
                .filter("cp.id", cicloPostula)
                .filter("mi.id", modalidadIngreso);

        return all(sql);
    }
}
