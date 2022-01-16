package it.unisannio.studenti.p.perugini.pps_compiler.Exception;

public class InsegnamentoNotFoundException extends Exception {
    public InsegnamentoNotFoundException(String insegnamento_non_presente_nel_db) {
        super(insegnamento_non_presente_nel_db);
    }

    public InsegnamentoNotFoundException() {
    }
}
