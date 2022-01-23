package it.unisannio.studenti.p.perugini.pps_compiler.core.service;

import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OTPExpiredException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserNotFound;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.ReadUserPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase.LoginUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Cookie;
import java.util.Optional;

/**Servizio che implementa il login use case*/
@Service
public class LoginService  implements LoginUseCase {
    /**Porta utilizzata per accedere in lettura al database degli user*/
    @Autowired
    private ReadUserPort readUserPort;
    /**Servizio utilizzato per validare ed inviare OTP*/
    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public void login(Email email) throws UserNotFound {
        Optional<User> user = this.readUserPort.findUserById(email);
        if(!user.isPresent())
            throw new UserNotFound("Effettua prima la registrazione al sistema");
    }

    @Override
    public User verifyLogin(String otpRichiesta, Cookie otpCookie, Email email) throws OTPExpiredException {
        if (authorizationService.verifyOtp(email, otpRichiesta, otpCookie)) {
            return readUserPort.findUserById(email).get();
        }
        throw new OTPExpiredException("L'OTP inserito non è valido");
    }
}
