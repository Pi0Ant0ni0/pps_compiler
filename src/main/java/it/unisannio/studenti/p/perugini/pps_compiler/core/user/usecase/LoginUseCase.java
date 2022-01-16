package it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase;

import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OTPExpiredException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserNotFound;

import javax.ws.rs.core.Cookie;

public interface LoginUseCase {
    void login(Email email) throws UserNotFound;
    User verifyLogin(String otpRichiesta, Cookie otpCookie, Email email) throws OTPExpiredException;
}
