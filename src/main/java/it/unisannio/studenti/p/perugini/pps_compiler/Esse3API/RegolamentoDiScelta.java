package it.unisannio.studenti.p.perugini.pps_compiler.Esse3API;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.annotation.Generated;
import java.util.Date;
import java.util.Objects;

@Generated("jsonschema2pojo")
@Data
public class RegolamentoDiScelta {

    private Integer aaOrdId;
    private Integer aaRevisioneId;
    private String cdsCod;
    private String cdsDes;
    private Integer cdsId;
    private Integer coorte;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/mm/yyyy")
    private Date dataInserimento;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/mm/yyyy")
    private Date dataUltimaModifica;
    private String facCod;
    private Integer facId;
    private Integer regsceId;
    private Stato stato;
    private String tipoCorsoCod;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegolamentoDiScelta that = (RegolamentoDiScelta) o;
        return cdsCod.equals(that.cdsCod) && cdsDes.equals(that.cdsDes) && cdsId.equals(that.cdsId) && coorte.equals(that.coorte) && facCod.equals(that.facCod) && facId.equals(that.facId) && tipoCorsoCod.equals(that.tipoCorsoCod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cdsCod, cdsDes, cdsId, coorte, facCod, facId, tipoCorsoCod);
    }
}