package edu.pe.lamolina.admision.controller.general.comun;

import java.util.List;
import edu.pe.lamolina.admision.zelper.model.DataSessionAdmision;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Colegio;
import pe.edu.lamolina.model.general.GradoSecundaria;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.Ubicacion;
import pe.edu.lamolina.model.general.Universidad;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;

public interface BuscarService {

    List<Ubicacion> allDistritosByName(String nombre);

    List<Pais> allPaisesByName(String nombre);

    List<Pais> allPaisesByCodigo(String codigo);

    List<Pais> allPaisesByNotCodigo(String nombre, String codigo);

    List<Colegio> allColegioSecundariaByName(String nombre, Long idUbicacion, Long idModalidadIngreso);

    List<Colegio> allColegiosCoar();

    List<ModalidadIngreso> allModalidadIngresoByName(String nombre);

    ModalidadIngresoCiclo findByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula ciclo);

    List<Pais> allPaisesByNameModalidad(String nombre, DataSessionAdmision ds, ModalidadIngreso modalidadIngreso);

    TipoDocIdentidad findById(Long id);

    List<ModalidadIngreso> allModalidadIngreso();

    List<ModalidadIngreso> allModalidadIngresoByCiclo(CicloPostula cicloPostula);

    List<ModalidadIngresoCiclo> allModalidadIngresoCicloByCiclo(CicloPostula cicloPostula);

    List<Universidad> allUniversidadByEstadoPais(String nombre, EstadoEnum estadoEnum, String CODIGO_PERU);

    List<GradoSecundaria> allGradoSecundaria();

}
