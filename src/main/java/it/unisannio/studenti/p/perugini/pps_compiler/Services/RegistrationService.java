package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.StudentDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserAlreadyExistException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.StudentDTORepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {
    @Autowired
    private CorsiDiStudioRepository corsiDiStudioRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private StudentDTORepository studentDTORepository;
    @Autowired
    private UserMapper userMapper;

    public CorsoDiStudio getCorsoDiStudioByMatricola(String matricola) throws CorsoDiStudioNotFoundException {
        String codiceCorsoDiStudio = matricola.substring(0,3);
        Optional<CorsoDiStudio> corsoDiStudioOptional = this.corsiDiStudioRepository.findById(codiceCorsoDiStudio);
        if (!corsoDiStudioOptional.isPresent())
            throw new CorsoDiStudioNotFoundException("La matricola fornita non risulta appartenere all'ateneo: "+ matricola);
        return  corsoDiStudioOptional.get();
    }

    public void addStudentNotVerified(StudentDTO user) throws EmailException, CorsoDiStudioNotFoundException, InvalidUserException, EmailNonCorrettaException {
        Optional<CorsoDiStudio> corsoDiStudioOptional;
        Email email = new Email(user.getEmail());
        if(this.usersRepository.existsById(email))
            throw new InvalidUserException("Utente gia registrato, effettua il login");

        //controllo la mail universitaria
        if(!email.getNomeDominio().equals("studenti.unisannio.it"))
            throw new EmailException("Bisogna usare la mail universitaria");
        //controllo ci siano i parametri essenziali
        // il corso di studio c'è controllo che sia nel DB
        corsoDiStudioOptional = this.corsiDiStudioRepository.findById(user.getMatricola().substring(0,3));
        if (!corsoDiStudioOptional.isPresent())
            throw new CorsoDiStudioNotFoundException("Il corso di studio inserito non è presente nel DB");

        //ho passato tutti i controlli posso validare
        this.studentDTORepository.save(user);
    }


    public User addStudentUser(String email) throws EmailException, CorsoDiStudioNotFoundException, InvalidUserException, EmailNonCorrettaException {
        Optional<StudentDTO> dto = this.studentDTORepository.findById(email);
        if(!dto.isPresent())
            throw new InvalidUserException("L'utente non ha effettuato una registrazione");
        User user = userMapper.fromStudentDTOToUser(dto.get(),this.getCorsoDiStudioByMatricola(dto.get().getMatricola()));
        //lo salvo nel nuovo db
        this.usersRepository.save(user);
        this.studentDTORepository.delete(dto.get());
        return user;
    }

    public void validateStudet(StudentDTO studentDTO) throws EmailNonCorrettaException, UserAlreadyExistException, EmailException, CorsoDiStudioNotFoundException, MatricolaNotValidException, InvalidUserException {
        if(this.usersRepository.findById(new Email(studentDTO.getEmail())).isPresent())
            throw new UserAlreadyExistException("Sei già registrato, loggati!");

        Email email = new Email(studentDTO.getEmail());
        if(!email.getNomeDominio().equals("studenti.unisannio.it"))
            throw new EmailException("E' possibile registrarsi solo con email che abbiano come dominio \"studenti.unisannio.it\"");

        if(studentDTO.getMatricola().length()!=9)
            throw new MatricolaNotValidException("La matricola inserita non è valida");

        if(!this.corsiDiStudioRepository.findById(studentDTO.getMatricola().substring(0,3)).isPresent())
            throw new CorsoDiStudioNotFoundException("La matricola inserita non si riferisce a nessun corso di studio noto");

        if(!this.corsiDiStudioRepository.findById(studentDTO.getMatricola().substring(0,3)).get().getDenominazioneFacolta().equals("DIPARTIMENTO DI INGEGNERIA"))
            throw new InvalidUserException("Al momento il sistema è aperto soltanto agli studenti del dipartimento di iNGEGNERIA");
    }
}
