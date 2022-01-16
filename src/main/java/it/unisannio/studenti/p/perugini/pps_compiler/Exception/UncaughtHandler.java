package it.unisannio.studenti.p.perugini.pps_compiler.Exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.glassfish.jersey.spi.ExceptionMappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UncaughtHandler implements ExceptionMapper<JsonMappingException> {
    @Override
    public Response toResponse(JsonMappingException uncaughtHandler) {
        return Response.status(Response.Status.BAD_REQUEST).entity("I parametri passati non sono validi!").build();
    }
}
