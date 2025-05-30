package cat.ferreria.dekstop.controller;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Controlador de envios de correos electronicos para poder restablecer la contraseña.
 *
 * @author alexl
 * @date 15/05/2025
 * */

public class EmailController {
    public static void main(String args[]){
        String para = "";                                //Destinatario
        String de = "biblioteca@inslaferreria.cat"; //Emisor
        String host = "127.0.0.1";                  //Servidor SMTP

        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(properties); // default session
        try {
            MimeMessage message = new MimeMessage(session); // create a default MimeMessage object
            message.setFrom(new InternetAddress(de)); // Set From: header field of the header
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(para)); // Set To: header field of the header
            message.setSubject("This is the Subject Line!"); // Set Subject: header field
            message.setText("This is actual message"); // Now set the actual message

            Transport.send(message); // Send message
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
