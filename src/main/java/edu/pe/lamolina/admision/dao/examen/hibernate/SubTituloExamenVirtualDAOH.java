package edu.pe.lamolina.admision.dao.examen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.edu.lamolina.model.examen.SubTituloExamen;
import edu.pe.lamolina.admision.dao.examen.SubTituloExamenDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

@Repository
public class SubTituloExamenVirtualDAOH extends AbstractEasyDAO<SubTituloExamen> implements SubTituloExamenDAO {

    public SubTituloExamenVirtualDAOH() {
        super();
        setClazz(SubTituloExamen.class);
    }

    @Override
    public List<SubTituloExamen> allByTemaExamenVirtual(TemaExamenVirtual temaExamenVirtual) {
        Octavia sql = Octavia.query()
                .from(SubTituloExamen.class, "se")
                .join("temaExamen te")
                .filter("te.id", temaExamenVirtual)
                .orderBy("se.orden");

        return all(sql);
    }

}
