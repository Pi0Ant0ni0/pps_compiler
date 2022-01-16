package it.unisannio.studenti.p.perugini.pps_compiler.Utils;

import it.unisannio.studenti.p.perugini.pps_compiler.API.AttivitaDidattica;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import it.unisannio.studenti.p.perugini.pps_compiler.Esse3API.ADContestualizzata;
import it.unisannio.studenti.p.perugini.pps_compiler.Esse3API.SEGContestualizzato;

public class InsegnamentoUtil {


    public static AttivitaDidattica makeInsegnamento(SEGContestualizzato segContestualizzato, ADContestualizzata adContestualizzata, TipoCorsoDiLaurea l2, boolean programmato, String contenuti, String metodiDidattici, String modalitaVerificaApprendimento, String obiettivi, String prerequisiti) {
        //creo l'insegnamento
        AttivitaDidattica attivitaDidattica = new AttivitaDidattica();
        attivitaDidattica.setDenominazioneAttivitaDidattica(segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getUdDes());
        attivitaDidattica.setCodiceAttivitaDidattica(segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getUdCod());
        attivitaDidattica.setCodiceCorsoDiStudio(segContestualizzato.getChiaveSegContestualizzato().getChiaveUdContestualizzata().getChiaveAdContestualizzata().getCdsCod());
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
        return attivitaDidattica;
    }
}
