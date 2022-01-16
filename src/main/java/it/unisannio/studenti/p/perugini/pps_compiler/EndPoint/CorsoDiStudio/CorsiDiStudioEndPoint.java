package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio;

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
public class CorsiDiStudioEndPoint {
    @Autowired
    private CorsoDiStudioService corsoDiStudioService;
    @Autowired
    private CorsoDiStudioMapper corsoDiStudioMapper;


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

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/{dipartimento}")
    public Response getCorsiDiStudioIngegneria(@PathParam("dipartimento") String dipartimento){
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

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("/programmati/{dipartimento}")
    public Response getCorsiDiStudioProgrammatiIngegneria(@PathParam("dipartimento") String dipartimento){
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
}
