package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Email;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.Role;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.CorsoDiStudio.CorsoDiStudioMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.CorsoDiStudioNotFoundException;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailNonCorrettaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private CorsoDiStudioMapper corsoDiStudioMapper;

    public User fromStudentDTOToUser(StudentDTO dto, CorsoDiStudio corsoDiStudio){
        User user = new User();
        user.setNome(dto.getNome());
        user.setCognome(dto.getCognome());
        try {
            user.setEmail(new Email(dto.getEmail()));
        } catch (EmailNonCorrettaException e) {
            //non puo mai verificarsi
            //è una conversione che si effettua da database a service
            // quindi i dati sono consistenti
        }
        user.setRole(Role.STUDENTE);
        user.setCorsoDiStudio(corsoDiStudio);
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
        return user;
    }

    public User fromDocenteDtoToUser(DocenteDTO docenteDTO) throws EmailNonCorrettaException, CorsoDiStudioNotFoundException {
        User user = new User();
        user.setNome(docenteDTO.getNome());
        user.setCognome(docenteDTO.getCognome());
        user.setEmail(new Email(docenteDTO.getEmail()));
        user.setRole(Role.DOCENTE);
        user.setCorsoDiStudio(corsoDiStudioMapper.fromCorsoDiStudioDTOToCorsoDiStudio(docenteDTO.getCorsoDiStudio()));
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
                dto.setCodiceCorsoDiStudio(user.getCorsoDiStudio().get().getCodice());
                break;
            case DOCENTE:
                dto.setCodiceCorsoDiStudio(user.getCorsoDiStudio().get().getCodice());
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
