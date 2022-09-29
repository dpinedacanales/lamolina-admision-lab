package edu.pe.lamolina.admision.dao.cms.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import edu.pe.lamolina.admision.dao.cms.MenuWebDAO;
import pe.edu.lamolina.model.cms.MenuWeb;

@Repository
public class MenuWebDAOH extends AbstractEasyDAO<MenuWeb> implements MenuWebDAO {

    public MenuWebDAOH() {
        super();
        setClazz(MenuWeb.class);
    }

    @Override
    public MenuWeb findByTitulo(String titulo) {
        Octavia octavia = Octavia.query(MenuWeb.class)
                .filter("titulo", titulo);

        return this.find(octavia);
    }

    @Override
    public List<MenuWeb> allByDynatable(DynatableFilter filter) {
        DynatableSql sql = new DynatableSql(filter);
        sql.from(MenuWeb.class, "m");

        return this.all(sql);
    }

    @Override
    public List<MenuWeb> allMenus() {
        Octavia octavia = Octavia.query().
                from(MenuWeb.class, "m");
        return this.all(octavia);
    }

    @Override
    public MenuWeb findById(Long id) {
        Octavia octavia = Octavia.query(MenuWeb.class)
                .filter("id", id);

        return this.find(octavia);
    }
}
