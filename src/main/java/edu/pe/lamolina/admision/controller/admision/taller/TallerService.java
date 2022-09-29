package edu.pe.lamolina.admision.controller.admision.taller;

import java.util.List;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.inscripcion.CarreraNueva;
import pe.edu.lamolina.model.inscripcion.InscritoTaller;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Taller;

public interface TallerService {

    List<Taller> allTaller();

    List<Archivo> allArchivosTaller(List<Taller> talleres);

    Taller findTaller(Long taller);

    void saveInscritoTaller(Interesado interesado, Long taller, DataSessionAdmision ds, JsonResponse json);

    void inscribirseTaller(Interesado interesadoDB, Taller tallerDB, JsonResponse json);

    InscritoTaller findInscritoTallerByInteresadoTaller(Interesado interesado, Long taller);

    Interesado findInteresado(Interesado interesado);

    List<TipoDocIdentidad> allTiposDocIdentidad();

    List<Carrera> allCarrera();

    List<CarreraNueva> allCarreraNueva();

    List<Taller> allTopTalleres(int top);

    List<Archivo> allArchivosTallerByInstanciaEnum(InstanciaEnum instanciaEnum);

    List<Carrera> allCarreraByEstado(EstadoEnum estadoEnum);

    List<CarreraNueva> allCarreraNuevaByEstado(EstadoEnum estadoEnum);

     void procesarDescripcionAndBanners(List<Taller> talleres);

}
