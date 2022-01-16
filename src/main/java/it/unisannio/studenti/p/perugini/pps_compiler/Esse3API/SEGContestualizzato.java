package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import lombok.Data;

import javax.annotation.Generated;
import java.util.Objects;

@Generated("jsonschema2pojo")
@Data
public class SEGContestualizzato {

    private Integer aaRegFin;
    private Integer aaRegIni;
    private String ambDes;
    private String ambDesEng;
    private Integer ambId;
    private ChiaveSegContestualizzato chiaveSegContestualizzato;
    private String discCod;
    private Integer durStuInd;
    private Integer durUniVal;
    private Integer freqObbligFlg;
    private String interclaAmbDes;
    private String interclaAmbDesEng;
    private Object interclaAmbId;
    private Object interclaTipoAfCod;
    private String interclaTipoAfDes;
    private String interclaTipoAfDesEng;
    private Integer liberaOdFlg;
    private String nota;
    private Object oreMinFreq;
    private Integer peso;
    private String settCod;
    private TipoAfCod tipoAfCod;
    private String tipoAfDes;
    private String tipoAfDesEng;
    private Object tipoAfReitCod;
    private String tipoAfReitDes;
    private String tipoAfReitDesEng;
    private String tipoCreCod;
    private String tipoCreDes;
    private String tipoCreDesEng;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SEGContestualizzato that = (SEGContestualizzato) o;
        return chiaveSegContestualizzato.equals(that.chiaveSegContestualizzato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chiaveSegContestualizzato);
    }
}