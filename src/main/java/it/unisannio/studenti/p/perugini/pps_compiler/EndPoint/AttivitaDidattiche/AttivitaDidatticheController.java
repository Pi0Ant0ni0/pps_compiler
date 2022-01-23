package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    private InsegnamentoService insegnamentoService;
    @Autowired
    private AttivitaDidatticheMapper attivitaDidatticheMapper;

    private Logger logger = LoggerFactory.getLogger(AttivitaDidatticheController.class);

    @Operation(description = "Aggiornamento Del database delle attività didattiche e dei corsi di studio",  tags = { "Attività Didattiche" }, security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "aggiornamento andato a buon fine"),
            @ApiResponse(responseCode = "500", description = "aggiornamento fallito")
    })
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed(value = {ADMINstr, SADstr})
    public Response updateDataBase(){
        try {
            logger.info("Aggiornamento del database iniziato");
            sadService.updateDatabse();
            logger.info("Aggiornamento del database concluso");
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

    @Operation(description = "Richiede tutte le attività didattiche presenti nel database",  tags = { "Attività Didattiche" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidattiche() {
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


    @Operation(description = "Richiede tutte le attività didattiche erogate presenti nel database",  tags = { "Attività Didattiche" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Path("/{codiceCorsoDiStudio}/erogate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidatticheByCorsoDiStudio(@Parameter(description = "codice del corso di studio", required = true)
                                                             @PathParam("codiceCorsoDiStudio")String codiceCorsoDiStudio){
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


    @Operation(description = "Richiede tutte le attività didattiche programmate presenti nel database",  tags = { "Attività Didattiche" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Path("/{codiceCorsoDiStudio}/programmate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getInsegnamentiProgrammatiByCorsoDiStudio(@Parameter(description = "codice del corso di studio", required = true)
                                                                  @PathParam("codiceCorsoDiStudio")String codiceCorsoDiStudio){
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




    @Operation(description = "Richiede tutte le attività didattiche a scelta per una data coorte, non solo le attività di automatica approvazione",  tags = { "Attività Didattiche" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Path("{codiceCorsoDiStudio}/{coorte}/aScelta")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getCorsiASceltaLibera(@Parameter(description = "codice del corso di studio", required = true)
                                                 @PathParam("codiceCorsoDiStudio") String cdsOffId,
                                             @Parameter(description = "coorte per la quale si vuole ricercare le attività a scelta", required = true)
                                             @PathParam("coorte") int coorte,
                                          @Parameter(description = "curriculum del manifesto degli studi", required = false)
                                          @QueryParam("curriculum")@DefaultValue("") String curriculum) {

        try {
            return Response
                    .ok()
                    .entity(this.studentiService
                            .getFreeChoiceCourses(cdsOffId, coorte, curriculum)
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

    @Operation(description = "Richiede tutte le attività didattiche di un dipartimento presenti nel database",  tags = { "Attività Didattiche" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",content = @Content(schema = @Schema(implementation = AttivitaDidatticaPPSDTO.class))),
    })
    @GET
    @Path("/dipartimento/{dipartimento}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getAttivitaDidatticheByDipartimento(@Parameter(name = "denominazione del dipartimento", required = true)@PathParam("dipartimento")String dipartimento){
        return Response
                .ok()
                .entity(this.insegnamentoService
                        .getAttivitaDidattichePerDipartimento(dipartimento)
                        .stream()
                        .map(attivitaDidatticheMapper::fromInsegnamentoToInsegnamentoDTO)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
