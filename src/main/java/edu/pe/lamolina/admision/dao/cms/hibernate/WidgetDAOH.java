package edu.pe.lamolina.admision.dao.cms.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import edu.pe.lamolina.admision.dao.cms.WidgetDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.cms.Widget;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;

@Repository
public class WidgetDAOH extends AbstractEasyDAO<Widget> implements WidgetDAO {

    public WidgetDAOH() {
        super();
        setClazz(Widget.class);
    }

    @Override
    public Widget findByTitulo(String titulo) {
        Octavia octavia = Octavia.query(Widget.class, "w")
                .join("bloque b", "b.sitio s")
                .filter("titulo", titulo)
                .filter("s.id", AdmisionConstantine.SITIO_ADMISION);

        return this.find(octavia);
    }

    @Override
    public List<Widget> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter);
        sql.from(Widget.class, "w")
                .join("bloque b", "b.sitio s")
                .searchFields("w.titulo")
                .filter("s.id", AdmisionConstantine.SITIO_ADMISION);
        
        return this.all(sql);
    }

}
