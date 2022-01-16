package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import lombok.Data;
import lombok.Getter;

import javax.annotation.Generated;
import java.util.Objects;

@Generated("jsonschema2pojo")
@Data
public class ADContestualizzata {

    private String aaOrdDesEng;
    private Object adCapogruppo;
    private String adDesEng;
    private Integer adWebViewFlg;
    private Integer capoGruppoFlg;
    private String cdsDesEng;
    private ChiaveADContestualizzata chiaveAdContestualizzata;
    private String gruppoGiudCod;
    private String gruppoGiudDes;
    private String linguaInsDes;
    private String linguaInsDesEng;
    private Integer nonErogabileOdFlg;
    private String pdsDesEng;
    private Integer reiterabile;
    private String tipoEsaCod;
    private String tipoEsaDes;
    private String tipoEsaDesEng;
    private String tipoInsCod;
    private String tipoInsDes;
    private String tipoValCod;
    private String tipoValDes;
    private String tipoValDesEng;
    private String urlCorsoMoodle;
    private String urlSitoWeb;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ADContestualizzata that = (ADContestualizzata) o;
        return Objects.equals(chiaveAdContestualizzata, that.chiaveAdContestualizzata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chiaveAdContestualizzata);
    }
}