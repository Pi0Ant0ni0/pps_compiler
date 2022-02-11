package it.unisannio.studenti.p.perugini.pps_compiler.core.user.port;

import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;

public interface CreateUserPort {
    User save(User user);
}
