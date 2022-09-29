package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.inscripcion.InscritoTallerDAO;
import java.util.Arrays;
import java.util.List;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.CicloAcademicoEstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.enums.EstadoInscritoEnum;
import pe.edu.lamolina.model.inscripcion.InscritoTaller;
import pe.edu.lamolina.model.inscripcion.Interesado;

@Repository
public class InscritoTallerDAOH extends AbstractEasyDAO<InscritoTaller> implements InscritoTallerDAO {

    public InscritoTallerDAOH() {
        super();
        setClazz(InscritoTaller.class);
    }

    @Override
    public InscritoTaller findByInteresadoTaller(Interesado interesado, Long taller) {
        Octavia sql = Octavia.query(InscritoTaller.class, "it")
                .join("interesado inte", "taller ta")
                .filter("inte.id", interesado)
                .filter("ta.id", taller);

        return find(sql);
    }

    @Override
    public List<InscritoTaller> allByInteresado(Interesado interesado) {
        Octavia sql = Octavia.query(InscritoTaller.class, "it")
                .join("interesado i", "taller t", "t.cicloPostula cp")
                .filter("cp.estado", CicloAcademicoEstadoEnum.ACT)
                .filter("t.estado", ACT)
                .filter("i.id", interesado)
                .in("it.estado", Arrays.asList(EstadoInscritoEnum.ACT, EstadoInscritoEnum.INS))
                .orderBy("t.fecha asc");

        return all(sql);
    }

}
