package it.unisannio.studenti.p.perugini.pps_compiler.Utils;

import it.unisannio.studenti.p.perugini.pps_compiler.API.CorsoDiStudio;
import it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject.TipoCorsoDiLaurea;
import it.unisannio.studenti.p.perugini.pps_compiler.Esse3API.Facolta;
import it.unisannio.studenti.p.perugini.pps_compiler.Esse3API.RegolamentoDiScelta;

public class CorsoDiStudiUtil {
    public static CorsoDiStudio makeCorsoDiStudio(RegolamentoDiScelta regolamentoDiScelta, Facolta facolta, boolean programmato) {
        CorsoDiStudio corsoDiStudio = new CorsoDiStudio();
        corsoDiStudio.setCodice(regolamentoDiScelta.getCdsCod());
        corsoDiStudio.setDenominazione(regolamentoDiScelta.getCdsDes());
        corsoDiStudio.setTipoCorsoDiLaurea(TipoCorsoDiLaurea.valueOf(regolamentoDiScelta.getTipoCorsoCod()));
        corsoDiStudio.setDenominazioneFacolta(facolta.getFacDes());
        corsoDiStudio.setProgrammato(programmato);
        return corsoDiStudio;
    }
}
