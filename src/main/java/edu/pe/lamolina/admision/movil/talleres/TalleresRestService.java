package edu.pe.lamolina.admision.movil.talleres;

import java.util.List;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Taller;

public interface TalleresRestService {

    List<Taller> all();

    List<Taller> allByInteresado(Interesado interesado);

    Taller find(Interesado interesado, Taller taller);

    void inscribirse(Interesado interesado, Taller taller, JsonResponse json);

}
