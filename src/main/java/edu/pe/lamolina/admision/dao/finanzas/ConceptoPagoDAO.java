package edu.pe.lamolina.admision.dao.finanzas;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.TipoCambioInfo;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;

public interface ConceptoPagoDAO extends EasyDAO<ConceptoPago> {

    ConceptoPago findByModalidad(ModalidadIngreso modalidad);

    List<ConceptoPago> allByModalidad(ModalidadIngreso modalidad);

    List<ConceptoPago> allSinOrigenByModalidad(ModalidadIngreso modalidad);

    List<ConceptoPago> allConceptos();

    List<ConceptoPago> allSinOrigen();

    ConceptoPago findByCodigo(String codigo);

    List<ConceptoPago> allByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula cicloPostula);

    List<ConceptoPago> allByTipoCambio(List<TipoCambioInfo> tipos);

}
