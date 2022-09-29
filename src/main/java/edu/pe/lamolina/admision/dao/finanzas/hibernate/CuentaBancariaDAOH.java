package edu.pe.lamolina.admision.dao.finanzas.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.finanzas.CuentaBancariaDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;

@Repository
public class CuentaBancariaDAOH extends AbstractEasyDAO<CuentaBancaria> implements CuentaBancariaDAO {

    public CuentaBancariaDAOH() {
        super();
        setClazz(CuentaBancaria.class);
    }

    @Override
    public CuentaBancaria findByNumero(String numero) {
        Octavia sql = Octavia.query()
                .from(CuentaBancaria.class, "cb")
                .join("compania")
                .filter("cb.numero", numero);

        return find(sql);
    }

    @Override
    public List<CuentaBancaria> all() {
        Octavia sql = Octavia.query()
                .from(CuentaBancaria.class, "cb")
                .join("compania");

        return all(sql);
    }

    @Override
    public CuentaBancaria findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(CuentaBancaria.class, "cb")
                .join("compania")
                .filter("cb.codigo", codigo);

        return find(sql);
    }

}
