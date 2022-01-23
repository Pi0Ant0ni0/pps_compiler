package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.Studente;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import it.unisannio.studenti.p.perugini.pps_compiler.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio.CorsoDiStudioMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailNonCorrettaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    @Autowired
    private CorsoDiStudioMapper corsoDiStudioMapper;


    public Studente fromUserToStudente(User user){
        Studente studente = new Studente();
        studente.setNome(user.getNome());
        studente.setCognome(user.getCognome());
        studente.setMatricola(user.getMatricola().get());
        studente.setCorsoDiStudio(user.getCorsoDiStudio().get().get(0));
        studente.setRole(Role.STUDENTE);
        studente.setEmail(user.getEmail());
        return studente;
    }

    public User fromStudentDTOToUser(StudentDTO dto, CorsoDiStudio corsoDiStudio){
        User user = new User();
        user.setNome(dto.getNome());
        user.setCognome(dto.getCognome());
        try {
            user.setEmail(new Email(dto.getEmail()));
        } catch (EmailNonCorrettaException e) {
            user.setEmail(null);
        }
        user.setRole(Role.STUDENTE);
        List<CorsoDiStudio>corsi = new LinkedList<>();
        corsi.add(corsoDiStudio);
        user.setCorsoDiStudio(corsi);
        user.setMatricola(dto.getMatricola());
        return user;
    }

    public UserAuthenticatedDTO fromUserToUserAuthenticatedDTO(User user){
        UserAuthenticatedDTO dto = new UserAuthenticatedDTO();
        dto.setCognome(user.getCognome());
        dto.setNome(user.getNome());
        dto.setRuolo(user.getRole().toString());
        dto.setEmail(user.getEmail().getEmail());
        return dto;
    }

    public User fromSadDTOToUser(SadDTO sadDTO) throws EmailNonCorrettaException {
        User user = new User();
        user.setNome(sadDTO.getNome());
        user.setCognome(sadDTO.getCognome());
        user.setEmail(new Email(sadDTO.getEmail()));
        user.setRole(Role.SAD);
        user.setCorsoDiStudio(null);
        return user;
    }

    public User fromDocenteDtoToUser(DocenteDTO docenteDTO) throws EmailNonCorrettaException, CorsoDiStudioNotFoundException {
        User user = new User();
        user.setNome(docenteDTO.getNome());
        user.setCognome(docenteDTO.getCognome());
        user.setEmail(new Email(docenteDTO.getEmail()));
        user.setRole(Role.DOCENTE);
        user.setCorsoDiStudio(
                docenteDTO.getCorsoDiStudio()
                        .stream()
                .filter(Objects::nonNull)
                .map(corsoDiStudioMapper::fromCorsoDiStudioDTOToCorsoDiStudio)
                .filter(corsoDiStudio -> corsoDiStudio.isPresent())
                .map(corsoDiStudio -> corsoDiStudio.get())
                .collect(Collectors.toList())
        );
        return user;
    }

    public  GenericUserDTO fromUserToGenericUserDTO(User user){
        GenericUserDTO dto = new GenericUserDTO();
        dto.setNome(user.getNome());
        dto.setCognome(user.getCognome());
        dto.setRole(user.getRole().toString());
        dto.setEmail(user.getEmail().getEmail());
        switch (user.getRole()){
            case STUDENTE:
                dto.setMatricola(user.getMatricola().get());
                dto.setCodiceCorsoDiStudio(user.getCorsoDiStudio().get().get(0).getCodice());
                break;
            case DOCENTE:
                String corsi="";
                for(CorsoDiStudio corsoDiStudio: user.getCorsoDiStudio().get())
                    corsi=corsi+corsoDiStudio.getCodice()+" ";
                dto.setCodiceCorsoDiStudio(corsi);
                dto.setMatricola("");
                break;
            case SAD:
            case ADMIN:
                dto.setMatricola("");
                dto.setCodiceCorsoDiStudio("");
        }
        return dto;
    }
}
