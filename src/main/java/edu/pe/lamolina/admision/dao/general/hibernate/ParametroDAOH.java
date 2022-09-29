package edu.pe.lamolina.admision.dao.general.hibernate;

import edu.pe.lamolina.admision.dao.general.ParametroDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.AmbienteAplicacionEnum;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.seguridad.Sistema;

@Repository
public class ParametroDAOH extends AbstractEasyDAO<Parametro> implements ParametroDAO {

    public ParametroDAOH() {
        super();
        setClazz(Parametro.class);
    }

    @Override
    public Parametro findBySistemaAmbienteParametrosSistemas(Sistema sistema, AmbienteAplicacionEnum ambiente, ParametrosSistemasEnum parametrosSistemas) {
        Octavia sql = Octavia.query()
                .from(Parametro.class, "p")
                .filter("ambiente", ambiente)
                .filter("contexto", parametrosSistemas.getContexto())
                .filter("parametro", parametrosSistemas.getParametro());

        return (Parametro) sql.find(getCurrentSession());
    }
}
