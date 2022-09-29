package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.AbonoPostulanteDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.finanzas.AbonoPostulante;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class AbonoPostulanteDAOH extends AbstractDAO<AbonoPostulante> implements AbonoPostulanteDAO {

    public AbonoPostulanteDAOH() {
        super();
        setClazz(AbonoPostulante.class);
    }

    @Override
    public List<AbonoPostulante> allByPostulante(Postulante postul) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ap")
                .parents("postulante po")
                .filter("po.id", postul);
        return all(sqlUtil);
    }

    @Override
    public AbonoPostulante findByItemCarga(ItemCargaAbono item) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ap")
                .parents("postulante po", "abono ab")
                .filter("ab.id", item);
        return find(sqlUtil);
    }

    @Override
    public AbonoPostulante findByPostulante(Postulante postulatenBD) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("ap")
                .parents("postulante po")
                .filter("po.id", postulatenBD);
        return find(sqlUtil);
    }
}
