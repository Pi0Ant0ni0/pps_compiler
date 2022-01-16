package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import lombok.Data;

@Data
public class UserAuthenticatedDTO {
    private String nome;
    private String cognome;
    private String ruolo;
    private String email;
}
