package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.CorsoDiStudioService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.SADService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/corsiDiStudio")
public class CorsiDiStudioController {
    @Autowired
    private CorsoDiStudioService corsoDiStudioService;
    @Autowired
    private CorsoDiStudioMapper corsoDiStudioMapper;


    @Operation(
            description = "ottengo tutti i corsi di studio",
            tags = { "Corsi Di Studio" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "corsi ottenuti correttamente",
                    content = @Content(schema = @Schema(implementation = CorsoDiStudioDTO[].class))
            )
    })
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getCorsiDiStudio(){
        return Response
                .ok()
                .entity(this.corsoDiStudioService
                        .getCorsiDiStudio()
                        .stream()
                        .map(corsoDiStudioMapper::fromCorsoDiStudioToCorsoDiStudioDto)
                        .collect(Collectors.toList())
                )
                .build();
    }

    @Operation(
            description = "ottengo tutti i corsi di studio programmati",
            tags = { "Corsi Di Studio" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "corsi ottenuti correttamente",
                    content = @Content(schema = @Schema(implementation = CorsoDiStudioDTO[].class))
            )
    })
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/programmati")
    public Response getCorsiDiStudioProgrammati(){
        return Response
                .ok()
                .entity(this.corsoDiStudioService
                        .getCorsiDiStudioProgrammati()
                        .stream()
                        .map(corsoDiStudioMapper::fromCorsoDiStudioToCorsoDiStudioDto)
                        .collect(Collectors.toList())
                )
                .build();
    }


    @Operation(
            description = "ottengo tutti i corsi di studio attivi",
            tags = { "Corsi Di Studio" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "corsi ottenuti correttamente",
                    content = @Content(schema = @Schema(implementation = CorsoDiStudioDTO[].class))
            )
    })
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/attivi")
    public Response getCorsiDiStudioAttivi(){
        return Response
                .ok()
                .entity(this.corsoDiStudioService
                        .getCorsiDiStudioAttiviti()
                        .stream()
                        .map(corsoDiStudioMapper::fromCorsoDiStudioToCorsoDiStudioDto)
                        .collect(Collectors.toList())
                )
                .build();
    }

    @Operation(
            description = "ottengo tutti i corsi di studio del dipartimento inserito",
            tags = { "Corsi Di Studio" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "corsi ottenuti correttamente",
                    content = @Content(schema = @Schema(implementation = CorsoDiStudioDTO[].class))
            )
    })
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/{dipartimento}")
    public Response getCorsiDiStudioDipartimento(@Parameter(required = true, description = "denominazione del dipartimento")
                                                     @PathParam("dipartimento") String dipartimento){
        return Response
                .ok()
                .entity(this.corsoDiStudioService
                        .getCorsiDiStudio(dipartimento)
                        .stream()
                        .map(corsoDiStudioMapper::fromCorsoDiStudioToCorsoDiStudioDto)
                        .collect(Collectors.toList())
                )
                .build();
    }

    @Operation(
            description = "ottengo tutti i corsi di studio programmati del dipartimento selezionato",
            tags = { "Corsi Di Studio" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "corsi ottenuti correttamente",
                    content = @Content(schema = @Schema(implementation = CorsoDiStudioDTO[].class))
            )
    })
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/{dipartimento}/programmati")
    public Response getCorsiDiStudioProgrammatiDipartimentoProgrammati(@Parameter(required = true,description = "denominazione del dipartimento")
                                                                           @PathParam("dipartimento") String dipartimento){
        return Response
                .ok()
                .entity(this.corsoDiStudioService
                        .getCorsiDiStudioProgrammati(dipartimento)
                        .stream()
                        .map(corsoDiStudioMapper::fromCorsoDiStudioToCorsoDiStudioDto)
                        .collect(Collectors.toList())
                )
                .build();
    }

    @Operation(
            description = "ottengo tutti i corsi di studio attivi del dipartimento selezionato",
            tags = { "Corsi Di Studio" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "corsi ottenuti correttamente",
                    content = @Content(schema = @Schema(implementation = CorsoDiStudioDTO[].class))
            )
    })
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/{dipartimento}/attivi")
    public Response getCorsiDiStudioProgrammatiDipartimentoAttivi(@Parameter(required = true, description = "denominaizone del dipartimento")
                                                                      @PathParam("dipartimento") String dipartimento){
        return Response
                .ok()
                .entity(this.corsoDiStudioService
                        .getCorsiDiStudioAttivi(dipartimento)
                        .stream()
                        .map(corsoDiStudioMapper::fromCorsoDiStudioToCorsoDiStudioDto)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
