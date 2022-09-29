package edu.pe.lamolina.admision.controller.admision.inscripcion.numeroidentidad;

import java.util.List;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface NumeroIdentidadService {

    Postulante findPostulante(Postulante postulante);

    List<TipoDocIdentidad> allTiposDocIdentidad();

    void saveCambioDni(Postulante postulante, Long tipoDoc, String numero, String email, CicloPostula cicloPostula);

}
