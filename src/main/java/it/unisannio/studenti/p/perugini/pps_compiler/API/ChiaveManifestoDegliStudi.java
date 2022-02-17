package it.unisannio.studenti.p.perugini.pps_compiler.API;

import lombok.*;

import java.util.Optional;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ChiaveManifestoDegliStudi {
    private int coorte;
    private String codiceCorsoDiStudio;
    private String curricula;

    public Optional<String> getCurricula() {
        return Optional.ofNullable(curricula);
    }
}
