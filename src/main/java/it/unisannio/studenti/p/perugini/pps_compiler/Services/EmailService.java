package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;


    public void sendTextEmail(String message, Email email) {

        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(email.getEmail());
        simpleMessage.setSubject("PPS HELPER");
        simpleMessage.setText(message);
        javaMailSender.send(simpleMessage);

    }

}
