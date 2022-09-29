package edu.pe.lamolina.admision.dao.finanzas;

import java.util.Date;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.finanzas.CargaAbonos;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.inscripcion.CicloPostula;

public interface CargaAbonosDAO extends Crud<CargaAbonos> {

    CargaAbonos findHistoricoByCtaBancoFecha(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fecha);

    Long countByFecha(CuentaBancaria ctaBanco, CicloPostula ciclo, Date fecha);

}
