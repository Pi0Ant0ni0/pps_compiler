package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AdLogConSyllabus {


    @JsonProperty("SyllabusAD")
    private SyllabusAD[] SyllabusAD;
    private ChiaveADContestualizzata chiaveADFisica;
    private ChiavePartizione chiavePartizione;
    private String dataFinValDid;
    private String dataFine;
    private String dataIniValDid;
    private String dataInizio;
    private String dataModLog;
    private String domPartEffCod;
    private String domPartEffDes;
    private String fatPartEffCod;
    private String fatPartEffDes;
    private String linguaDidCod;
    private String linguaDidDes;
    private Integer linguaDidId;
    private String partEffCod;
    private String partEffDes;
    private String sedeDes;
    private String sedeDesEng;
    private Integer sedeId;
    private String tipoDidCod;
    private String tipoDidDes;
    private String tipoDidDesEng;


}