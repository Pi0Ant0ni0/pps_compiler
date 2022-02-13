package it.unisannio.studenti.p.perugini.pps_compiler.Utils;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import it.unisannio.studenti.p.perugini.pps_compiler.Esse3API.ADContestualizzata;
import it.unisannio.studenti.p.perugini.pps_compiler.Esse3API.SEGContestualizzato;
import it.unisannio.studenti.p.perugini.pps_compiler.core.corsoDiStudio.port.ReadCorsoDiStudioPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttivitaDidatticheUtil {
    @Autowired
    private ReadCorsoDiStudioPort readCorsoDiStudioPort;


    public AttivitaDidattica makeUnitaDidattica(SEGContestualizzato segContestualizzato, ADContestualizzata adContestualizzata, boolean programmato, String contenuti, String metodiDidattici, String modalitaVerificaApprendimento, String obiettivi, String prerequisiti) {
        //creo l'insegnamento
        AttivitaDidattica attivitaDidattica = new AttivitaDidattica();
        attivitaDidattica.setDenominazioneAttivitaDidattica(segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getUdDes());
        attivitaDidattica.setCodiceAttivitaDidattica(segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getUdCod());
        attivitaDidattica.setCorsoDiStudio(this.readCorsoDiStudioPort.findCorsoDiStudioById(segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getChiaveAdContestualizzata().getCdsCod()).get());
        attivitaDidattica.setCfu(segContestualizzato.getPeso());
        if (adContestualizzata.getNonErogabileOdFlg()==0) {
            attivitaDidattica.setNonErogabile(false);
        }else{
            attivitaDidattica.setNonErogabile(true);
        }
        attivitaDidattica.setSettoreScientificoDisciplinare(segContestualizzato.getSettCod());
        attivitaDidattica.setProgrammato(programmato);
        attivitaDidattica.setContenuti(contenuti);
        attivitaDidattica.setMetodiDidattici(metodiDidattici);
        attivitaDidattica.setModalitaVerificaApprendimento(modalitaVerificaApprendimento);
        attivitaDidattica.setObiettivi(obiettivi);
        attivitaDidattica.setPrerequisiti(prerequisiti);

        if(adContestualizzata.getChiaveAdContestualizzata().getAdCod().equals("86307")){
            attivitaDidattica.setCodiceAttivitaDidattica("86307");
            attivitaDidattica.setCfu(3);
        }
        attivitaDidattica.setUnitaDidattiche(null);
        return attivitaDidattica;
    }


    public AttivitaDidattica makeAttivitaDidattica(List<AttivitaDidattica> unitaDidattiche, ADContestualizzata adContestualizzata,int cfu,String settore,  boolean programmato, String contenuti, String metodiDidattici, String modalitaVerificaApprendimento, String obiettivi, String prerequisiti) {
        //creo l'insegnamento
        AttivitaDidattica attivitaDidattica = new AttivitaDidattica();
        attivitaDidattica.setDenominazioneAttivitaDidattica(adContestualizzata.getChiaveAdContestualizzata().getAdDes());
        attivitaDidattica.setCodiceAttivitaDidattica(adContestualizzata.getChiaveAdContestualizzata().getAdCod());
        attivitaDidattica.setCorsoDiStudio(this.readCorsoDiStudioPort.findCorsoDiStudioById(adContestualizzata.getChiaveAdContestualizzata().getCdsCod()).get());
        attivitaDidattica.setCfu(cfu);
        if (adContestualizzata.getNonErogabileOdFlg()==0) {
            attivitaDidattica.setNonErogabile(false);
        }else{
            attivitaDidattica.setNonErogabile(true);
        }
        attivitaDidattica.setSettoreScientificoDisciplinare(settore);
        attivitaDidattica.setProgrammato(programmato);
        attivitaDidattica.setContenuti(contenuti);
        attivitaDidattica.setMetodiDidattici(metodiDidattici);
        attivitaDidattica.setModalitaVerificaApprendimento(modalitaVerificaApprendimento);
        attivitaDidattica.setObiettivi(obiettivi);
        attivitaDidattica.setPrerequisiti(prerequisiti);
        attivitaDidattica.setUnitaDidattiche(unitaDidattiche);
        return attivitaDidattica;
    }

}
