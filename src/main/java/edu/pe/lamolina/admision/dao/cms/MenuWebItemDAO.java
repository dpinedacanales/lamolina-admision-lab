package edu.pe.lamolina.admision.dao.cms;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.cms.MenuWeb;
import pe.edu.lamolina.model.cms.MenuWebItem;

public interface MenuWebItemDAO extends EasyDAO<MenuWebItem> {

    MenuWebItem findByTitulo(String titulo);

    MenuWebItem findById(Long id);

    List<MenuWebItem> allByDynatable(DynatableFilter filter, MenuWeb menu);

    List<MenuWebItem> allMenusItems(MenuWeb menu);
}
