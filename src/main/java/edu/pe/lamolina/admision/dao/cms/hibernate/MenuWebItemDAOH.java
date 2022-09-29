package edu.pe.lamolina.admision.dao.cms.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import edu.pe.lamolina.admision.dao.cms.MenuWebItemDAO;
import pe.edu.lamolina.model.cms.MenuWeb;
import pe.edu.lamolina.model.cms.MenuWebItem;

@Repository
public class MenuWebItemDAOH extends AbstractEasyDAO<MenuWebItem> implements MenuWebItemDAO {

    public MenuWebItemDAOH() {
        super();
        setClazz(MenuWebItem.class);
    }

    @Override
    public MenuWebItem findByTitulo(String titulo) {
        Octavia octavia = Octavia.query(MenuWebItem.class)
                .filter("titulo", titulo);

        return this.find(octavia);
    }

    @Override
    public MenuWebItem findById(Long id) {
        Octavia octavia = Octavia.query(MenuWebItem.class)
                .filter("id", id);

        return this.find(octavia);
    }

    @Override
    public List<MenuWebItem> allByDynatable(DynatableFilter filter, MenuWeb menu) {
        DynatableSql sql = new DynatableSql(filter)
                .from(MenuWebItem.class, "mi")
                .join("menu m")
                .filter("m.id", menu);

        return sql.all(getCurrentSession());
    }

    @Override
    public List<MenuWebItem> allMenusItems(MenuWeb menu) {
        Octavia octavia = Octavia.query().
                from(MenuWebItem.class, "mi")
                .join("menu m")
                .filter("m.id", menu);

        return this.all(octavia);
    }
}
