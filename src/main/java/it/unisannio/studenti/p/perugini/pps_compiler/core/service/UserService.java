package it.unisannio.studenti.p.perugini.pps_compiler.core.service;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InvalidUserException;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.CreateUserPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.DeleteUserPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.ListUsersPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase.AggiungiUtenteUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase.RimuoviUtenteUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase.VisualizzaUtentiUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements VisualizzaUtentiUseCase, AggiungiUtenteUseCase, RimuoviUtenteUseCase {
    @Autowired
    private ListUsersPort listUsersPort;
    @Autowired
    private CreateUserPort createUserPort;
    @Autowired
    private DeleteUserPort deleteUserPort;
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;

    @Override
    public List<User> visualizzaUtenti() {
        return this.listUsersPort.listUsers();
    }

    @Override
    public void aggiungiUtente(User user) throws EmailException, InvalidUserException, CorsoDiStudioNotFoundException {

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
                //per gli studenti deve esserci un solo corso di studio
                if(user.getCorsoDiStudio().get().size()!=1)
                    throw new InvalidUserException("Uno studente puo essere associato ad un unico corso di studio");
                corsoDiStudioOptional = this.readCorsoDiStudioPort.findCorsoDiStudioById(user.getCorsoDiStudio().get().get(0).getCodice());
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
                //un docente puo avere diversi corsi di studio
                for(CorsoDiStudio corsoDiStudio: user.getCorsoDiStudio().get()) {
                    corsoDiStudioOptional = this.readCorsoDiStudioPort.findCorsoDiStudioById(corsoDiStudio.getCodice());
                    if (!corsoDiStudioOptional.isPresent())
                        throw new CorsoDiStudioNotFoundException("Il corso di studio inserito non è presente nel DB");
                }
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


        this.createUserPort.save(user);
    }

    @Override
    public void rimuoviUtente(Email email) {
        this.deleteUserPort.deleteUser(email);
    }
}
