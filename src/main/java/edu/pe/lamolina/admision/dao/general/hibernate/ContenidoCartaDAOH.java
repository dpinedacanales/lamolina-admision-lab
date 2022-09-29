package edu.pe.lamolina.admision.dao.general.hibernate;

import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.ContenidoCartaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Sistema;

@Repository
public class ContenidoCartaDAOH extends AbstractDAO<ContenidoCarta> implements ContenidoCartaDAO {

    public ContenidoCartaDAOH() {
        super();
        setClazz(ContenidoCarta.class);
    }

    @Override
    public ContenidoCarta findByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum) {
        SqlUtil sqlUtil = SqlUtil.creaSqlUtil("cc")
                .filter("cc.codigo", contenidoCartaEnum.name());
        return find(sqlUtil);
    }

    @Override
    public ContenidoCarta findByCodigo(String codigo, Sistema sistema) {
        Octavia sql = Octavia.query()
                .from(ContenidoCarta.class, "cc")
                .join("sistema s")
                .filter("cc.codigo", codigo)
                .filter("s.id", sistema);
        return (ContenidoCarta) sql.find(getCurrentSession());
    }

}
