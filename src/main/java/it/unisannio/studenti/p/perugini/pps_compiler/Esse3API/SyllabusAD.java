package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import lombok.Data;

@Data
public class SyllabusAD {

    private Integer adLogId;
    private String altreInfo;
    private String altreInfoEng;
    private ChiaveADContestualizzata chiaveADContestualizzata;
    private ChiavePartizione chiavePartizione;
    private String contenuti;
    private String contenutiEng;
    private Integer desAdPubblFlg;
    private Integer fisicaFlg;
    private String metodiDidattici;
    private String metodiDidatticiEng;
    private String modalitaVerificaApprendimento;
    private String modalitaVerificaApprendimentoEng;
    private String obiettiviFormativi;
    private String obiettiviFormativiEng;
    private String prerequisiti;
    private String prerequisitiEng;
    private Object realFisicaFlg;
    private String testiRiferimento;
    private String testiRiferimentoEng;

}