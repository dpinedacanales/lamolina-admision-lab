package edu.pe.lamolina.admision.controller.website;

import java.util.List;
import pe.edu.lamolina.model.cms.Datalist;
import pe.edu.lamolina.model.cms.Post;
import pe.edu.lamolina.model.cms.Widget;

public interface WebsiteService {

    Post findPostByURL(String pageURL);

    Widget findWidget(String titulo);

    List<Datalist> allDatalist();

}
