package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.Optional;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Document(collection = "users")
public class User  implements Principal {
    @Getter @Setter @NonNull @NotBlank
    private String nome;
    @Getter @Setter @NonNull @NotBlank
    private String cognome;
    @Id @Getter @Setter @NonNull @EqualsAndHashCode.Include
    private Email email;
    @Getter @Setter @NonNull
    private Role role;
    @Setter
    private CorsoDiStudio corsoDiStudio;
    @Setter
    private String matricola;

    public Optional<CorsoDiStudio> getCorsoDiStudio() {
        return Optional.ofNullable(corsoDiStudio);
    }

    public Optional<String> getMatricola() {
        return Optional.ofNullable(matricola);
    }

    @Override
    public String getName() {
        return email.getEmail();
    }
}
