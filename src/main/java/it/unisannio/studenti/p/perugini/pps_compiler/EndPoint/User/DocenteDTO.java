package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio.CorsoDiStudioDTO;
import lombok.Data;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DocenteDTO {

    @NotBlank @NotNull
    private String nome;
    @NotNull @NotBlank
    private String cognome;
    @NotNull
    private List<CorsoDiStudioDTO> corsoDiStudio;
    @NotNull @NotBlank @Email
    private String email;
}
