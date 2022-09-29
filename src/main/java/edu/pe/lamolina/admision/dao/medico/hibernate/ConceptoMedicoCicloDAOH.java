package edu.pe.lamolina.admision.dao.medico.hibernate;

import edu.pe.lamolina.admision.dao.medico.ConceptoMedicoCicloDAO;
import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.PagoMedicoEnum;
import pe.edu.lamolina.model.medico.ConceptoMedicoCiclo;

@Repository
public class ConceptoMedicoCicloDAOH extends AbstractEasyDAO<ConceptoMedicoCiclo> implements ConceptoMedicoCicloDAO {

    public ConceptoMedicoCicloDAOH() {
        super();
        setClazz(ConceptoMedicoCiclo.class);
    }

    @Override
    public List<ConceptoMedicoCiclo> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ConceptoMedicoCiclo.class, "cmc")
                .join("conceptoMedico cm", "cicloAcademico ca")
                .searchFields("cm.nombre", "cm.codigo", "ca.descripcion2", "ca.descripcion")
                .orderBy("ca.year", "cm.nombre asc");
        return all(sql);
    }

    @Override
    public List<ConceptoMedicoCiclo> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .from(ConceptoMedicoCiclo.class, "cmc")
                .join("conceptoMedico cm", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico);
        return all(sql);
    }

    @Override
    public ConceptoMedicoCiclo findByCicloCodigoEnum(CicloAcademico cicloAcademico, PagoMedicoEnum pagoMedicoEnum) {
        Octavia sql = Octavia.query()
                .from(ConceptoMedicoCiclo.class, "cmc")
                .join("conceptoMedico cm", "cicloAcademico ca")
                .filter("ca.id", cicloAcademico)
                .filter("cm.codigo", pagoMedicoEnum.name());
        return find(sql);
    }

}
