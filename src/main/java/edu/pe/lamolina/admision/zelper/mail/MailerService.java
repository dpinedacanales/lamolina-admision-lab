package edu.pe.lamolina.admision.zelper.mail;

import java.util.List;
import edu.pe.lamolina.admision.controller.general.contacto.FormContacto;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.ConceptoExamenMedico;

public interface MailerService {

    void enviarPagoPostulante(Postulante postulante, List<DeudaInteresado> deudas, CicloPostula ciclo, ContenidoCarta contenidoCarta);

    void enviarPagoGuiaPostulante(String tipoProspecto, Postulante postulante, List<DeudaInteresado> deudas, CicloPostula ciclo);

    void enviarEmailContacto(FormContacto mensaje);

    void enviarEmailRecorridoPersonalizadoIngresante(Persona persona, ContenidoCarta contenidoCarta, RecorridoIngresante recorrido, ConceptoExamenMedico pago);

    void enviarEmailRecorridoBienvenidoIngresante(Persona persona, ContenidoCarta contenidoCarta, List<TurnoEntrevistaObuae> turnos);

}
