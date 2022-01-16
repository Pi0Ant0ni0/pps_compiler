package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Document(collection = "utenti_non_verificati")
public class StudentDTO {
    @NotNull @NotBlank
    private String nome;
    @NotNull @NotBlank
    private String cognome;
    @NotNull @NotBlank
    private String matricola;
    @NotNull @NotBlank @Email @Id
    private String email;
}
