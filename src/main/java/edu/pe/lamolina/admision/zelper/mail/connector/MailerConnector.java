package edu.pe.lamolina.admision.zelper.mail.connector;

public interface MailerConnector {

    void sendMail(MailMessage mail);

    void sendMailToAdmision(MailMessage mail);
}
