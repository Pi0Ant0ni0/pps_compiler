package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CONSTANTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Path("/auth")
public class AuthEndPoint {
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserMapper userMapper;


    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response authenticate(@RequestBody String email){
        try {
            Email emailUser = new Email(email);
            if(this.authorizationService.validateLogin(emailUser)) {
                String hashOTP = authorizationService.sendOtp(emailUser);
                Date expirationDate = Date.from(Instant.now().plus(5, ChronoUnit.MINUTES));
                NewCookie otpCookie = new NewCookie(CONSTANTS.cookie,
                        hashOTP,
                        null,
                        null,
                        NewCookie.DEFAULT_VERSION,
                        null,
                        5*60,
                        expirationDate,
                        false,
                        false
                );
                return Response
                        .ok()
                        .entity("E' stata mandata una mail di conferma all'indirizzo specificato")
                        .cookie(otpCookie)
                        .build();
            }else{
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("E-mail non è associata a nessun utente")
                        .build();
            }
        } catch (EmailNonCorrettaException | EmailException e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/{email}/verify")
    public Response verifyOTP(@CookieParam(CONSTANTS.cookie) Cookie cookie,
                                @RequestBody String otp, @PathParam("email") String email){
        try {
            if (authorizationService.verifyOtp(new Email(email), otp, cookie)) {
                User user = this.authorizationService.getUserByEmail(new Email(email));
                String jwt = this.authorizationService.generateJWT(user);
                //creo un jwt e lo mando insieme ai dati dell'utente
                return Response
                        .ok()
                        .entity(userMapper.fromUserToUserAuthenticatedDTO(user))
                        .header(HttpHeaders.AUTHORIZATION,jwt)
                        .build();
            } else {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("OTP ERRATO")
                        .build();
            }
        }catch (EmailNonCorrettaException | OTPExpiredException | UserNotFound e){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }



}
