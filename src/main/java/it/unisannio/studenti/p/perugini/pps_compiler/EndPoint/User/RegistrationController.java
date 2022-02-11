package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Components.JwtProvider;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserAlreadyExistException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CONSTANTS;
import it.unisannio.studenti.p.perugini.pps_compiler.core.studente.usecases.RegistrationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RegistrationController {

    /**Utilizzato per inviare OTP e verificarlo*/
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RegistrationUseCase registrationUseCase;
    /**Genera i jwt*/
    @Autowired
    private JwtProvider jwtProvider;
    private Logger logger = LoggerFactory.getLogger(RegistrationController.class);


    @Operation(
            description = "registrazione di uno studente al sistema",
            tags = { "Auth" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "i dati dello studente non rispettano le specifiche",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response register(@Parameter(required = true, schema = @Schema(implementation = StudentDTO.class))
                                 @Valid @RequestBody StudentDTO studentDTO){
        logger.info("Arrivata un richiesta di registrazione dalla email: "+studentDTO.getEmail());
        try {
            this.registrationUseCase.register(studentDTO);
            logger.info("Registrazione avvenuta con successo, si è in attesa di verifica tramite OTP");
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
            return Response.ok()
                    .entity("E' stata mandata una OTP all'email specificata")
                    .cookie(otpCookie)
                    .build();
        } catch (EmailException | EmailNonCorrettaException | CorsoDiStudioNotFoundException | InvalidUserException| MatricolaNotValidException |UserAlreadyExistException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @Operation(
            description = "verifica della registrazione di uno studente al sistema",
            tags = { "Auth" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = UserAuthenticatedDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "otp errato o scaduto",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/{email}/verify")
    public Response verifyRegistration(@Parameter(required = true)@PathParam("email")String email,
                                       @Parameter(required = true)@RequestBody String otp,
                                       @Parameter(required = true, description = "cookie con otp hashed", schema = @Schema(implementation = Cookie.class))
                                           @CookieParam(CONSTANTS.cookie)Cookie otpCookie){
        logger.info("Arrivata Richiesta di validazione di una registrazione con email: "+email);
        try {
            User user = this.registrationUseCase.verifyRegistration(otp,otpCookie,new Email(email));
            logger.info("Verifica andata a buon fine, è stato generato il JWT");
            String jwt = this.jwtProvider.generateJWT(user);
            return Response.ok()
                    .entity(userMapper.fromUserToUserAuthenticatedDTO(user))
                    .header(HttpHeaders.AUTHORIZATION,jwt)
                    .build();
        } catch (EmailNonCorrettaException | OTPExpiredException  e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
