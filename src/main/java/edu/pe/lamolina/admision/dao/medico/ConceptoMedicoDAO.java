package edu.pe.lamolina.admision.dao.medico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.medico.ConceptoMedico;

public interface ConceptoMedicoDAO extends EasyDAO<ConceptoMedico> {

    List<ConceptoMedico> allConceptoMedico();

    List<ConceptoMedico> allByDynatable(DynatableFilter filter);

}
