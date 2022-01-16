package it.unisannio.studenti.p.perugini.pps_compiler.core.user.adapter;

import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.UsersRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.CreateUserPort;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.port.ReadUserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserAdapter implements CreateUserPort, ReadUserPort {
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public User save(User user) {
        return this.usersRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(Email email) {
        return this.usersRepository.findById(email);
    }
}
