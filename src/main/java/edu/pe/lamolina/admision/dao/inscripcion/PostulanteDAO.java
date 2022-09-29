package edu.pe.lamolina.admision.dao.inscripcion;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.PostulanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoDocIdentidadEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface PostulanteDAO extends EasyDAO<Postulante> {

    Postulante findActivoByDocumento(TipoDocIdentidadEnum tipo, String numeroDocumento, CicloPostula ciclo);

    Postulante findByInteresado(Interesado interesado);

    Postulante findActivoByInteresadoSimple(Interesado interesado);

    Postulante findActivoByCodigoCiclo(String codigoPostulante, CicloPostula ciclo);

    List<Postulante> allByPersonaCiclo(Persona persona, CicloPostula ciclo);

    void findLock(Long id);

    List<Postulante> allIngresoAnuladoByPersonaCiclo(Persona persona, List<CicloPostula> ciclos);

    Postulante findLastByInteresadoCicloEstado(Postulante interesado, CicloPostula ciclo, PostulanteEstadoEnum postulanteEstadoEnum);

    Postulante findLastActivoByPersona(Persona personaBD);

    public Postulante allByPersonaCicloRenuncianteCepre(Persona persona, CicloPostula ciclo);

}
