package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserAlreadyExistException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.RegistrationService;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CONSTANTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Path("/register")
public class RegisterEndPoint {
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserMapper userMapper;


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response register(@Valid @RequestBody StudentDTO studentDTO){
        try {
            this.registrationService.validateStudet(studentDTO);
            String hashOTP = authorizationService.sendOtp(new Email(studentDTO.getEmail()));
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
            this.registrationService.addStudentNotVerified(studentDTO);
            return Response.ok()
                    .entity("E' stata mandata una OTP all'email specificata")
                    .cookie(otpCookie)
                    .build();
        } catch (EmailException | EmailNonCorrettaException | CorsoDiStudioNotFoundException | InvalidUserException| MatricolaNotValidException |UserAlreadyExistException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/{email}/verify")
    public Response verifyRegistration(@PathParam("email")String email,
                                       @RequestBody String otp,
                                       @CookieParam(CONSTANTS.cookie)Cookie otpCookie){
        try {
            if(this.authorizationService.verifyOtp(new Email(email),otp,otpCookie)) {
                User user = this.registrationService.addStudentUser(email);
                String jwt = this.authorizationService.generateJWT(user);
                return Response.ok()
                        .entity(userMapper.fromUserToUserAuthenticatedDTO(user))
                        .header(HttpHeaders.AUTHORIZATION,jwt)
                        .build();
            }
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("OTP ERRATO")
                    .build();
        } catch (EmailException | EmailNonCorrettaException | InvalidUserException | CorsoDiStudioNotFoundException | OTPExpiredException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
