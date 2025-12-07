package ldw.squad.project.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

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

            return "Email enviado (text)";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao enviar email (text): " + e.getLocalizedMessage();
        }
    }

    public String enviarEmailHtml(String recepient, String subject, String htmlBody, String textBody) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            // true = multipart
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(recepient);
            helper.setSubject(subject);

            String plain = (textBody != null && !textBody.isEmpty()) ? textBody : stripHtml(htmlBody);
            helper.setText(plain, htmlBody);

            javaMailSender.send(mimeMessage);
            return "Email enviado (HTML)";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao enviar email (HTML): " + e.getMessage();
        }
    }

    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("\\<.*?\\>", "").replaceAll("&nbsp;", " ").trim();
    }
}