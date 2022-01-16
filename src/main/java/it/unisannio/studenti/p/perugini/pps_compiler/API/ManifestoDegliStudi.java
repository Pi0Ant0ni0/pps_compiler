package it.unisannio.studenti.p.perugini.pps_compiler.API;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;


@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "Manifesti Degli Studi")
public class ManifestoDegliStudi {

    @EqualsAndHashCode.Include
    @Getter @Setter @NonNull @Id
    private ChiaveManifestoDegliStudi chiaveManifestoDegliStudi;
    @Getter @Setter @NonNull
    private int annoOrdinamento;
    @Getter @Setter @NonNull
    private int cfuASceltaLibera;
    @Getter @Setter @NonNull
    private int cfuOrientamento;
    @Getter @Setter @NonNull
    private int cfuTotali;
    @Getter @Setter @NonNull
    private int cfuExtra;
    @Setter @Getter @NonNull
    private Map<Integer, AnnoAccademico> anniAccademici;



    public void addAnnoSchemaDIPiano(int anno, AnnoAccademico annoAccademico){
        if(this.anniAccademici == null)
            this.anniAccademici = new HashMap<>();
        this.anniAccademici.put(anno, annoAccademico);
    }

}
