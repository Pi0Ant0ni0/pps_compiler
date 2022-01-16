package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailNonCorrettaException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InvalidUserException;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminSerivice {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CorsiDiStudioRepository corsiDiStudioRepository;

    public void addUser(User user) throws EmailException, CorsoDiStudioNotFoundException, InvalidUserException {
        Optional<CorsoDiStudio> corsoDiStudioOptional;

        switch (user.getRole()){
            case STUDENTE:
                //controllo la mail universitaria
                if(!user.getEmail().getNomeDominio().equals("studenti.unisannio.it"))
                    throw new EmailException("Bisogna usare la mail universitaria");
                //controllo ci siano i parametri essenziali
                if (!user.getMatricola().isPresent() || !user.getCorsoDiStudio().isPresent())
                    throw new InvalidUserException("uno studente deve avere matricola e corso di studio");
                // il corso di studio c'è controllo che sia nel DB
                corsoDiStudioOptional = this.corsiDiStudioRepository.findById(user.getCorsoDiStudio().get().getCodice());
                if (!corsoDiStudioOptional.isPresent())
                    throw new CorsoDiStudioNotFoundException("Il corso di studio inserito non è presente nel DB");
                break;
            case DOCENTE:
                //controllo la mial
                if(!user.getEmail().getNomeDominio().equals("unisannio.it"))
                    throw new EmailException("Bisogna usare la mail universitaria");
                if (!user.getCorsoDiStudio().isPresent())
                    throw new InvalidUserException("Un docente deve avere un corso di studio");
                // il corso di studio c'è controllo che sia nel DB
                corsoDiStudioOptional = this.corsiDiStudioRepository.findById(user.getCorsoDiStudio().get().getCodice());
                if (!corsoDiStudioOptional.isPresent())
                    throw new CorsoDiStudioNotFoundException("Il corso di studio inserito non è presente nel DB");
                break;
            case SAD:
                //controllo la mial
                if(!user.getEmail().getNomeDominio().equals("unisannio.it"))
                    throw new EmailException("Bisogna usare la mail universitaria");
                //non puo avere matricola o corso di studio
                if (user.getMatricola().isPresent() || user.getCorsoDiStudio().isPresent())
                    throw new InvalidUserException("Un sad non puo avere matricola o corso di studio");
            case ADMIN:
                //l'admin non ha limitazioni
        }

        //ho passato tutti i controlli posso validare
        this.usersRepository.save(user);
    }

    public List<User> getUtenti() {
        return this.usersRepository.findAll();
    }

    public void deleteUtente(String email) throws EmailNonCorrettaException {
        this.usersRepository.deleteById(new Email(email));
    }
}
