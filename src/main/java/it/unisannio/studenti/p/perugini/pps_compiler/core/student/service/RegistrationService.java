package it.unisannio.studenti.p.perugini.pps_compiler.core.student.service;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.StudentDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.student.port.CreateStudentRegistrationPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.student.port.ReadStudentPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.student.usecases.RegistrationUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.CreateUserPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.ReadUserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Cookie;
import java.util.Optional;

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

        if(!email.getNomeDominio().equals("studenti.unisannio.it"))
            throw new EmailException("E' possibile registrarsi solo con email che abbiano come dominio \"studenti.unisannio.it\"");

        if(this.readUserPort.findUserById(email).isPresent())
            throw new UserAlreadyExistException("Sei già registrato, loggati!");

        if(studentDTO.getMatricola().length()!=9)
            throw new MatricolaNotValidException("La matricola inserita non è valida");

        if(!this.readCorsoDiStudioPort.findCorsoDiStudioById(studentDTO.getMatricola().substring(0,3)).isPresent())
            throw new CorsoDiStudioNotFoundException("La matricola inserita non si riferisce a nessun corso di studio noto");

        if(!this.readCorsoDiStudioPort.findCorsoDiStudioById(studentDTO.getMatricola().substring(0,3)).get().getDenominazioneFacolta().equals("DIPARTIMENTO DI INGEGNERIA"))
            throw new InvalidUserException("Al momento il sistema è aperto soltanto agli studenti del dipartimento di iNGEGNERIA");

        this.createStudentRegistrationPort.addUserNotVerified(studentDTO);
    }

    @Override
    public User verifyRegistration(String otpRichiesta, Cookie otpCookie, Email email) throws UserNotFound, OTPExpiredException {
        if(this.authorizationService.verifyOtp(email,otpRichiesta,otpCookie)) {
            Optional<StudentDTO> studentDTO = this.readStudentPort.findStudentById(email);
            if (!studentDTO.isPresent())
                throw new UserNotFound("Lo studente per il quale si è richiesta la verifica della email non ha avanzato nessuna richiesta di registrazione");

            CorsoDiStudio corsoDiStudio = this.readCorsoDiStudioPort.findCorsoDiStudioById(studentDTO.get().getMatricola().substring(0, 3)).get();
            return this.createUserPort.save(userMapper.fromStudentDTOToUser(studentDTO.get(), corsoDiStudio));
        }
        throw new OTPExpiredException("L'OTP inserito non è valido");

    }
}
