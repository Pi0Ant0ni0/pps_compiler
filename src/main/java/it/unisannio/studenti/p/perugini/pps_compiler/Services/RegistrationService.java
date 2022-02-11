package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants.ERR_MESSAGES;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CONSTANTS;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.StudentDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.studente.port.CreateStudentRegistrationPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.studente.port.ReadStudentPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.studente.usecases.RegistrationUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.CreateUserPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.ReadUserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Cookie;

/**
 * Servizio che implementa lo use case di registrazione di uno studente al sistema*/
@Service
public class RegistrationService implements RegistrationUseCase {

    /**Componente che permette di interfacciarsi con la collezione degli utenti non verificati.
     * Sono tutti gli utenti che hanno avanzato una richiesta di registrazione senza aver confermato la e-mail*/
    @Autowired
    private CreateStudentRegistrationPort createStudentRegistrationPort;
    @Autowired
    private ReadStudentPort readStudentPort;

    /**Componente che permette di interfacciarsi con la collezione degli utenti già verificati.
     * Sono tutti gli utenti che hanno avanzato una richiesta di registrazione senza aver confermato la e-mail*/
    @Autowired
    private CreateUserPort createUserPort;
    @Autowired
    private ReadUserPort readUserPort;

    /**Componente che permette di interfacciarsi con la collezione dei corsi di studio*/
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;

    /**Service per inviare e verificare OTP e per recuperare i dati dei jwt token*/
    @Autowired
    private AuthorizationService authorizationService;

    /**Mapper per la conversione dello user a tutti i suoi dto e viceversa*/
    @Autowired
    private UserMapper userMapper;


    @Override
    public void register(StudentDTO studentDTO) throws EmailNonCorrettaException, EmailException, MatricolaNotValidException, CorsoDiStudioNotFoundException, InvalidUserException, UserAlreadyExistException {
        Email email = new Email(studentDTO.getEmail());

        if(!email.getNomeDominio().equals(CONSTANTS.DOMINIO_UNISANNIO_STUDENTE))
            throw new EmailException(ERR_MESSAGES.REGISTRATION_EMAIL);

        if(this.readUserPort.findUserById(email).isPresent())
            throw new UserAlreadyExistException(ERR_MESSAGES.REGISTRATION_DUPLICATED);

        if(studentDTO.getMatricola().length()!=9)
            throw new MatricolaNotValidException(ERR_MESSAGES.REGISTRATION_MATRICOLA);

        if(!this.readCorsoDiStudioPort.findCorsoDiStudioById(studentDTO.getMatricola().substring(0,3)).isPresent())
            throw new CorsoDiStudioNotFoundException(ERR_MESSAGES.REGISTRATION_MATRICOLA);

        if(!this.readCorsoDiStudioPort.findCorsoDiStudioById(studentDTO.getMatricola().substring(0,3)).get().getDenominazioneFacolta().equals("DIPARTIMENTO DI INGEGNERIA"))
            throw new InvalidUserException(ERR_MESSAGES.REGISTRATION_MATRICOLA);

        this.createStudentRegistrationPort.addUserNotVerified(studentDTO);
    }

    @Override
    public User verifyRegistration(String otpRichiesta, Cookie otpCookie, Email email) throws OTPExpiredException {
        if(this.authorizationService.verifyOtp(email,otpRichiesta,otpCookie)) {
           StudentDTO studentDTO = this.readStudentPort.findStudentById(email).get();
            CorsoDiStudio corsoDiStudio = this.readCorsoDiStudioPort.findCorsoDiStudioById(studentDTO.getMatricola().substring(0, 3)).get();
            return this.createUserPort.save(userMapper.fromStudentDTOToUser(studentDTO, corsoDiStudio));
        }
        throw new OTPExpiredException(ERR_MESSAGES.OTP_NOT_VALID);

    }
}
