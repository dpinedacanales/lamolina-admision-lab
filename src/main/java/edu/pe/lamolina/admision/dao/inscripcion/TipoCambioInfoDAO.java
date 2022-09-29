package edu.pe.lamolina.admision.dao.inscripcion;

import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.TipoCambioInfoEnum;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;

public interface TipoCambioInfoDAO extends EasyDAO<TipoCambioInfo> {

    TipoCambioInfo findByEnum(TipoCambioInfoEnum tipoCambioInfoEnum);

    TipoCambioInfo findByCodigo(TipoCambioInfoEnum codigo);

}
