package edu.pe.lamolina.admision.zelper.mail;

import java.util.List;
import javax.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import edu.pe.lamolina.admision.config.DespliegueConfig;
import edu.pe.lamolina.admision.controller.general.contacto.FormContacto;
import edu.pe.lamolina.admision.zelper.mail.connector.MailMessage;
import edu.pe.lamolina.admision.zelper.mail.connector.MailerConnector;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.constantines.AdmisionConstantine;
import pe.edu.lamolina.model.enums.TipoProspectoEnum;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.CODIGO_POSTULANTE;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.ESTIMADO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NOMBRE_PERSONA;
import pe.edu.lamolina.model.finanzas.ConceptoPago;
import pe.edu.lamolina.model.finanzas.ConceptoPrecio;
import pe.edu.lamolina.model.finanzas.DeudaInteresado;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.inscripcion.Postulante;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.ConceptoExamenMedico;

@Service
@Transactional
public class MailerServiceImp implements MailerService {

    @Autowired
    MailerConnector mailerConnector;
    @Autowired
    DespliegueConfig despliegueConfig;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void enviarPagoPostulante(Postulante postulante, List<DeudaInteresado> deudas, CicloPostula ciclo, ContenidoCarta contenidoCarta) {
        if (deudas.isEmpty()) {
            throw new PhobosException("error de precios");
        }

        int numero = 1;
        for (DeudaInteresado deuda : deudas) {
            deuda.setNumero(numero);
            numero++;
        }

        String estimado = postulante.getPersona().esFemenino() ? "Estimada" : "Estimado";
        String contenido = contenidoCarta.getContenido();

        contenido = contenido.replace(ESTIMADO.getValue(), estimado);
        contenido = contenido.replace(NOMBRE_PERSONA.getValue(), postulante.getPersona().getNombreCompleto());
        contenido = contenido.replace(CODIGO_POSTULANTE.getValue(), postulante.getCodigo());

        Context ctx = new Context();
        ctx.setVariable("contenido", contenido);
        ctx.setVariable("estimado", estimado);
        ctx.setVariable("postulante", postulante);
        ctx.setVariable("deudas", deudas);
        ctx.setVariable("helper", new NumberFormat());
        ctx.setVariable("banner", AdmisionConstantine.URL_BANNER_EMAIL);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("admision/mail/mailPagoPostulante");
        mail.setSubject("Información de Pago.");
        mail.setDestinatarios(new String[]{postulante.getPersona().getEmail()});

        mailerConnector.sendMail(mail);

    }

    @Override
    public void enviarPagoGuiaPostulante(String tipoProspecto, Postulante postulante, List<DeudaInteresado> deudas, CicloPostula ciclo) {

        Context ctx = new Context();
        ctx.setVariable("postulante", postulante);
        logger.debug("POSTULANTE {} CICLO POSTULA {}", postulante.getId(), ciclo.getId());
        logger.debug("DEUDAS SIZE  {} ", deudas.size());
        ConceptoPrecio conceptoGuiaPostulante = null;
        if (tipoProspecto.equals(TipoProspectoEnum.regular.toString())) {
            conceptoGuiaPostulante = findConceptoGuiaPostulante(deudas);
        } else {
            logger.debug("tipoGuiaPostulante:::: {}", tipoProspecto);
            logger.debug("TipoProspectoEnum:::: {}", TipoProspectoEnum.valueOf(tipoProspecto).getDisplayName());
            conceptoGuiaPostulante = findConceptoGuiaPostulante(TipoProspectoEnum.valueOf(tipoProspecto).getDisplayName(), deudas);

        }

        logger.debug("CONCEPTO GUIA DEL POSTULANTE  {} ", conceptoGuiaPostulante.getId());
        ctx.setVariable("deudas", deudas);
        ctx.setVariable("helper", new NumberFormat());
        ctx.setVariable("banner", AdmisionConstantine.URL_BANNER_EMAIL);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("admision/mail/mailPagoGuiaPostulante");
        mail.setSubject("Información de Pago.");
        mail.setDestinatarios(new String[]{postulante.getPersona().getEmail()});

        mailerConnector.sendMail(mail);

    }

