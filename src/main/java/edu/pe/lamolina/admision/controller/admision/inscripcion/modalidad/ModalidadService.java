package edu.pe.lamolina.admision.controller.admision.inscripcion.modalidad;

import java.util.List;
import javax.servlet.http.HttpSession;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface ModalidadService {

    ModalidadIngreso getModalidad(Long modalidad);

    ModalidadIngresoCiclo getModalidadIngresoCiclo(ModalidadIngreso modalidadIngreso, CicloPostula cicloPostula);

    List<Universidad> allUniversidadPeru();

    Postulante findPostulante(Postulante postulanteSess);

    void aceptarCambioModalidad(Postulante postulanteSess, HttpSession session);

    List<CarreraPostula> allCarreras(ModalidadIngreso modalidad, CicloPostula ciclo);

    Pais findPeru();

    List<GradoSecundaria> allGrado();

    CarreraPostula findCarreraIngreso(Postulante postulante);

    void testCovenio(Postulante postulante, ModalidadIngreso modalidadIngreso);

    Colegio findColegio(Postulante postulante);

    List<ModalidadIngreso> allModalidadesByCiclo(CicloPostula ciclo);

}
