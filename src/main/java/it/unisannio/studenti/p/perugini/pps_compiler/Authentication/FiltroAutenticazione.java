package it.unisannio.studenti.p.perugini.pps_compiler.Authentication;

import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailNonCorrettaException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UserNotFound;
import it.unisannio.studenti.p.perugini.pps_compiler.Services.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.*;

@Provider
public class FiltroAutenticazione implements ContainerRequestFilter
{

    @Context
    private ResourceInfo resourceInfo;
    @Autowired
    private AuthorizationService authorizationService;

    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private static Logger logger = LoggerFactory.getLogger(FiltroAutenticazione.class);

    @Override
    public void filter(ContainerRequestContext requestContext)
    {
        logger.info("attivazione filtro per richiesta in entrata");
        Method method = resourceInfo.getResourceMethod();

        //se non c'è il permit all devo controllare
        if(!method.isAnnotationPresent(PermitAll.class)) {
            logger.info("Il metodo ha un accesso basato su ruolo");

            //Get request headers
            final MultivaluedMap<String, String> headers = requestContext.getHeaders();

            //Fetch authorization header
            final List<String> authorization = headers.get(HttpHeaders.AUTHORIZATION);

            //If no authorization information present; block access
            if(authorization == null || authorization.isEmpty())
            {
                logger.info("Non è stato trovato l'header di authorization");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Bisogna essere loggati per accedere a questa risorsa").build());
                return;
            }

            //se non è lo schema corretto blocco
            if (!authorization.get(0).startsWith(AUTHENTICATION_SCHEME)){
                logger.info("L'header di autorization non è corretto");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Authorization token non corretto, l'autenticazione si basa su JWT").build());
                return;
            }

            //recupero il token
            final String jwtToken = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");


            //provo ad autenticare e ad autorizzare
            try {
                if (this.authorizationService.isJWTExpired(jwtToken)){
                    logger.info("token scaduto");
                    requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                            .entity("Il Token è scaduto").build());
                    return;
                }
                User user = this.authorizationService.GetUserFromJwt(jwtToken);


                //se ho trovato l'utente controllo se ha i ruoli necessari
                if(method.isAnnotationPresent(RolesAllowed.class))
                {
                    logger.info("Il metodo è ristretto solo ad alcuni ruoli");
                    RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                    Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
                    logger.info(" i ruoli permessi sono: "+rolesSet);

                    String userRole = user.getRole().toString();
                   logger.info("ecco il ruolo dell'utente: "+userRole);
                    if(!rolesSet.contains(userRole))
                    {
                        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                .entity("Non hai i privileggi per accedere a questa risorsa").build());
                        return;
                    }

                    //se ha i ruoli necessari lo identifico cosi da rendere disponibile i dati anche ai controller
                    requestContext.setSecurityContext(new SecurityContext() {
                        @Override
                        public Principal getUserPrincipal() {
                            return user;
                        }

                        @Override
                        public boolean isUserInRole(String s) {
                            return s.equals(userRole);
                        }

                        @Override
                        public boolean isSecure() {
                            return false;
                        }

                        @Override
                        public String getAuthenticationScheme() {
                            return AUTHENTICATION_SCHEME;
                        }
                    });
                }

            } catch (EmailNonCorrettaException | UserNotFound e) {
                requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Il Token non è valido").build());
            }

        }

    }

}