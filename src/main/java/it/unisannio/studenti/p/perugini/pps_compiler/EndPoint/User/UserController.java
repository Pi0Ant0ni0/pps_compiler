package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailNonCorrettaException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InvalidUserException;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase.AggiungiUtenteUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase.RimuoviUtenteUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.user.usecase.VisualizzaUtentiUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.stream.Collectors;

import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.ADMINstr;


@Path("/users")
@RolesAllowed("ADMIN")
public class UserController {
    @Autowired
    private VisualizzaUtentiUseCase visualizzaUtentiUseCase;
    @Autowired
    private AggiungiUtenteUseCase aggiungiUtenteUseCase;
    @Autowired
    private RimuoviUtenteUseCase rimuoviUtenteUseCase;
    @Autowired
    private UserMapper userMapper;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(
            description = "ottengo tutti gli utenti del sistema",
            tags = { "Utenti" },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = GenericUserDTO[].class))
            )
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @RolesAllowed(ADMINstr)
    public Response getUsers(){
        this.logger.info("Sono stati richiesti tutti gli utenti");
        return Response
                .ok()
                .entity(this.visualizzaUtentiUseCase
                        .visualizzaUtenti()
                        .stream()
                        .map(userMapper::fromUserToGenericUserDTO)
                        .collect(Collectors.toList())
                ).build();
    }


    @Operation(
            description = "aggiungo un nuovo docente al sistema",
            tags = { "Utenti" },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "il docente non rispetta le specifiche",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/docenti")
    @RolesAllowed(ADMINstr)
    public Response addDocente(@Parameter(required = true, schema = @Schema(implementation = DocenteDTO.class))
                                   @Valid @RequestBody DocenteDTO docenteDTO){
        logger.info("E' stata richiesta l'aggiunta di un nuovo docente");
        try {
            this.aggiungiUtenteUseCase.aggiungiUtente(userMapper.fromDocenteDtoToUser(docenteDTO));
            return Response.ok().entity("Utente aggiunto correttamente").build();
        } catch (EmailException | InvalidUserException | EmailNonCorrettaException | CorsoDiStudioNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Operation(
            description = "aggiungo un nuovo operatore sad al sistema",
            tags = { "Utenti" },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "l'operatore sad non rispetta le specifiche",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/sad")
    @RolesAllowed(ADMINstr)
    public Response addSAD(@Parameter(required = true, schema = @Schema(implementation = SadDTO.class))
                               @Valid @RequestBody SadDTO sadDTO){
        logger.info("E' stata richiesta l'aggiunta di un nuovo operatore sad");
        try {
            this.aggiungiUtenteUseCase.aggiungiUtente(userMapper.fromSadDTOToUser(sadDTO));
            return Response.ok().entity("Utente aggiunto correttamente").build();
        } catch (EmailException | InvalidUserException | EmailNonCorrettaException | CorsoDiStudioNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }



    @Operation(
            description = "elimino un utente dal sistema",
            tags = { "Utenti" },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "l'email specificata non è corretta",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{email}/delete")
    @RolesAllowed(ADMINstr)
    public Response deleteUser(@Parameter(required = true, description = "email associata all'utente da eliminare")
                                   @PathParam("email")String email){
        this.logger.info("E' stata richiesta la cancellazione dell'utente con email: "+email);
        try {
            this.rimuoviUtenteUseCase.rimuoviUtente(new Email(email));
            return Response.ok().entity("Utente Eliminato Correttamente").build();
        } catch (EmailNonCorrettaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
}
