package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS;


import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.DocenteService;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.PPSMaker;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.usecase.CompilaPPSUseCase;
import it.unisannio.studenti.p.perugini.pps_compiler.core.pps.usecase.VisualizzaStatoPPSUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;


import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.DOCENTEStr;
import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.STUDENTEstr;


@Path("/pps")
public class PPSController {


    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private DocenteService docenteService;
    @Autowired
    private PPSMapper ppsMapper;
    @Autowired
    private CompilaPPSUseCase compilaPPSUseCase;
    @Autowired
    private VisualizzaStatoPPSUseCase visualizzaStatoPPSUseCase;

    private Logger logger = LoggerFactory.getLogger(PPSController.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(STUDENTEstr)
    public Response compilaPPS(@RequestBody PPSAggiuntaDTO dto, @Context SecurityContext securityContext){
        logger.info("E' stato ricevuto un modulo pps ");
        try {
            Email email = new Email(securityContext.getUserPrincipal().getName());
            this.compilaPPSUseCase.compila(dto,email);
            logger.info("Modulo PPS corretto. Inserimento Completato.");
            return Response.ok().entity("PPs Aggiunto correttamente").build();
        } catch (EmailNonCorrettaException e) {
            return Response.serverError().build();
        } catch (InsegnamentoNotFoundException | RegolaNotFoundException | PPSNonValidoException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({STUDENTEstr, DOCENTEStr})
    @Path("/{email}")
    public Response getPPS(@PathParam("email") String email, @Context SecurityContext securityContext){

        try {
            logger.info("Arrivata richiesta del pps di: "+email);
            logger.info("La richiesta arriva da: "+email);
            String emailRichiedente = securityContext.getUserPrincipal().getName();
            Optional<PPS> ppsOptional = this.visualizzaStatoPPSUseCase.getPPS(new Email(email), new Email(emailRichiedente));
            if(ppsOptional.isPresent()) {
                return Response.ok()
                        .entity(this.ppsMapper.fromPPSToPPSPreviewDTO(ppsOptional.get()))
                        .build();
            }else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("L'e-mail non è associata a nessun modulo pps")
                        .build();
            }
        } catch (EmailNonCorrettaException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }catch(RichiestaNonValidaException e){
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }

    }


    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(DOCENTEStr)
    @Path("/visionati")
    public List<PPSPreviewDTO> getAllPPSVisionatiPreview(@Context SecurityContext securityContext){
        logger.info("Sono stati richiesti i pps visionati.");
        try {
            String email = securityContext.getUserPrincipal().getName();
            logger.info("La richiesta è stata effettuata da "+email);
            User user = this.authorizationService.getUserByEmail(new Email(email));
            return this.docenteService
                    .getAllPPSVisionati(user)
                    .stream()
                    .map(ppsMapper::fromPPSToPPSPreviewDTO)
                    .collect(Collectors.toList());
        } catch (UserNotFound | EmailNonCorrettaException e) {
            return new ArrayList<>();
        }

    }


    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(DOCENTEStr)
    @Path("/inSospeso")
    public List<PPSPreviewDTO> getAllPPSInSospesoPreview(@Context SecurityContext securityContext){
        logger.info("Sono stati richiesti i pps in sospeso");
        try {
            String email = securityContext.getUserPrincipal().getName();
            logger.info("La richiesta è stata effettuata da "+email);
            User user = this.authorizationService.getUserByEmail(new Email(email));
            return this.docenteService
                    .getAllPPSNonGestitiFilterdOnCorsoDiStudio(user)
                    .stream()
                    .map(ppsMapper::fromPPSToPPSPreviewDTO)
                    .collect(Collectors.toList());
        } catch (UserNotFound | EmailNonCorrettaException e) {
            return new ArrayList<>();
        }

    }



    @GET
    @Produces(org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    @RolesAllowed({STUDENTEstr, DOCENTEStr})
    @Path("/{email}/pdf")
    public StreamingOutput getPPSPdf(@PathParam("email") String email, @Context SecurityContext securityContext){
        logger.info("E' stato richiesto il  PPS di: "+email+" in formato PDF");
        try {
            logger.info("La richiesta è stata effettuata da: "+securityContext.getUserPrincipal().getName());
            String emailRichiedente = securityContext.getUserPrincipal().getName();
            Optional<PPS> optionalPPS = this.visualizzaStatoPPSUseCase.getPPS(new Email(emailRichiedente),new Email(email));
            if(optionalPPS.isPresent()) {
                return outputStream -> {
                    PPSMaker.makePPS(optionalPPS.get(), outputStream);
                };
            }else {
                return null;
            }
        } catch (EmailNonCorrettaException  | RichiestaNonValidaException e) {
            return null;
        }

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(DOCENTEStr)
    @Path("/{email}/approva")
    public Response approvaPPS(@PathParam("email")String email){
        try {
            User user = this.authorizationService.getUserByEmail(new Email(email));
            this.docenteService.accettaPPS(user);
            return Response.ok().entity("PPs accettato correttamente").build();
        } catch (UserNotFound |EmailNonCorrettaException | RichiestaNonValidaException | PPSNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(DOCENTEStr)
    @Path("/{email}/rifiuta")
    public Response rifiutaPPS(@PathParam("email")String email){

        try {
            User user = this.authorizationService.getUserByEmail(new Email(email));
            this.docenteService.rifiutaPPS(user);
            return Response.ok().entity("PPs rifiutato correttamente").build();
        } catch (UserNotFound |EmailNonCorrettaException | RichiestaNonValidaException | PPSNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }






}
