package edu.pe.lamolina.admision.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

public interface TipoDocIdentidadDAO extends Crud<TipoDocIdentidad> {

    TipoDocIdentidad findByEnum(TipoDocIdentidadEnum tipoDocEnum);

    List<TipoDocIdentidad> allForPersonaNatural();

}
