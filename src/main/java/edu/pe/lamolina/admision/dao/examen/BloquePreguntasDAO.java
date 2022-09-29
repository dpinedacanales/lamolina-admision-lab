package edu.pe.lamolina.admision.dao.examen;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.model.examen.BloquePreguntas;
import pe.edu.lamolina.model.examen.SubTituloExamen;

public interface BloquePreguntasDAO extends Crud<BloquePreguntas> {

    List<BloquePreguntas> allByTituloExamenVirtual(SubTituloExamen subTituloExamen);

}
