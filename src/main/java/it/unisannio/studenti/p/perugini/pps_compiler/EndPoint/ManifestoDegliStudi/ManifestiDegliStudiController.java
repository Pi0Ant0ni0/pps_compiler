package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants.ERR_MESSAGES;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.SADService;
import it.unisannio.studenti.p.perugini.pps_compiler.Components.ManifestoDegliStudiMaker;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.StudentiService;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.SHARED;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase.AggiungiManfiestoUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase.ManifestoPDFUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.manifestiDegliStudi.usecase.VisualizzaManifestoUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.ADMINstr;
import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.SADstr;

@RestController
@Path("/manifestideglistudi")
public class ManifestiDegliStudiController {
    private Logger logger = LoggerFactory.getLogger(ManifestiDegliStudiController.class);
    @Autowired
    private SADService sadService;
    @Autowired
    private ManifestoDegliStudiMaker manifestoDegliStudiMaker;
    @Autowired
    private ManifestoPDFUseCase manifestoPDFUseCase;
    @Autowired
    private VisualizzaManifestoUseCase visualizzaManifestoUseCase;
    @Autowired
    private AggiungiManfiestoUseCase aggiungiManfiestoUseCase;
    @Autowired
    private StudentiService studentiService;


    @Operation(
            description = "inserisce un nuovo manifesto degli studi nel database",
            tags = { "Manifesti Degli Studi" },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "manifesto aggiunto correttamente",
                    content = @Content(mediaType = "text/plain")
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ordinamento non rispetta le specifiche",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed(value = {ADMINstr, SADstr})
    public Response aggiungiManifesto(@Parameter(required = true, schema = @Schema(implementation = ManifestoDegliStudiDTO.class))
                                          @RequestBody @Valid ManifestoDegliStudiDTO regola) {
        if(SHARED.updatingDatabase){
            return Response.status(Response.Status.BAD_REQUEST).entity(ERR_MESSAGES.DB_UPDATING).build();
        }
        try {
            logger.info("è arrivata una nuova regola: "+regola);
            this.aggiungiManfiestoUseCase.addManifesto(ManifestiDegliStudiMapper.fromRegolaDTOToRegola(regola));
            return Response.status(Response.Status.OK)
                    .entity("Regola aggiunta correttamente")
                    .build();
        } catch (OrdinamentoNotFoundException | ManifestoDegliStudiNonValidoException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @Operation(
            description = "ottengo le preview dei manifesti degli studi di un determinato corso di studio",
            tags = { "Manifesti Degli Studi" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = ManifestoPreviewDTO[].class))
            )
    })
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{codiceCorsoDiStudio}")
    @PermitAll
    public Response getManifestiPreviw(@Parameter(required = true, description = "codice del corso di studio")
                                           @PathParam("codiceCorsoDiStudio")String codiceCorsoDiSudio){
        return Response
                .ok()
                .entity(this.visualizzaManifestoUseCase.getManifesti(codiceCorsoDiSudio)
                        .stream()
                        .map(ManifestiDegliStudiMapper::fromManifestoToPreview)
                        .collect(Collectors.toList())
                ).build();
    }


    @Operation(
            description = "ottengo il manifesto degli studi in formato pdf per la corte ed il corso di studio selezionato",
            tags = { "Manifesti Degli Studi" },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/pdf")
            )
    })
    @GET
    @Produces(org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    @PermitAll()
    @Path("/{codiceCorsoDiStudio}/{coorte}/")
    public StreamingOutput getManifesto(@Parameter(required = true)
                                            @PathParam("coorte")int anno,
                                        @Parameter(required = true,description = "codice del corso di studio")
                                        @PathParam("codiceCorsoDiStudio")String codiceCorsoDiStudio,
                                        @Parameter(required = false, description = "curriculum del manifesto degli studi")
                                        @QueryParam("curriculum") @DefaultValue("") String curriculum) {
        logger.info("Arrivata una richiesta per il manifesto degli studi della corte: "+anno+" per il corso di studi: "+codiceCorsoDiStudio);
        Optional<ManifestoDegliStudi> manifestoDegliStudi = this.manifestoPDFUseCase.manifestoPDF(anno,codiceCorsoDiStudio,curriculum);
        if(manifestoDegliStudi.isPresent()){
            return outputStream -> {
                manifestoDegliStudiMaker.getManifestoDegliStudi(manifestoDegliStudi.get(), outputStream);
            };
        }else {
            return null;
        }
    }


    @Operation(
            description = "ottengo gli orientamenti di una determinata coorte ed un determinato corso di studio",
            tags = { "Manifesti Degli Studi" },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Orientamento[].class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "manifesto non trovato",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {ADMINstr, SADstr})
    @Path("/{codiceCorsoDiStudio}/{coorte}/orientamenti/")
    @PermitAll
    public Response getOrientamenti(@Parameter(required = true, description = "codice del corso di studio")
                                        @PathParam("codiceCorsoDiStudio") String codiceCorsoDiStudio,
                                    @Parameter(required = true)
                                    @PathParam("coorte")int coorte) {
            return Response.ok()
                    .entity(sadService.getOrientamentoManifesto(coorte,codiceCorsoDiStudio))
                    .build();
    }



    @Operation(
            description = "ottengo i curricula di un determinato corso di studio e per una determinata coorte",
            tags = { "Manifesti Degli Studi" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String[].class))
            )
    })
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/{codiceCorsoDiStudio}/{coorte}/curricula")
    public Response getCurricula(@PathParam("codiceCorsoDiStudio")String codiceCorsoDiStudio,
                                 @PathParam("coorte")int coorte){
        return  Response.ok(this.studentiService.getCurricula(codiceCorsoDiStudio,coorte)).build();
    }




}
