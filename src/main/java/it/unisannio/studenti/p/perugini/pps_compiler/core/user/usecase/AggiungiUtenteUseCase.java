package it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InvalidUserException;

public interface AggiungiUtenteUseCase {
    void aggiungiUtente(User utente) throws EmailException, InvalidUserException, CorsoDiStudioNotFoundException;
}
