package it.unisannio.studenti.p.perugini.pps_compiler.core.user.port;

import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;

import java.util.List;

public interface ListUsersPort {
    List<User> listUsers();
}
