package edu.pe.lamolina.admision.dao.obu.hibernate;

import edu.pe.lamolina.admision.dao.obu.DocumentoBienestarDAO;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bienestar.DocumentoBienestar;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;

@Repository
public class DocumentoBienestarDAOH extends AbstractEasyDAO<DocumentoBienestar> implements DocumentoBienestarDAO {
    
    public DocumentoBienestarDAOH() {
        super();
        setClazz(DocumentoBienestar.class);
    }
    
    @Override
    public List<DocumentoBienestar> allByCicloAcademico(CicloAcademico cicloAcademico, TipoTramiteEnum tipoTramiteEnum) {
        Octavia sql = Octavia.query()
                .from(DocumentoBienestar.class, "cmc")
                .join("tipoTramite tt", "cicloAcademico ca")
                .left("tipoSubvencion tSu")
                .filter("ca.id", cicloAcademico)
                .filter("tt.codigo", tipoTramiteEnum);
        return all(sql);
    }
    
}
