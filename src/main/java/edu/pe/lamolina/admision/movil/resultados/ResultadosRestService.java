package edu.pe.lamolina.admision.movil.resultados;

import pe.edu.lamolina.model.inscripcion.Evaluado;

import java.util.List;

public interface ResultadosRestService {

    List<Evaluado> allByCarrera(Long idCarrera);

    Evaluado findByPostulante(Long idPostulante);

    Evaluado findByDNI(String dni);
    
}