package edu.pe.lamolina.admision.movil.inscripcion;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CarreraPostula;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.Interesado;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.model.inscripcion.Postulante;

public interface InscripcionRestService {

    void verifyNumeroDocumento(Postulante postulanteForm);

    Postulante savePostulante(Postulante postulanteForm);

    List<ModalidadIngresoCiclo> allModalidad();

    List<CarreraPostula> allCarreraPostulaByModalidad(ModalidadIngreso modalidadIngreso);

    Postulante findPostulante(Postulante postulante);

    ModalidadIngreso findModalidadCepre();

    boolean mostrarUbicacion();

    List<ModalidadIngreso> allModalidadParticipanteLibre();

    ObjectNode opcionesInscripcionByPostulante(Postulante postulanteForm);

    Boolean tienePermisoVerExamen(Postulante postulante);

    Boolean tienePermisoVerResultados(Postulante postulante);

    Postulante findPostulanteActivoByInteresado(Interesado interesado);

    boolean esFinalInscripciones();

    Persona getPersona(Persona persona);

    void completarPostulante(Postulante postulante, Interesado interesado);

    Interesado findInteresado(Long idInteresado);

    ModalidadIngresoCiclo findModalidad(Long modalidadIngreso);

}
