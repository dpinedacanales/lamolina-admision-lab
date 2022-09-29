package edu.pe.lamolina.admision.zelper.mail.connector;

import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring4.SpringTemplateEngine;
import edu.pe.lamolina.admision.config.DespliegueConfig;

@Service
public class MailerConnectorImp implements MailerConnector {

    @Autowired
    DespliegueConfig despliegueConfig;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMail(MailMessage mail) {
        try {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            InternetAddress ie = new InternetAddress();
            ie.setPersonal("UNALM - Sistema de Inscripción del Postulantes");
            ie.setAddress(despliegueConfig.getEmailcontacto());
            message.setFrom(ie);

            message.setSubject(mail.getSubject());
            message.setTo(mail.getDestinatarios());

            String[] copias = despliegueConfig.getCopias().split(" ");

            String[] emails = despliegueConfig.getEmails().split(" ");

            String[] destinatarios = despliegueConfig.getMailer() ? mail.getDestinatarios() : emails;
            for (String destinatario : destinatarios) {
                logger.info("SEND EMAIL TO {}", destinatario);
            }
            message.setTo(destinatarios);
            if (despliegueConfig.getMailer()) {
                for (String copia : copias) {
                    logger.info("SEND EMAIL BCC TO {}", copia);
                }
                message.setBcc(copias);
            }

            String htmlContent = this.templateEngine.process(mail.getTemplate(), mail.getContext());
            message.setText(htmlContent, true);

            this.javaMailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException ex) {
            ex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getLocalizedMessage());
        }
    }
    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMailToAdmision(MailMessage mail) {
        try {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            InternetAddress ie = new InternetAddress();
            ie.setPersonal("UNALM - Sistema de Inscripción del Postulantes");
            ie.setAddress(despliegueConfig.getEmailcontacto());
            message.setFrom(ie);

            message.setSubject(mail.getSubject());

            String[] copias = despliegueConfig.getCopias().split(" ");

            String[] emails = despliegueConfig.getEmails().split(" ");

            String[] destinatarios = emails;
            for (String destinatario : destinatarios) {
                logger.info("SEND EMAIL TO {}", destinatario);
            }
            message.setTo(destinatarios);
            if (despliegueConfig.getMailer()) {
                for (String copia : copias) {
                    logger.info("SEND EMAIL BCC TO {}", copia);
                }
                message.setBcc(copias);
            }

            String htmlContent = this.templateEngine.process(mail.getTemplate(), mail.getContext());
            message.setText(htmlContent, true);

            this.javaMailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException ex) {
            ex.printStackTrace();

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getLocalizedMessage());
        }
    }

}
