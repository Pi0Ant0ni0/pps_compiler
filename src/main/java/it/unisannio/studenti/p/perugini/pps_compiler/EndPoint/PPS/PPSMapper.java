package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.PPS;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.API.PPS;
import it.unisannio.studenti.p.perugini.pps_compiler.API.User;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticheMapper;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaDiOrientamentoDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.AttivitaDidatticaPPSDTO;
import it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.AttivitaDidattiche.InsegnamentoRegola;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.InsegnamentoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class PPSMapper {

    @Autowired
    AttivitaDidatticheMapper attivitaDidatticheMapper;

    public PPS fromPPSDTOToPPS(PPSAggiuntaDTO ppsAggiuntaDTO, User user) throws InsegnamentoNotFoundException {
        PPS pps = new PPS();
        pps.setInsegnamentiASceltaLibera(ppsAggiuntaDTO.getAttivitaDidatticheAScelta());
        List<AttivitaDidatticaDiOrientamentoDTO> orientamento = new ArrayList<>();
        if(ppsAggiuntaDTO.getOrientamento().isPresent() && ppsAggiuntaDTO.getOrientamento().get().size()!=0){
            for(InsegnamentoRegola insegnamento: ppsAggiuntaDTO.getOrientamento().get()){
                orientamento.add(this.attivitaDidatticheMapper.fromInsegnamentoRegolaToInsegnamentoOrientamento(insegnamento));
            }
            pps.setOrientamento(orientamento);
        }else pps.setOrientamento(null);

        pps.setDataCompilazione(LocalDate.now());
        pps.setApprovato(false);
        pps.setRifiutato(false);
        pps.setUser(user);
        if(ppsAggiuntaDTO.getCurriculum()!= null && ppsAggiuntaDTO.getCurriculum().length()!=0)
            pps.setCurriculum(ppsAggiuntaDTO.getCurriculum());
        else pps.setCurriculum(null);
        return pps;
    }

    public PPSPreviewDTO fromPPSToPPSPreviewDTO(PPS pps) {
        PPSPreviewDTO dto = new PPSPreviewDTO();
        dto.setEmail(pps.getUser().getEmail().getEmail());
        dto.setDataCompilazione(pps.getDataCompilazione());
        dto.setApprovato(pps.isApprovato());
        dto.setRifiutato(pps.isRifiutato());
        dto.setNome(pps.getUser().getNome());
        dto.setCognome(pps.getUser().getCognome());
        List<AttivitaDidattica>buffer = new ArrayList<>();
        for(AttivitaDidatticaPPSDTO attivitaDidatticaPPSDTO : pps.getInsegnamentiASceltaLibera())
            buffer.add(this.attivitaDidatticheMapper.fromInsegnamentoPPSDTOToInsegnamento(attivitaDidatticaPPSDTO));

        dto.setLiberi(buffer);
        if(pps.getOrientamento().isPresent()){
            buffer= new ArrayList<>();
            for(AttivitaDidatticaDiOrientamentoDTO attivitaDidatticaDiOrientamentoDTO : pps.getOrientamento().get())
                buffer.add(this.attivitaDidatticheMapper.fromInsegnamentoOrientamentoToInsegnamento(attivitaDidatticaDiOrientamentoDTO));
            dto.setOrientamento(buffer);
        }else{
            dto.setOrientamento(new ArrayList<>());
        }
        if(pps.getCurriculum().isPresent())
            dto.setCurriculum(pps.getCurriculum().get());
        else dto.setCurriculum("");
        return dto;
    }
}
