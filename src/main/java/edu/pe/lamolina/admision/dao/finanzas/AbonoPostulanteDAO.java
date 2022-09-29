package edu.pe.lamolina.admision.dao.finanzas;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.finanzas.AbonoPostulante;
import pe.edu.lamolina.model.finanzas.ItemCargaAbono;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface AbonoPostulanteDAO extends Crud<AbonoPostulante> {

    List<AbonoPostulante> allByPostulante(Postulante postul);

    AbonoPostulante findByItemCarga(ItemCargaAbono item);

    AbonoPostulante findByPostulante(Postulante postulatenBD);

}
