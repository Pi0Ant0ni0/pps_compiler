package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants.ERR_MESSAGES;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.RegolaNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.TipoCorsoDiLaureaNonSupportatoException;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AttivitaDidatticaService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.SADService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.StudentiService;

import it.unisannio.studenti.p.perugini.pps_compiler.Utils.SHARED;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.ADMINstr;
import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.SADstr;

@Path("/attivitadidattiche")
public class AttivitaDidatticheController {

    @Autowired
    private StudentiService studentiService;
    @Autowired
    private SADService sadService;
    @Autowired
    private AttivitaDidatticaService attivitaDidatticaService;
    @Autowired
    private AttivitaDidatticheMapper attivitaDidatticheMapper;

    private Logger logger = LoggerFactory.getLogger(AttivitaDidatticheController.class);

    @Operation(description = "Aggiornamento Del database delle attivit?? didattiche e dei corsi di studio", tags = {"Attivit?? Didattiche"}, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "aggiornamento andato a buon fine"),
            @ApiResponse(responseCode = "500", description = "aggiornamento fallito")
    })
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed(value = {ADMINstr, SADstr})
    public CompletableFuture<Response> updateDataBase() throws InterruptedException {
        logger.info("Aggiornamento del database iniziato");
        CompletableFuture<Void> result = sadService.updateDatabse();
        return result
                .thenApply(unused -> Response.ok().entity("Aggiornamento del database concluso").build())
                .exceptionally(throwable -> Response.serverError().entity(throwable.getMessage()).build());
    }

    @Operation(description = "Richiede tutte le attivit?? didattiche presenti nel database", tags = {"Attivit?? Didattiche"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidattiche() {
        if(SHARED.updatingDatabase){
            return Response.status(Response.Status.BAD_REQUEST).entity(ERR_MESSAGES.DB_UPDATING).build();
        }
        return Response
                .ok()
                .entity(this.attivitaDidatticaService
                        .getAttivitaDidattiche()
                        .stream()
                        .map(attivitaDidatticheMapper::toAttivitaDidatticaPPSDTO)
                        .collect(Collectors.toList())
                )
                .build();
    }


    @Operation(description = "Richiede tutte le attivit?? didattiche erogate presenti nel database", tags = {"Attivit?? Didattiche"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Path("/{codiceCorsoDiStudio}/erogate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidatticheByCorsoDiStudio(@Parameter(description = "codice del corso di studio", required = true)
                                                         @PathParam("codiceCorsoDiStudio") String codiceCorsoDiStudio) {
        if(SHARED.updatingDatabase){
            return Response.status(Response.Status.BAD_REQUEST).entity(ERR_MESSAGES.DB_UPDATING).build();
        }
        return Response
                .ok()
                .entity(this.attivitaDidatticaService
                        .getAttivitaDidatticaPerCorsoDiStudio(codiceCorsoDiStudio)
                        .stream()
                        .map(attivitaDidatticheMapper::toAttivitaDidatticaPPSDTO)
                        .collect(Collectors.toList())
                )
                .build();
    }


    @Operation(description = "Richiede tutte le attivit?? didattiche programmate presenti nel database", tags = {"Attivit?? Didattiche"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Path("/{codiceCorsoDiStudio}/programmate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidatticheProgrammateByCorsoDiStudio(@Parameter(description = "codice del corso di studio", required = true)
                                                              @PathParam("codiceCorsoDiStudio") String codiceCorsoDiStudio) {
        if(SHARED.updatingDatabase){
            return Response.status(Response.Status.BAD_REQUEST).entity(ERR_MESSAGES.DB_UPDATING).build();
        }
        return Response
                .ok()
                .entity(this.attivitaDidatticaService
                        .getAttivitaDidatticheProgrammatePerCorsoDiStudio(codiceCorsoDiStudio)
                        .stream()
                        .map(attivitaDidatticheMapper::toAttivitaDidatticaPPSDTO)
                        .collect(Collectors.toList())
                )
                .build();
    }


    @Operation(description = "Richiede tutte le attivit?? didattiche a scelta per una data coorte, non solo le attivit?? di automatica approvazione", tags = {"Attivit?? Didattiche"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Path("{codiceCorsoDiStudio}/{coorte}/aScelta")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getCorsiASceltaLibera(@Parameter(description = "codice del corso di studio", required = true)
                                          @PathParam("codiceCorsoDiStudio") String cdsOffId,
                                          @Parameter(description = "coorte per la quale si vuole ricercare le attivit?? a scelta", required = true)
                                          @PathParam("coorte") int coorte,
                                          @Parameter(description = "curriculum del manifesto degli studi", required = false)
                                          @QueryParam("curriculum") @DefaultValue("") String curriculum) {
        if(SHARED.updatingDatabase){
            return Response.status(Response.Status.BAD_REQUEST).entity(ERR_MESSAGES.DB_UPDATING).build();
        }
        try {
            return Response
                    .ok()
                    .entity(this.studentiService
                            .getFreeChoiceCourses(cdsOffId, coorte, curriculum)
                            .stream()
                            .map(attivitaDidatticheMapper::toAttivitaDidatticaPPSDTO)
                            .collect(Collectors.toList())
                    )
                    .build();
        } catch (IllegalArgumentException | TipoCorsoDiLaureaNonSupportatoException | CorsoDiStudioNotFoundException | RegolaNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Il tipo di corso di laurea cercato non ?? valido")
                    .build();
        }
    }

    @Operation(description = "Richiede tutte le attivit?? didattiche di un dipartimento presenti nel database", tags = {"Attivit?? Didattiche"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Path("/dipartimento/{dipartimento}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidatticheByDipartimento(@Parameter(name = "denominazione del dipartimento", required = true) @PathParam("dipartimento") String dipartimento) {
        if(SHARED.updatingDatabase){
            return Response.status(Response.Status.BAD_REQUEST).entity(ERR_MESSAGES.DB_UPDATING).build();
        }
        return Response
                .ok()
                .entity(this.attivitaDidatticaService
                        .getAttivitaDidattichePerDipartimento(dipartimento)
                        .stream()
                        .map(attivitaDidatticheMapper::toAttivitaDidatticaPPSDTO)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
