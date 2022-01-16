package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import lombok.Data;

import javax.annotation.Generated;
import java.util.Objects;

@Generated("jsonschema2pojo")
@Data
public class ChiaveADContestualizzata {

    private Integer aaOffId;
    private String aaOrdCod;
    private String aaOrdDes;
    private Integer aaOrdId;
    private String adCod;
    private String adDes;
    private Integer adId;
    private Integer afId;
    private String cdsCod;
    private String cdsDes;
    private Integer cdsId;
    private String pdsCod;
    private String pdsDes;
    private Integer pdsId;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiaveADContestualizzata that = (ChiaveADContestualizzata) o;
        return aaOffId.equals(that.aaOffId) && Objects.equals(adId, that.adId) && cdsId.equals(that.cdsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aaOffId, adId, cdsId);
    }
}