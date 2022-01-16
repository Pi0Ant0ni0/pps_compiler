package it.unisannio.studenti.p.perugini.pps_compiler.Exception;

import lombok.NonNull;

public class PPSNotFoundException extends Exception {
    public PPSNotFoundException(@NonNull String s) {
        super(s);
    }

    public PPSNotFoundException() {
    }
}
