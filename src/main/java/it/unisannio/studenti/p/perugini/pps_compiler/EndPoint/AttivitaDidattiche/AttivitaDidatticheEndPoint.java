package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.RegolaNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.TipoCorsoDiLaureaNonSupportatoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.InsegnamentoService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.SADService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.StudentiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.ADMINstr;
import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.SADstr;


@Path("/insegnamenti")
public class AttivitaDidatticheEndPoint {

    @Autowired
    private StudentiService studentiService;
    @Autowired
    private SADService sadService;
    @Autowired
    private InsegnamentoService insegnamentoService;
    @Autowired
    private AttivitaDidatticheMapper attivitaDidatticheMapper;
    private  static boolean updatingDB = false;

    private Logger logger = LoggerFactory.getLogger(AttivitaDidatticheEndPoint.class);

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed(value = {ADMINstr, SADstr})
    public Response updateDataBase(){
        try {
            updatingDB=true;
            logger.info("Aggiornamento del database iniziato");
            sadService.updateDatabse();
            logger.info("Aggiornamento del database concluso");
            updatingDB=false;
            return Response
                    .ok()
                    .entity("DataBase aggiornato correttamente")
                    .build();
        } catch (InterruptedException e) {
            return Response
                    .status(500)
                    .entity("Procedura Abortita")
                    .build();
        }catch (NullPointerException e){
            return Response
                    .status(500)
                    .entity("Impossibile recuperare un insegnamento, Procedura abortita")
                    .build();
        }
    }


    @GET
    @Path("/{codiceCorsoDiStudio}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidatticheByCorsoDiStudio(@PathParam("codiceCorsoDiStudio")String codiceCorsoDiStudio){
        return Response
                .ok()
                .entity(this.insegnamentoService
                        .getInsegnamentiPerCorsoDiStudio(codiceCorsoDiStudio)
                        .stream()
                        .map(attivitaDidatticheMapper::fromInsegnamentoToInsegnamentoDTO)
                        .collect(Collectors.toList())
                )
                .build();
    }


    @GET
    @Path("/{codiceCorsoDiStudio}/programmati")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getInsegnamentiProgrammatiByCorsoDiStudio(@PathParam("codiceCorsoDiStudio")String codiceCorsoDiStudio){
        return Response
                .ok()
                .entity(this.insegnamentoService
                        .getInsegnamentiProgrammatiPerCorsoDiStudio(codiceCorsoDiStudio)
                        .stream()
                        .map(attivitaDidatticheMapper::fromInsegnamentoToInsegnamentoDTO)
                        .collect(Collectors.toList())
                )
                .build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidattiche() throws CorsoDiStudioNotFoundException {
        return Response
                .ok()
                .entity(this.insegnamentoService
                        .getInsegnamenti()
                        .stream()
                        .map(attivitaDidatticheMapper::fromInsegnamentoToInsegnamentoDTO)
                        .collect(Collectors.toList())
                )
                .build();
    }



    @GET
    @Path("{codiceCorsoDiStudio}/{coorte}/aScelta")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getAllFreeChoiceCourses (@PathParam("codiceCorsoDiStudio") String cdsOffId,
                                             @PathParam("coorte") int coorte) {

        try {
            return Response
                    .ok()
                    .entity(this.studentiService
                            .getFreeChoiceCourses(cdsOffId, coorte)
                            .stream()
                            .map(attivitaDidatticheMapper::fromInsegnamentoToInsegnamentoDTO)
                            .collect(Collectors.toList())
                    )
                    .build();
        }catch (IllegalArgumentException | TipoCorsoDiLaureaNonSupportatoException| CorsoDiStudioNotFoundException| RegolaNotFoundException e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Il tipo di corso di laurea cercato non è valido")
                    .build();
        }
    }
}
