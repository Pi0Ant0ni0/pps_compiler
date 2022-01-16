package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import lombok.Data;

import javax.annotation.Generated;
import java.util.Objects;

@Generated("jsonschema2pojo")
@Data
public class ChiaveUdContestualizzata {

    private ChiaveADContestualizzata chiaveAdContestualizzata;
    private String udCod;
    private String udDes;
    private Integer udId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiaveUdContestualizzata that = (ChiaveUdContestualizzata) o;
        return chiaveAdContestualizzata.equals(that.chiaveAdContestualizzata) && udCod.equals(that.udCod) && udDes.equals(that.udDes) && udId.equals(that.udId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chiaveAdContestualizzata, udCod, udDes, udId);
    }



}