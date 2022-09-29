package edu.pe.lamolina.admision.dao.cms;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.cms.Post;

public interface PostDAO extends EasyDAO<Post> {

    Post findByURL(String pageURL);

    List<Post> allByDynatable(DynatableFilter filter);

}
