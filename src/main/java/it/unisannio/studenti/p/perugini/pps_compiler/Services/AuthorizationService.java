package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailNonCorrettaException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OTPExpiredException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserNotFound;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.StudentDTORepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.UsersRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Components.JwtProvider;
import it.unisannio.studenti.p.perugini.pps_compiler.Components.OTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Cookie;

import java.util.Optional;


@Service
public class AuthorizationService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private OTP otpProvider;

    private Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    public String sendOtp(Email email){
        //genero otp e la mando tramite email
        String otp = otpProvider.makeOtp();
        this.emailService.sendTextEmail(otp,email);
        logger.info("OTP mandato tramite e-mail");

        //creo hash
        String hash = otpProvider.cryptOTP(otp);
        logger.info("hash generato: "+hash);
        return hash;
    }

    public boolean verifyOtp(Email email, String otp, Cookie cookie) throws OTPExpiredException {
        if (cookie == null)
            throw new OTPExpiredException("L' OTP è scaduto prova a loggarti di nuovo");

        logger.info("ecco il cookie ricevuto: "+cookie.getValue());
        logger.info("ecco la mail ricevuta: "+email.getEmail());

        String otpHashed = otpProvider.cryptOTP(otp);
        if (otpHashed.equals(cookie.getValue()))
            return true;
        return false;
    }

    public User getUserByEmail(Email email) throws UserNotFound {
        Optional<User> userOptional = this.usersRepository.findById(email);
        if(!userOptional.isPresent())
            throw new UserNotFound("Non esiste nessun utente con questa mail");
        return userOptional.get();
    }


    public User GetUserFromJwt(String jwtToken) throws EmailNonCorrettaException, UserNotFound {
        String email = jwtProvider.getSubjectFromToken(jwtToken);
        return this.getUserByEmail(new Email(email));
    }


    public boolean isJWTExpired(String jwtToken) {
        return this.jwtProvider.isExpired(jwtToken);
    }

}
