package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.Ordinamento;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Ordinamento;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio.CorsoDiStudioDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.OrdinamentoNonCorrettoException;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.usecase.AggiungiOrdinamentoUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.ordinamento.usecase.OrdinamentoCorrenteUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Optional;

import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.ADMINstr;
import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.SADstr;


@Path("/ordinamenti")
public class OrdinamentiController {

    private Logger logger = LoggerFactory.getLogger(OrdinamentiController.class);
    @Autowired
    private AggiungiOrdinamentoUseCase aggiungiOrdinamentoUseCase;
    @Autowired
    private OrdinamentoCorrenteUseCase ordinamentoCorrenteUseCase;

    @Operation(
            description = "inserisce un nuovo ordinamento nel database",
            tags = { "Ordinamenti" },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ordinamento aggiunto correttamente",
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
    public Response aggiungiOrdinamento(@Parameter(required = true, schema = @Schema(implementation = OrdinamentoDTO.class))
                                            @RequestBody @Valid OrdinamentoDTO ordinamento){
        logger.info("è stata richiesta l'aggiunta di un nuovo ordinamento");
        logger.info("ordinamento: "+ordinamento.getAnnoDiRedazione());
        try {
            aggiungiOrdinamentoUseCase.aggiungiOrdinamento(OrdinamentoMapper.fromOrdinamentoDTOToOrdinamento(ordinamento));
            logger.info("ordinamento: "+ordinamento.getAnnoDiRedazione()+" + stato aggiunto correttamente");
            return Response
                    .ok()
                    .entity("Ordinamento aggiunto correttamente")
                    .build();
        } catch ( OrdinamentoNonCorrettoException e) {
            logger.info("ordinamento: "+ordinamento.getAnnoDiRedazione()+" non rispetta le specifiche\n"+e.getMessage());
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }


    @Operation(
            description = "recupera l'anno di redazione dell'ordinamento corrente per il corso di studio specificato",
            tags = { "Ordinamenti" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "text/plain")
            )
    })
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    @Path("{codiceCorsoDiStudio}/ordinamentoCorrente")
    @PermitAll
    public int getAnniOrdinamenti(@Parameter(required = true, description = "il corso di studio per il quale si ricerca l'ordinamento")
                                      @PathParam("codiceCorsoDiStudio")String codice){
        Optional<Ordinamento> ordinamento =  this.ordinamentoCorrenteUseCase.ordinamentoCorrente(codice);
        if(ordinamento.isPresent())
            return ordinamento.get().getChiaveOrdinamento().getAnnoDiRedazione();
        return 0;
    }
}
