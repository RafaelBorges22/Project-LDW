package ldw.squad.project.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public String enviarEmailText(String recepient, String subject, String message){
        try{
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setFrom(sender);
            simpleMailMessage.setTo(recepient);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(message);
            javaMailSender.send(simpleMailMessage);

            return "Email Funfando";

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Tentando enviar e-mail...");
            System.out.println("E-mail enviado com sucesso!");
            return "Email não funfante" + e.getLocalizedMessage();
        }
    }

}
