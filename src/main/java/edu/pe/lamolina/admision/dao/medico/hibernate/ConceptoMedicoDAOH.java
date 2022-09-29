package edu.pe.lamolina.admision.dao.medico.hibernate;

import edu.pe.lamolina.admision.dao.medico.ConceptoMedicoDAO;
import java.util.List;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.medico.ConceptoMedico;

@Repository
public class ConceptoMedicoDAOH extends AbstractEasyDAO<ConceptoMedico> implements ConceptoMedicoDAO {

    public ConceptoMedicoDAOH() {
        super();
        setClazz(ConceptoMedico.class);
    }

    @Override
    public List<ConceptoMedico> allConceptoMedico() {
        Octavia sql = Octavia.query()
                .from(ConceptoMedico.class, "cm")
                .join("cuentaBancaria cb")
                .orderBy("cm.nombre asc");
        return all(sql);
    }

    @Override
    public List<ConceptoMedico> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter)
                .from(ConceptoMedico.class, "cm")
                .join("cuentaBancaria cb")
                .searchFields("cm.nombre", "cm.codigo", "cb.numero", "cb.nombre")
                .orderBy("cm.nombre asc");
        return all(sql);
    }

}
