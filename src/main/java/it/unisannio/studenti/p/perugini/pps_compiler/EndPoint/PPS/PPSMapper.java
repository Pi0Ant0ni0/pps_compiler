package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS;

import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.User.UserMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.persistance.Repositories.User;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PPSMapper {

    @Autowired
    private AttivitaDidatticheMapper attivitaDidatticheMapper;
    @Autowired
    private UserMapper userMapper;

    public PPS fromPPSDTOToPPS(PPSAggiuntaDTO ppsAggiuntaDTO, User user) {
        PPS pps = new PPS();
        pps.setInsegnamentiASceltaLibera(ppsAggiuntaDTO.getAttivitaDidatticheAScelta()
                .stream()
                .map(attivitaDidatticheMapper::toAttivitaDidattica)
                .collect(Collectors.toList())
        );
        if(ppsAggiuntaDTO.getOrientamento().isPresent() && ppsAggiuntaDTO.getOrientamento().get().size()!=0){
           pps.setOrientamento(ppsAggiuntaDTO.getOrientamento().get()
                   .stream()
                   .map(attivitaDidatticheMapper::fromInsegnamentoRegolaToAttivitaDidattica)
                   .collect(Collectors.toList())
           );
        }else pps.setOrientamento(null);

        pps.setDataCompilazione(LocalDate.now());
        pps.setApprovato(false);
        pps.setRifiutato(false);
        pps.setStudente(userMapper.fromUserToStudente(user));
        if(ppsAggiuntaDTO.getCurriculum()!= null && ppsAggiuntaDTO.getCurriculum().length()!=0)
            pps.setCurriculum(ppsAggiuntaDTO.getCurriculum());
        else pps.setCurriculum(null);
        return pps;
    }

    public PPSPreviewDTO fromPPSToPPSPreviewDTO(PPS pps) {
        PPSPreviewDTO dto = new PPSPreviewDTO();
        dto.setEmail(pps.getStudente().getEmail().getEmail());
        dto.setDataCompilazione(pps.getDataCompilazione());
        dto.setApprovato(pps.isApprovato());
        dto.setRifiutato(pps.isRifiutato());
        dto.setNome(pps.getStudente().getNome());
        dto.setCognome(pps.getStudente().getCognome());
        dto.setLiberi(pps.getInsegnamentiASceltaLibera()
                .stream()
                .map(attivitaDidatticheMapper::toAttivitaDidatticaDettagliata)
                .collect(Collectors.toList())
        );
        if(pps.getOrientamento().isPresent()){
           dto.setOrientamento(pps.getOrientamento().get()
                   .stream()
                   .map(attivitaDidatticheMapper::toAttivitaDidatticaDettagliata)
                   .collect(Collectors.toList())
           );
        }else{
            dto.setOrientamento(new ArrayList<>());
        }
        if(pps.getCurriculum().isPresent())
            dto.setCurriculum(pps.getCurriculum().get());
        else dto.setCurriculum("");
        dto.setCodiceCorsoDiStudio(pps.getStudente().getCorsoDiStudio().getCodice());
        return dto;
    }
}
