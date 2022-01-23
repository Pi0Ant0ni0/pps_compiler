package it.unisannio.studenti.p.perugini.pps_compiler.Services;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.CorsiDiStudioRepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.StudentDTORepository;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class RegistrationService {
    @Autowired
    private CorsiDiStudioRepository corsiDiStudioRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private StudentDTORepository studentDTORepository;
    @Autowired
    private UserMapper userMapper;


}
