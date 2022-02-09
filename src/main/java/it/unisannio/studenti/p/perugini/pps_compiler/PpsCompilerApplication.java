package it.unisannio.studenti.p.perugini.pps_compiler;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.unisannio.studenti.p.perugini.pps_compiler.Authentication.FiltroAutenticazione;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticheController;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio.CorsiDiStudioController;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi.ManifestiDegliStudiController;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.Ordinamento.OrdinamentiController;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS.PPSController;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.LoginController;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.RegistrationController;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserController;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.ConstraintsHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.ws.rs.ApplicationPath;

@OpenAPIDefinition(
		info = @Info(
				title = "PPS Helper",
				version = "0.0",
				description = "API REST esposte dal sistema PPS Helper",
				contact = @Contact(name = "Pio Antonio Perugini", email = "pioantonioperugini@gmail.com")
		),
		tags = {
				@Tag(name = "Attività Didattiche"),
				@Tag(name = "Corsi Di Studio"),
				@Tag(name = "Ordinamenti"),
				@Tag(name = "Manifesti Degli Studi"),
				@Tag(name = "Utenti"),
				@Tag(name = "Moduli Di Presentazione Dei Piani Di Studio"),
				@Tag(name = "Auth")
		}
)
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "bearerAuth", scheme = "bearer", bearerFormat = "jwt")
@SpringBootApplication
@EnableMongoRepositories
@ApplicationPath("/rest")
@EnableAsync
public class PpsCompilerApplication extends ResourceConfig {
	private Logger logger = LoggerFactory.getLogger(PpsCompilerApplication.class);




	public static void main(String[] args) {
		SpringApplication.run(PpsCompilerApplication.class, args);
	}

	public PpsCompilerApplication() {
		logger.info("Registrazione ENDPOINT in corso");
		register(AttivitaDidatticheController.class);
		register(OrdinamentiController.class);
		register(ManifestiDegliStudiController.class);
		register(CorsiDiStudioController.class);
		register(UserController.class);
		register(LoginController.class);
		register(RegistrationController.class);
		register(PPSController.class);
		register(FiltroAutenticazione.class);
		register(ConstraintsHandler.class);
		//swagger
		register(OpenApiResource.class);
	}




}
