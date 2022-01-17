package it.unisannio.studenti.p.perugini.pps_compiler.core.user.port;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;

public interface DeleteUserPort {
    void deleteUser(Email email);
}
