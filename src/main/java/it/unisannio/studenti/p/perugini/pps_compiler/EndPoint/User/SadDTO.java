package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import lombok.Data;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SadDTO {
    @NotNull @NotBlank
    private String nome;
    @NotNull @NotBlank
    private String cognome;
    @NotNull @NotBlank @Email
    private String email;
}
