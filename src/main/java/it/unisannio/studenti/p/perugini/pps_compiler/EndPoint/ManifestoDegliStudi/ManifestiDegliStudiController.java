package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi;


import it.unisannio.studenti.p.perugini.pps_compiler.API.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.SADService;
import it.unisannio.studenti.p.perugini.pps_compiler.Components.ManifestoDegliStudiMaker;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed(value = {ADMINstr, SADstr})
    public Response aggiungiManifesto(@RequestBody @Valid ManifestoDegliStudiDTO regola) {
        try {
            logger.info("è arrivata una nuova regola: "+regola);
            this.aggiungiManfiestoUseCase.addManifesto(ManifestiDegliStudiMapper.fromRegolaDTOToRegola(regola));
            return Response.status(Response.Status.OK)
                    .entity("Regola aggiunta correttamente")
                    .build();
        } catch (OrdinamentoNotFoundException | RegolaNonValidaException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{codiceCorsoDiStudio}")
    @PermitAll
    public Response getManifestiPreviw(@PathParam("codiceCorsoDiStudio")String codiceCorsoDiSudio){
        return Response
                .ok()
                .entity(this.visualizzaManifestoUseCase.getManifesti(codiceCorsoDiSudio)
                        .stream()
                        .map(ManifestiDegliStudiMapper::fromManifestoToPreview)
                        .collect(Collectors.toList())
                ).build();
    }

    @GET
    @Produces(org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    @PermitAll()
    @Path("/{codiceCorsoDiStudio}/{coorte}/")
    public StreamingOutput getManifesto(@PathParam("coorte")int anno,
                                        @PathParam("codiceCorsoDiStudio")String codiceCorsoDiStudio,
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


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {ADMINstr, SADstr})
    @Path("/{codiceCorsoDiStudio}/{coorte}/orientamenti/")
    @PermitAll
    public Response getOrientamenti(@PathParam("codiceCorsoDiStudio") String codiceCorsoDiStudio,
                                    @PathParam("coorte")int coorte) {

        try {
            ManifestoDegliStudi manifestoDegliStudi = this.sadService.getRegolaByID(coorte,codiceCorsoDiStudio);
            Map<Integer, AnnoAccademico> schemiDiPiano = manifestoDegliStudi.getAnniAccademici();
            List<Orientamento>orientamenti = new ArrayList<>();
            for (Integer anno: schemiDiPiano.keySet())
                if(schemiDiPiano.get(anno).getOrientamenti().isPresent())
                    orientamenti.addAll(schemiDiPiano.get(anno).getOrientamenti().get());
            return Response.ok()
                    .entity(orientamenti)
                    .build();
        } catch (RegolaNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }




}
