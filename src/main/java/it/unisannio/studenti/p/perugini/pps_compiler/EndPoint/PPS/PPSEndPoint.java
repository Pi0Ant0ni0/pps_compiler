package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS;


import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.*;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.DocenteService;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.StudentiService;
import it.unisannio.studenti.p.perugini.pps_compiler.Utils.PPSMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;


import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.DOCENTEStr;
import static it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role.STUDENTEstr;


@Path("/pps")
public class PPSEndPoint {


    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private StudentiService studentiService;
    @Autowired
    private DocenteService docenteService;
    @Autowired
    private PPSMapper ppsMapper;

    private Logger logger = LoggerFactory.getLogger(PPSEndPoint.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(STUDENTEstr)
    public Response addPPS(@RequestBody PPSAggiuntaDTO dto, @Context SecurityContext securityContext){

        try {
            String email = securityContext.getUserPrincipal().getName();
            User user = this.authorizationService.getUserByEmail(new Email(email));
            this.studentiService.addModulePPS(this.ppsMapper.fromPPSDTOToPPS(dto,user), dto.getCoorte());
            return Response.ok().entity("PPs Aggiunto correttamente").build();

        } catch (UserNotFound userNotFound) {
            return Response.serverError().build();
        } catch (EmailNonCorrettaException e) {
            return Response.serverError().build();
        } catch (TipoCorsoDiLaureaNonSupportatoException | InsegnamentoNotFoundException | RegolaNotFoundException | PPSNonValidoException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed(DOCENTEStr)
    @Path("/visionati")
    public List<PPSPreviewDTO> getAllPPSVisionatiPreview(@Context SecurityContext securityContext){
        try {
            String email = securityContext.getUserPrincipal().getName();
            logger.info("ecco l'utente che ha richiesto i pps visionati: "+email);
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
        try {
            String email = securityContext.getUserPrincipal().getName();
            logger.info("ecco l'utente che ha richiesto i pps: "+email);
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
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({STUDENTEstr, DOCENTEStr})
    @Path("/{email}/preview")
    public Response getPPSPreview(@PathParam("email") String email, @Context SecurityContext securityContext){

        try {
            logger.info("Arrivata richiesta del pps di: "+email);
            String emailRichiedente = securityContext.getUserPrincipal().getName();
            PPS pps = this.docenteService.getPPSByEmail(new Email(email), new Email(emailRichiedente));
            return Response.ok()
                    .entity(this.ppsMapper.fromPPSToPPSPreviewDTO(pps))
                    .build();
        } catch (EmailNonCorrettaException |UserNotFound | PPSNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }catch(RichiestaNonValidaException e){
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({DOCENTEStr})
    @Path("/{email}")
    public Response getPPS(@PathParam("email") String email, @Context SecurityContext securityContext){

        try {
            logger.info("Arrivata richiesta del pps di: "+email);
            String emailRichiedente = securityContext.getUserPrincipal().getName();
            PPS pps = this.docenteService.getPPSByEmail(new Email(email), new Email(emailRichiedente));
            return Response.ok()
                    .entity(this.ppsMapper.fromPPSToPPSPreviewDTO(pps))
                    .build();
        } catch (EmailNonCorrettaException |UserNotFound | PPSNotFoundException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }catch(RichiestaNonValidaException e){
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        }

    }


    @GET
    @Produces(org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    @RolesAllowed({STUDENTEstr, DOCENTEStr})
    @Path("/{email}/pdf")
    public StreamingOutput getPPSPdf(@PathParam("email") String email, @Context SecurityContext securityContext){
        try {
            logger.info("Arrivata richiesta del pps in formato pdf da parte di: "+securityContext.getUserPrincipal().getName());
            String emailRichiedente = securityContext.getUserPrincipal().getName();
            PPS pps = this.docenteService.getPPSByEmail(new Email(email), new Email(emailRichiedente));
            return outputStream -> {
                PPSMaker.makePPS(pps, outputStream);
            };
        } catch (EmailNonCorrettaException |UserNotFound | PPSNotFoundException e) {
            throw new  WebApplicationException(Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build());
        }catch (RichiestaNonValidaException e){
            throw new  WebApplicationException(Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(e.getMessage())
                    .build());
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
