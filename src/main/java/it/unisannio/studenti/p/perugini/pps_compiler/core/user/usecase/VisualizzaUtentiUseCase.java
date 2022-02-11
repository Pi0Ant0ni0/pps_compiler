package it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;

import java.util.List;

public interface VisualizzaUtentiUseCase {
    List<User> visualizzaUtenti();
}
