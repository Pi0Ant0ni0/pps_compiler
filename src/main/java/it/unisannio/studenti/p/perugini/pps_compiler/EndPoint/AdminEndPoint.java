package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint;

import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.DocenteDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.GenericUserDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.SadDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailNonCorrettaException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InvalidUserException;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AdminSerivice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.ADMINstr;


@Path("/admin")
@RolesAllowed("ADMIN")
public class AdminEndPoint {
    @Autowired
    private AdminSerivice adminSerivice;
    @Autowired
    private UserMapper userMapper;
    private Logger logger = LoggerFactory.getLogger(AdminEndPoint.class);

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/users/docenti")
    @RolesAllowed(ADMINstr)
    public Response addDocente(@Valid @RequestBody DocenteDTO docenteDTO){
        try {
            adminSerivice.addUser(userMapper.fromDocenteDtoToUser(docenteDTO));
            return Response.ok().entity("Utente aggiunto correttamente").build();
        } catch (EmailException | InvalidUserException | EmailNonCorrettaException | CorsoDiStudioNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @POST
    @Path("/users/sad")
    @RolesAllowed(ADMINstr)
    public Response addSAD(@Valid @RequestBody SadDTO sadDTO){
        try {
            adminSerivice.addUser(userMapper.fromSadDTOToUser(sadDTO));
            return Response.ok().entity("Utente aggiunto correttamente").build();
        } catch (EmailException | InvalidUserException | EmailNonCorrettaException | CorsoDiStudioNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/users")
    @RolesAllowed(ADMINstr)
    public Response getUsers(){
        this.logger.info("Sono stati richiesti tutti gli utenti");
        return Response
                .ok()
                .entity(this.adminSerivice
                        .getUtenti()
                        .stream()
                        .map(userMapper::fromUserToGenericUserDTO)
                        .collect(Collectors.toList())
                ).build();
    }

    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/users/{email}/delete")
    @RolesAllowed(ADMINstr)
    public Response deleteUser(@PathParam("email")String email){
        this.logger.info("E' stata richiesta la cancellazione dell'utente con email: "+email);
        try {
            this.adminSerivice.deleteUtente(email);
            return Response.ok().entity("Utente Eliminato Correttamente").build();
        } catch (EmailNonCorrettaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
}
