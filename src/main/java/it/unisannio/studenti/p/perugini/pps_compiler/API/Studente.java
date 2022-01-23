package it.unisannio.studenti.p.perugini.pps_compiler.API;

import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import lombok.*;


@ToString
public class Studente {
    @Getter
    @Setter
    @NonNull
    private String nome;
    @Getter @Setter @NonNull
    private String cognome;
    @Getter @Setter @NonNull @EqualsAndHashCode.Include
    private Email email;
    @Getter @Setter @NonNull
    private Role role;
    @Setter @Getter
    private CorsoDiStudio corsoDiStudio;
    @Getter @Setter @NonNull
    private String matricola;
}
