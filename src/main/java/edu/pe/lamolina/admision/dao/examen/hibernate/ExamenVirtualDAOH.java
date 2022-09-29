package edu.pe.lamolina.admision.dao.examen.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.examen.ExamenVirtualDAO;
import pe.albatross.octavia.Octavia;
import pe.edu.lamolina.model.enums.EvaluacionVirtualEstadoEnum;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.ExamenVirtualInteresado;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.EncuestaCiclo;

@Repository
public class ExamenVirtualDAOH extends AbstractDAO<ExamenVirtual> implements ExamenVirtualDAO {

    public ExamenVirtualDAOH() {
        super();
        setClazz(ExamenVirtual.class);
    }

    @Override
    public ExamenVirtual findEncuestaByCicloPostula(CicloPostula ciclo, String tipoEncuesta) {

        Octavia sql = Octavia.query()
                .select("exv")
                .from(EncuestaCiclo.class, "ec")
                .join("cicloPostula cp", "examenVirtual exv", "exv.tipoExamen te")
                .filter("cp.id", ciclo)
                .filter("te.codigo", tipoEncuesta);

        return (ExamenVirtual) sql.find(getCurrentSession());

    }

    @Override
    public ExamenVirtual findExamenByCicloPostula(CicloPostula ciclo, String tipoExamen) {

        Octavia sql = Octavia.query()
                .select("exv")
                .from(ExamenVirtualInteresado.class, "ec")
                .join("cicloPostula cp", "examenVirtual exv", "exv.tipoExamen te")
                .filter("cp.id", ciclo)
                .filter("te.codigo", tipoExamen);

        return (ExamenVirtual) sql.find(getCurrentSession());

    }

    @Override
    public ExamenVirtual findEncuestaActivaByCicloPostula(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .select("exv")
                .from(EncuestaCiclo.class, "ec")
                .join("cicloPostula cp", "examenVirtual exv", "exv.tipoExamen te")
                .filter("cp.id", ciclo)
                .filter("te.codigo", TipoExamenVirtualEnum.ENC)
                .filter("exv.estado", EvaluacionVirtualEstadoEnum.ACT);

        return (ExamenVirtual) sql.find(getCurrentSession());
    }

}
