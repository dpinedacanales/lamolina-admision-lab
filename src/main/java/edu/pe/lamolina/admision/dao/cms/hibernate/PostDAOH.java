package edu.pe.lamolina.admision.dao.cms.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import edu.pe.lamolina.admision.dao.cms.PostDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.edu.lamolina.model.cms.Post;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;

@Repository
public class PostDAOH extends AbstractEasyDAO<Post> implements PostDAO {

    public PostDAOH() {
        super();
        setClazz(Post.class);
    }

    @Override
    public Post findByURL(String pageURL) {
        Octavia octavia = Octavia.query(Post.class)
                .join("sitio si")
                .filter("url", pageURL)
                .filter("si.id", AdmisionConstantine.SITIO_ADMISION);

        return this.find(octavia);
    }

    @Override
    public List<Post> allByDynatable(DynatableFilter filter) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Post.class, "p")
                .join("sitio si")
                .searchFields("p.titulo", "p.contenido", "p.url")
                .filter("si.id", AdmisionConstantine.SITIO_ADMISION);

        return sql.all(getCurrentSession());
    }

}
