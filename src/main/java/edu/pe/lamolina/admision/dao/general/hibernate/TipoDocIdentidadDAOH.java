package edu.pe.lamolina.admision.dao.general.hibernate;

import java.util.Arrays;
import java.util.List;
import pe.albatross.zelpers.dao.AbstractDAO;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.TipoDocIdentidadDAO;
import pe.albatross.zelpers.dao.SqlUtil;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import static pe.edu.lamolina.model.enums.TipoDocIdentidadEnum.CE;
import static pe.edu.lamolina.model.enums.TipoDocIdentidadEnum.CEX;
import static pe.edu.lamolina.model.enums.TipoDocIdentidadEnum.DNI;
import static pe.edu.lamolina.model.enums.TipoDocIdentidadEnum.PAS;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

@Repository
public class TipoDocIdentidadDAOH extends AbstractDAO<TipoDocIdentidad> implements TipoDocIdentidadDAO {

    public TipoDocIdentidadDAOH() {
        super();
        setClazz(TipoDocIdentidad.class);
    }

    @Override
    public TipoDocIdentidad findByEnum(TipoDocIdentidadEnum tipoDocEnum) {
        SqlUtil sqltUtil = SqlUtil.creaSqlUtil("td")
                .filter("td.codigo", tipoDocEnum.name());
        return find(sqltUtil);
    }

    @Override
    public List<TipoDocIdentidad> allForPersonaNatural() {
        SqlUtil sqltUtil = SqlUtil.creaSqlUtil("td")
                .filterIn("td.simbolo", Arrays.asList(DNI.name(), CEX.name(), CE.name(), PAS.name()));
        return all(sqltUtil);
    }
}
