package it.unisannio.studenti.p.perugini.pps_compiler.core.user.port;

import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;

import java.util.Optional;

public interface ReadUserPort {
    Optional<User> findUserById(Email email);
}
