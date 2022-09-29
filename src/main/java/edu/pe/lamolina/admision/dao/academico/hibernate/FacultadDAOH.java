package edu.pe.lamolina.admision.dao.academico.hibernate;


import edu.pe.lamolina.admision.dao.academico.FacultadDAO;
import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EstadoEnum;

@Repository
public class FacultadDAOH extends AbstractEasyDAO<Facultad> implements FacultadDAO {

    @Override
    public List<Facultad> all() {
        Octavia sql = Octavia.query(Facultad.class, "fac")
                .filter("estado", EstadoEnum.ACT);
        
        return all(sql);
    }
    
}
