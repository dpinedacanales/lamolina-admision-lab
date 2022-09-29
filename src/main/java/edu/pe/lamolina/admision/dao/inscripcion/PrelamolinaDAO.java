package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import edu.pe.lamolina.admision.controller.admision.inscripcion.inscripcion.FormCepre;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.Prelamolina;

public interface PrelamolinaDAO extends Crud<Prelamolina> {

    List<Prelamolina> allDynatable(CicloPostula ciclo, DynatableFilter filter);

    Prelamolina findByCodigoCiclo(String codigoCepre, CicloPostula ciclo);

    Prelamolina findByPersona(Persona persona, CicloPostula ciclo);

    List<Prelamolina> allByDocumento(Persona persona, CicloPostula cicloPostula);

    Prelamolina findByInteresado(Interesado interesado, CicloPostula ciclo);

    Prelamolina findByInteresadoCicloPostula(CicloPostula ciclo, FormCepre formCepre);

    Prelamolina findByPostulante(Postulante postulante);

    List<Prelamolina> allByDocumentoEsIngresante(Persona persona, CicloPostula ciclo, int esIngresante);
    
    Prelamolina findByInteresadoAndCiclo(Interesado interesado, CicloPostula ciclo);

}
