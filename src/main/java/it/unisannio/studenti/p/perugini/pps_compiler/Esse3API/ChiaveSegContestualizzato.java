package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import lombok.Data;

import javax.annotation.Generated;
import java.util.Objects;

@Generated("jsonschema2pojo")
@Data
public class ChiaveSegContestualizzato {
    private ChiaveUdContestualizzata chiaveUdContestualizzata;
    private Integer segId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiaveSegContestualizzato that = (ChiaveSegContestualizzato) o;
        return chiaveUdContestualizzata.equals(that.chiaveUdContestualizzata) && segId.equals(that.segId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chiaveUdContestualizzata, segId);
    }





}