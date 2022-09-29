package edu.pe.lamolina.admision.movil.boletas;

import pe.edu.lamolina.model.finanzas.DeudaInteresado;

import java.util.List;
import pe.edu.lamolina.model.inscripcion.Interesado;

public interface BoletasRestService {

    List<DeudaInteresado> allByPostulante(Long idPostulante);

    public Boolean tienePermiso(Interesado interesado);

    public Long countPendientes(Interesado interesado);

}
