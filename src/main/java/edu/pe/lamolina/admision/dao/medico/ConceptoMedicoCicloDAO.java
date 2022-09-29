package edu.pe.lamolina.admision.dao.medico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.PagoMedicoEnum;
import pe.edu.lamolina.model.medico.ConceptoMedicoCiclo;

public interface ConceptoMedicoCicloDAO extends EasyDAO<ConceptoMedicoCiclo> {

    List<ConceptoMedicoCiclo> allByDynatable(DynatableFilter filter);

    List<ConceptoMedicoCiclo> allByCicloAcademico(CicloAcademico cicloAcademico);

    ConceptoMedicoCiclo findByCicloCodigoEnum(CicloAcademico cicloAcademico, PagoMedicoEnum pagoMedicoEnum);

}
