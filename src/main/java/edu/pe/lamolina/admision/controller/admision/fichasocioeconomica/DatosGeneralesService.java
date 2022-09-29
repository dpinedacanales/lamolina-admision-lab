package edu.pe.lamolina.admision.controller.admision.fichasocioeconomica;

import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface DatosGeneralesService {

    void asignarMatricula(Long idPostulante, DataSessionAdmision ds, HttpSession session);

    String goMaipi(Alumno alumno, Usuario usuario);

    Parametro findParametroByEnum(ParametrosSistemasEnum parametrosSistemasEnum);

}
