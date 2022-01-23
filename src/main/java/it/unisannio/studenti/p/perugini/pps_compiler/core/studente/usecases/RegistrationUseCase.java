package it.unisannio.studenti.p.perugini.pps_compiler.core.studente.usecases;

import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.StudentDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;

import javax.ws.rs.core.Cookie;

public interface RegistrationUseCase {
    void register(StudentDTO dto) throws EmailNonCorrettaException, EmailException, MatricolaNotValidException, CorsoDiStudioNotFoundException, InvalidUserException, UserAlreadyExistException;
    User verifyRegistration(String otpRichiesta, Cookie otpCookie, Email email) throws OTPExpiredException;
}
