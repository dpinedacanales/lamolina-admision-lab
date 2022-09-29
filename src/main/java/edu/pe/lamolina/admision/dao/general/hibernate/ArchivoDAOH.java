package edu.pe.lamolina.admision.dao.general.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.general.ArchivoDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.general.Archivo;

@Repository
public class ArchivoDAOH extends AbstractEasyDAO<Archivo> implements ArchivoDAO {

    public ArchivoDAOH() {
        super();
        setClazz(Archivo.class);
    }

    @Override
    public List<Archivo> allByIstanciaTaller(InstanciaEnum instanciaEnum, Long taller) {
        Octavia oct = Octavia.query(Archivo.class)
                .filter("instancia", instanciaEnum.name())
                .filter("idInstancia", taller);
        return this.all(oct);
    }

    @Override
    public List<Archivo> allByIstancia(InstanciaEnum instanciaEnum, List<Long> ids) {
        Octavia oct = Octavia.query(Archivo.class)
                .filter("instancia", instanciaEnum.name());

        if (!ids.isEmpty()) {
            oct.filter("idInstancia", ids);
        }

        oct.orderBy("id DESC");

        return this.all(oct);
    }

}
