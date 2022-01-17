package it.unisannio.studenti.p.perugini.pps_compiler.EndPoint.ManifestoDegliStudi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManifestoPreviewDTO {
    private String curricula;
    private int anno;
}
