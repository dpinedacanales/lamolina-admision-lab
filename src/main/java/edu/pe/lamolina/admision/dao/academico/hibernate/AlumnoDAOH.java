package edu.pe.lamolina.admision.dao.academico.hibernate;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;
import edu.pe.lamolina.admision.dao.academico.AlumnoDAO;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import static pe.edu.lamolina.model.enums.ModalidadEstudioEnum.PRE;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_A;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_E;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_N;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_R;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_X;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_XD;
import pe.edu.lamolina.model.inscripcion.Postulante;

@Repository
public class AlumnoDAOH extends AbstractEasyDAO<Alumno> implements AlumnoDAO {

    public AlumnoDAOH() {
        super();
        setClazz(Alumno.class);
    }

    @Override
    public Alumno findByPostulante(Postulante postulante) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("postulantePregrado pos", "pos.persona")
                .filter("pos.id", postulante);
        return find(sql);
    }

    @Override
    public List<Alumno> allExpuladosByDNI(String numeroDocIdentidad) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.persona per", "situacionAcademica sa")
                .filter("per.numeroDocIdentidad", numeroDocIdentidad)
                .in("sa.codigo", Arrays.asList(S_X.name(), S_XD.name()));
        return all(sql);
    }

    @Override
    public List<Alumno> allIngresanteTemporalByCiclo(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.cicloIngreso ci")
                .filter("ci.id", ciclo)
                .filter("alu.codigo", "like", "P%")
                .orderBy("alu.codigo desc");
        return all(sql);
    }

    @Override
    public List<Alumno> allNoNormalByDNI(String numeroDocIdentidad) {

        Octavia sql = Octavia.query()
                .from(Alumno.class, "alu")
                .join("alu.persona per", "situacionAcademica sa", "modalidadEstudio md")
                .filter("per.numeroDocIdentidad", numeroDocIdentidad)
                .notIn("sa.codigo", Arrays.asList(S_N.getValue(), S_A.getValue(), S_R.getValue(), S_E.getValue()))
                .filter("md.codigo", PRE);
        return all(sql);
    }

}
