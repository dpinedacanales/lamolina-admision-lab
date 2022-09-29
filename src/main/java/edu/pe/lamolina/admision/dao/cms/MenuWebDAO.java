package edu.pe.lamolina.admision.dao.cms;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.cms.MenuWeb;

public interface MenuWebDAO extends EasyDAO<MenuWeb> {

    MenuWeb findByTitulo(String titulo);

    List<MenuWeb> allByDynatable(DynatableFilter filter);

    List<MenuWeb> allMenus();

    MenuWeb findById(Long id);

}
