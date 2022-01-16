package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import lombok.Data;

@Data
public class AdLog {

    public ChiaveADContestualizzata chiaveADFisica;
    public ChiavePartizione chiavePartizione;
    public String dataFinValDid;
    public String dataFine;
    public String dataIniValDid;
    public String dataInizio;
    public String dataModLog;
    public String domPartEffCod;
    public String domPartEffDes;
    public String fatPartEffCod;
    public String fatPartEffDes;
    public String linguaDidCod;
    public String linguaDidDes;
    public Integer linguaDidId;
    public String partEffCod;
    public String partEffDes;
    public String sedeDes;
    public String sedeDesEng;
    public Integer sedeId;
    public String tipoDidCod;
    public String tipoDidDes;
    public String tipoDidDesEng;

}