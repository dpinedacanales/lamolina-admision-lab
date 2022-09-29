package edu.pe.lamolina.admision.controller.website;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import edu.pe.lamolina.admision.dao.cms.DatalistDAO;
import edu.pe.lamolina.admision.dao.cms.PostDAO;
import edu.pe.lamolina.admision.dao.cms.SitioDAO;
import edu.pe.lamolina.admision.dao.cms.WidgetDAO;
import pe.edu.lamolina.model.cms.Datalist;
import pe.edu.lamolina.model.cms.Post;
import pe.edu.lamolina.model.cms.Widget;

@Service
@Transactional
public class WebsiteServiceImp implements WebsiteService {

    @Autowired
    SitioDAO siteDAO;

    @Autowired
    PostDAO postDAO;

    @Autowired
    WidgetDAO widgetDAO;

    @Autowired
    DatalistDAO datalistDAO;

    @Override
    public Post findPostByURL(String pageURL) {
        return postDAO.findByURL(pageURL);
    }

    @Override
    public Widget findWidget(String titulo) {
        return widgetDAO.findByTitulo(titulo);
    }

    @Override
    public List<Datalist> allDatalist() {
        return datalistDAO.all();
    }

}
