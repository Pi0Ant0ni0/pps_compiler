package it.unisannio.studenti.p.perugini.pps_compiler;

import it.unisannio.studenti.p.perugini.pps_compiler.Authentication.FiltroAutenticazione;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.*;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticheEndPoint;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio.CorsiDiStudioEndPoint;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi.ManifestiDegliStudiEndPoint;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.Ordinamento.OrdinamentiEndPoint;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS.PPSEndPoint;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.AuthEndPoint;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.RegistrationController;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.ConstraintsHandler;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.UncaughtHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.ws.rs.ApplicationPath;


@SpringBootApplication
@EnableMongoRepositories
@ApplicationPath("/rest")
public class PpsCompilerApplication extends ResourceConfig {
	private Logger logger = LoggerFactory.getLogger(PpsCompilerApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(PpsCompilerApplication.class, args);
	}

	public PpsCompilerApplication() {
		logger.info("Registrazione ENDPOINT in corso");
		register(AttivitaDidatticheEndPoint.class);
		register(OrdinamentiEndPoint.class);
		register(ManifestiDegliStudiEndPoint.class);
		register(CorsiDiStudioEndPoint.class);
		register(AdminEndPoint.class);
		register(AuthEndPoint.class);
		register(RegistrationController.class);
		register(PPSEndPoint.class);
		register(FiltroAutenticazione.class);
		register(ConstraintsHandler.class);
		register(UncaughtHandler.class);
	}


}
