package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants.ERR_MESSAGES;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CONSTANTS;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;
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
                if(!user.getEmail().getNomeDominio().equals(CONSTANTS.DOMINIO_UNISANNIO_STUDENTE))
                    throw new EmailException(ERR_MESSAGES.REGISTRATION_EMAIL);
                //controllo ci siano i parametri essenziali
                if (!user.getMatricola().isPresent() || !user.getCorsoDiStudio().isPresent())
                    throw new InvalidUserException(ERR_MESSAGES.STUDENTE_NON_VALIDO);
                // il corso di studio c'è controllo che sia nel DB
                //per gli studenti deve esserci un solo corso di studio
                if(user.getCorsoDiStudio().get().size()!=1)
                    throw new InvalidUserException(ERR_MESSAGES.STUDENTE_NON_VALIDO);
                corsoDiStudioOptional = this.readCorsoDiStudioPort.findCorsoDiStudioById(user.getCorsoDiStudio().get().get(0).getCodice());
                if (!corsoDiStudioOptional.isPresent())
                    throw new CorsoDiStudioNotFoundException(ERR_MESSAGES.CORSO_NOT_FOUND+user.getCorsoDiStudio().get().get(0).getCodice());
                break;
            case DOCENTE:
                //controllo la mial
                if(!user.getEmail().getNomeDominio().equals(CONSTANTS.DOMINIO_UNISANNIO_DOCENTE))
                    throw new EmailException(ERR_MESSAGES.REGISTRATION_EMAIL);
                if (!user.getCorsoDiStudio().isPresent())
                    throw new InvalidUserException(ERR_MESSAGES.DOCENTE_NON_VALIDO);
                // il corso di studio c'è controllo che sia nel DB
                //un docente puo avere diversi corsi di studio
                for(CorsoDiStudio corsoDiStudio: user.getCorsoDiStudio().get()) {
                    corsoDiStudioOptional = this.readCorsoDiStudioPort.findCorsoDiStudioById(corsoDiStudio.getCodice());
                    if (!corsoDiStudioOptional.isPresent())
                        throw new CorsoDiStudioNotFoundException(ERR_MESSAGES.CORSO_NOT_FOUND+corsoDiStudio.getCodice());
                }
                break;
            case SAD:
                //controllo la mial
                if(!user.getEmail().getNomeDominio().equals(CONSTANTS.DOMINIO_UNISANNIO_DOCENTE))
                    throw new EmailException(ERR_MESSAGES.REGISTRATION_EMAIL);
                //non puo avere matricola o corso di studio
                if (user.getMatricola().isPresent() || user.getCorsoDiStudio().isPresent())
                    throw new InvalidUserException(ERR_MESSAGES.SAD_NON_VALIDO);
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
