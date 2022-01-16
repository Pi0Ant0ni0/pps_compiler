package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import lombok.Data;

@Data
public class GenericUserDTO {

    private String nome;
    private String cognome;
    private String email;
    private String role;
    private String codiceCorsoDiStudio;
    private String matricola;
}
