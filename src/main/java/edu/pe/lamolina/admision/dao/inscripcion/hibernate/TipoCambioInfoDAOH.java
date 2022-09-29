package edu.pe.lamolina.admision.dao.inscripcion.hibernate;

import edu.pe.lamolina.admision.dao.inscripcion.TipoCambioInfoDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.TipoCambioInfoEnum;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;

@Repository
public class TipoCambioInfoDAOH extends AbstractEasyDAO<TipoCambioInfo> implements TipoCambioInfoDAO {

    public TipoCambioInfoDAOH() {
        super();
        setClazz(TipoCambioInfo.class);
    }

    @Override
    public TipoCambioInfo findByEnum(TipoCambioInfoEnum tipoCambioInfoEnum) {
        Octavia sql = Octavia.query(TipoCambioInfo.class)
                .filter("codigo", tipoCambioInfoEnum);
        return find(sql);
    }

    @Override
    public TipoCambioInfo findByCodigo(TipoCambioInfoEnum codigo) {
        Octavia sql = Octavia.query(TipoCambioInfo.class)
                .filter("codigo", codigo);
        return find(sql);
    }

}
