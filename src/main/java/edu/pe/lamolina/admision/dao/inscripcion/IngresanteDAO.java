package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Ingresante;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface IngresanteDAO extends EasyDAO<Ingresante> {

    List<Ingresante> allByCiclo(CicloPostula ciclo);

    Ingresante findByPostulante(Postulante postulante);

}
