package edu.pe.lamolina.admision.dao.cms;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.cms.Widget;

public interface WidgetDAO extends EasyDAO<Widget> {

    Widget findByTitulo(String titulo);

    List<Widget> allByDynatable(DynatableFilter filter);

}
