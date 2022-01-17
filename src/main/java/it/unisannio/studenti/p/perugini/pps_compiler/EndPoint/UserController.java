package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.DocenteDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.SadDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserMapper;
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

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/docenti")
    @RolesAllowed(ADMINstr)
    public Response addDocente(@Valid @RequestBody DocenteDTO docenteDTO){
        logger.info("E' stata richiesta l'aggiunta di un nuovo docente");
        try {
            this.aggiungiUtenteUseCase.aggiungiUtente(userMapper.fromDocenteDtoToUser(docenteDTO));
            return Response.ok().entity("Utente aggiunto correttamente").build();
        } catch (EmailException | InvalidUserException | EmailNonCorrettaException | CorsoDiStudioNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/sad")
    @RolesAllowed(ADMINstr)
    public Response addSAD(@Valid @RequestBody SadDTO sadDTO){
        logger.info("E' stata richiesta l'aggiunta di un nuovo operatore sad");
        try {
            this.aggiungiUtenteUseCase.aggiungiUtente(userMapper.fromSadDTOToUser(sadDTO));
            return Response.ok().entity("Utente aggiunto correttamente").build();
        } catch (EmailException | InvalidUserException | EmailNonCorrettaException | CorsoDiStudioNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

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

    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{email}/delete")
    @RolesAllowed(ADMINstr)
    public Response deleteUser(@PathParam("email")String email){
        this.logger.info("E' stata richiesta la cancellazione dell'utente con email: "+email);
        try {
            this.rimuoviUtenteUseCase.rimuoviUtente(new Email(email));
            return Response.ok().entity("Utente Eliminato Correttamente").build();
        } catch (EmailNonCorrettaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
}
