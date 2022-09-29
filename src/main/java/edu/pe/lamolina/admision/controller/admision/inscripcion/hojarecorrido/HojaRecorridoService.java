package edu.pe.lamolina.admision.controller.admision.inscripcion.hojarecorrido;

import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import java.util.List;
import org.springframework.ui.Model;
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.OficinaRecorrido;
import pe.edu.lamolina.model.aporte.AporteAlumnoCiclo;
import pe.edu.lamolina.model.bienestar.DocumentoBienestar;
import pe.edu.lamolina.model.examen.AlumnoTestSicologico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.PostulanteDocumento;
import pe.edu.lamolina.model.socioeconomico.FichaSocioeconomica;

public interface HojaRecorridoService {

    List<OficinaRecorrido> allOficinasRecorridoResumen(Postulante postulante, CicloPostula cicloPostula);

    void generarBoletaExamenMedico(Model model, DataSessionAdmision ds);

    List<PostulanteDocumento> allDocumentosByPostulante(Postulante postulante);

    ActividadIngresante findActividadCareo(Postulante postulante, CicloAcademico cicloAcademico);

    boolean verificarCambioColegio(Postulante postulante);

    List<DocumentoBienestar> allDocumentosSocioEconomicosByCiclo(CicloAcademico cicloAcademico);

    void generarBoletaIngreso(Model model, DataSessionAdmision ds);

    Alumno findAlumnoByPostulante(Postulante postulante);

    FichaSocioeconomica findFichaSocioeconomica(Postulante postulante, CicloAcademico cicloAcademico);

    AlumnoTestSicologico findTestSicologico(Postulante postulante, CicloAcademico cicloAcademico);

    List<AporteAlumnoCiclo> allAportesByAlumnoCiclo(Postulante postulante, CicloAcademico cicloAcademico);

    Boolean verNivelacion(DataSessionAdmision ds);

    Boolean verInscritoNivelacion(DataSessionAdmision ds);

    void inscripcion(DataSessionAdmision ds);
}
