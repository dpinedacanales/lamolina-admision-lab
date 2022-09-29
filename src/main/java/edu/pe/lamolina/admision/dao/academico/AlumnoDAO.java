package edu.pe.lamolina.admision.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface AlumnoDAO extends EasyDAO<Alumno> {

    Alumno findByPostulante(Postulante postulante);

    List<Alumno> allExpuladosByDNI(String numeroDocIdentidad);

    public List<Alumno> allIngresanteTemporalByCiclo(CicloAcademico ciclo);

    public List<Alumno> allNoNormalByDNI(String numeroDocIdentidad);

}
