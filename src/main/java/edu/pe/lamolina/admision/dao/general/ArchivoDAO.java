package edu.pe.lamolina.admision.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.general.Archivo;

public interface ArchivoDAO extends EasyDAO<Archivo> {

    List<Archivo> allByIstanciaTaller(InstanciaEnum instanciaEnum, Long taller);

    List<Archivo> allByIstancia(InstanciaEnum instanciaEnum, List<Long> ids);

}
