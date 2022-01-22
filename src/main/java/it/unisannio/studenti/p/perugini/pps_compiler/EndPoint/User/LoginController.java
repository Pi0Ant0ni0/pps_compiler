package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.Components.JwtProvider;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CONSTANTS;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase.LoginUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


@Path("/auth")
public class LoginController {

    @Autowired
    private LoginUseCase loginUseCase;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtProvider jwtProvider;


    @Operation(
            description = "login al sistema",
            tags = { "Auth" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "email non associata a nessun account",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response login(@Parameter(required = true)
                              @RequestBody String email){
        try {
            Email emailUser = new Email(email);
            this.loginUseCase.login(emailUser);
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
        } catch (EmailNonCorrettaException  | UserNotFound e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }


    @Operation(
            description = "verifica del login al sistema",
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
    public Response verifyOTP(@Parameter(required = true, description = "cookie contenente otp hashed", schema = @Schema(implementation = Cookie.class))
                                  @CookieParam(CONSTANTS.cookie) Cookie cookie,
                              @Parameter(required = true)
                              @RequestBody String otp,
                              @Parameter(required = true)
                                  @PathParam("email") String email){
        try {
            Email emailUser = new Email(email);
            User user = this.loginUseCase.verifyLogin(otp,cookie,emailUser);
            String jwt = this.jwtProvider.generateJWT(user);
            //creo un jwt e lo mando insieme ai dati dell'utente
            return Response
                    .ok()
                    .entity(userMapper.fromUserToUserAuthenticatedDTO(user))
                    .header(HttpHeaders.AUTHORIZATION,jwt)
                    .build();
        }catch (EmailNonCorrettaException | OTPExpiredException  e){
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }



}
