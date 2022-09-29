package edu.pe.lamolina.admision.dao.finanzas;

import java.math.BigDecimal;
import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface ConceptoPrecioDAO extends Crud<ConceptoPrecio> {

    List<ConceptoPrecio> allByCicloImporte(CicloPostula ciclo, BigDecimal importe);

    ConceptoPrecio findByConceptoCiclo(ConceptoPago concepto, CicloPostula ciclo);

    ConceptoPrecio find(Long id);

    ConceptoPrecio findByCodigoConceptoPago(String CODIGO_ANU);

    List<ConceptoPrecio> allCambiosByCiclo(CicloPostula ciclo, Integer ai, Integer ae);

    List<ConceptoPrecio> allModalidadByCicloPostula(CicloPostula cicloPostula);

    ConceptoPrecio findByCicloCodigoConceptoPago(CicloPostula cicloPostula, String CODE_PRUEBAS);

}
