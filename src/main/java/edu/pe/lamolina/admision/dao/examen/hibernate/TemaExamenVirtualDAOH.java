package edu.pe.lamolina.admision.dao.examen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.examen.TemaExamenVirtualDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TemaExamenVirtual;

@Repository
public class TemaExamenVirtualDAOH extends AbstractEasyDAO<TemaExamenVirtual> implements TemaExamenVirtualDAO {

    public TemaExamenVirtualDAOH() {
        super();
        setClazz(TemaExamenVirtual.class);
    }

    @Override
    public List<TemaExamenVirtual> allByExamenCodigo(ExamenVirtual evaluacion, List<String> codigoTemas) {
        Octavia sql = Octavia.query()
                .from(TemaExamenVirtual.class, "tev")
                .join("examenVirtual ev")
                .filter("ev.id", evaluacion)
                .in("codigo", codigoTemas)
                .orderBy("tev.orden");

        return all(sql);

    }

}