    private ConceptoPrecio findConceptoGuiaPostulante(String codigoProspecto, List<DeudaInteresado> deudas) {
        for (DeudaInteresado deuda : deudas) {
            if (deuda.isCancelada()) {
                continue;
            }
            ConceptoPrecio conceptoPrecio = deuda.getConceptoPrecio();
            ConceptoPago conceptoPago = conceptoPrecio.getConceptoPago();
            if (conceptoPago.getCodigo().equals(codigoProspecto)) {
                return conceptoPrecio;
            }
        }
        return null;
    }

    private ConceptoPrecio findConceptoGuiaPostulante(List<DeudaInteresado> deudas) {
        for (DeudaInteresado deuda : deudas) {
            if (deuda.isCancelada()) {
                continue;
            }
            ConceptoPrecio conceptoPrecio = deuda.getConceptoPrecio();
            ConceptoPago conceptoPago = conceptoPrecio.getConceptoPago();
            if (conceptoPago.isProspecto()) {
                return conceptoPrecio;
            }
        }
        return null;
    }

    @Override
    public void enviarEmailContacto(FormContacto mensaje) {
        MailMessage mail = new MailMessage();
        InternetAddress ie = new InternetAddress();
        ie.setAddress(mensaje.getEmail());
        Context ctx = new Context();
        ctx.setVariable("con", mensaje);
        ctx.setVariable("banner", AdmisionConstantine.URL_BANNER_EMAIL);

        mail.setContext(ctx);
        mail.setTemplate("admision/mail/mailContacto");
        mail.setSubject("Contacto desde la web");
        mail.setFrom(ie);

        mailerConnector.sendMailToAdmision(mail);
    }

    @Override
    public void enviarEmailRecorridoBienvenidoIngresante(Persona persona, ContenidoCarta contenidoCarta, List<TurnoEntrevistaObuae> turnos) {

        Context ctx = new Context();
        String contenido = contenidoCarta.getContenido();
        contenido = contenido.replaceAll(NOMBRE_PERSONA.getValue(), persona.getNombreCompleto());
        ctx.setVariable("contenido", contenido);
        ctx.setVariable("turnos", turnos);
        ctx.setVariable("banner", AdmisionConstantine.URL_BANNER_EMAIL);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("mail/mailRecorridoIngresanteBienvenido");
        mail.setSubject(contenidoCarta.getNombre());
        mail.setDestinatarios(new String[]{persona.getEmail()});
        mailerConnector.sendMail(mail);
    }

    @Override
    public void enviarEmailRecorridoPersonalizadoIngresante(Persona persona, ContenidoCarta contenidoCarta, RecorridoIngresante recorrido, ConceptoExamenMedico pago) {

        Context ctx = new Context();
        String contenido = contenidoCarta.getContenido();
        contenido = contenido.replaceAll(NOMBRE_PERSONA.getValue(), persona.getNombreCompleto());
        ctx.setVariable("contenido", contenido);
        ctx.setVariable("numeroAtencion", recorrido.getNumeroAtencion());
        ctx.setVariable("turno", recorrido.getTurnoEntrevistaObuae());
        ctx.setVariable("persona", persona);
        ctx.setVariable("deuda", pago);
        ctx.setVariable("helper", new NumberFormat());
        ctx.setVariable("banner", AdmisionConstantine.URL_BANNER_EMAIL);

        MailMessage mail = new MailMessage();
        mail.setContext(ctx);
        mail.setTemplate("mail/mailRecorridoIngresantePersonalizado");
        mail.setSubject(contenidoCarta.getNombre());
        mail.setDestinatarios(new String[]{persona.getEmail()});
        logger.debug("*********************send email personalizado to {}", persona.getEmail());
        mailerConnector.sendMail(mail);
    }

}
