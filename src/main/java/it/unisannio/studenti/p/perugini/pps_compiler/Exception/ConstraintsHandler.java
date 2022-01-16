package it.unisannio.studenti.p.perugini.pps_compiler.Exception;

import org.glassfish.jersey.spi.ExceptionMappers;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintsHandler implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException constraintViolation) {
        String error = "";
        for(ConstraintViolation violation: constraintViolation.getConstraintViolations()) {
            error+=violation.getMessage()+", \n";
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
}
