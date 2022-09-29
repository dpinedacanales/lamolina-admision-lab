package edu.pe.lamolina.admision.dao.academico.hibernate;

import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.academico.ModalidadEstudioDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;

@Repository
public class ModalidadEstudioDAOH extends AbstractDAO<ModalidadEstudio> implements ModalidadEstudioDAO {

    public ModalidadEstudioDAOH() {
        super();
        setClazz(ModalidadEstudio.class);
    }

    @Override
    public ModalidadEstudio findByCodigo(ModalidadEstudioEnum estado) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("me")
                .filter("me.codigo", estado.name());
        return this.find(sqlUtil);
    }

    @Override
    public List<ModalidadEstudio> allModalidadEstudioAct() {
        return this.all();
    }
}
