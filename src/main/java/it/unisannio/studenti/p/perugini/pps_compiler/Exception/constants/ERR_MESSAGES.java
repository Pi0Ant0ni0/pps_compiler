package it.unisannio.studenti.p.perugini.pps_compiler.Exception.constants;

public class ERR_MESSAGES {
    public static final String DB_UPDATING ="Impossibile completare l'operazione, il database è in aggiornamento, riprova tra qualche minuto";
    public static final String OTP_EXPIRED = "OTP scaduto, effettua nuovamente il login";
    public static final String OTP_NOT_VALID= "OTP inserito non è valido";
    public static final String USER_NOT_FOUND= "Effetua prima la registrazione";
    public static final String CORSO_NOT_FOUND= "Impossibile trovare il corso di studio con codice: ";
    public static final String ATTIVITA_NOT_FOUND = "Attività didattica non presente nel database";
    public static final String MANIFESTO_GIA_PRESENTE = "Il manifesto degli studi che si sta inserendo è già presente nel database";
    public static final String MANIFESTO_ORDINAMENTO_NOT_FOUND = "Il manifesto non è associato ad un ordinamento valido";
    public static final String MANIFESTO_CFU_A_SCELTA_ORDINAMENTO = "Il numero di cfu a scelta non è nel range definito dall'ordinamento";
    public static final String MANIFESTO_CFU_ORIENTAMENTO_ORDINAMENTO = "Il numero di cfu di orientamento non è nel range definito dall'ordinamento";
    public static final String MANIFESTO_CFU_TOTALI_ORDINAMENTO = "Il numero di cfu totali non è nel range definito dall'ordinamento";
    public static final String MANIFESTO_CFU_TOTALI= "il numero di cfu totali dichiarati non coincide con quelli effettivi";
    public static final String MANIFESTO_CFU_A_SCELTA= "il numero di cfu a scelta dichiarati non coincide con quelli effettivi";
    public static final String MANIFESTO_CFU_ORIENTAMENTO= "il numero di cfu di orientamento dichiarati non coincide con quelli effettivi";
    public static final String MANIFESTO_ATTIVITA_DUPLICATA= "Il manifesto contiene due volta la stessa attivita didattica: ";
    public static final String ATTIVITA_NOT_VALID ="attivita non è valida, codice attivita: ";
    public static final String MANIFESTO_FINESTRA_COMPILAZIONE_NON_VALIDA = "la finestra di compilazione non è valida";
    public static final String PPS_COMPILATO_ACCETTATO="modulo PPS già compilato ed accettato non è possibile compilarne di nuovi";
    public static final String PPS_COMPILATO_IN_REVISIONE= "modulo PPS già compilato ed in fase di revisione, impossibile compilarne di nuovi";
    public static final String MANIFESTO_NOT_FOUND = "manifesto degli studi non trovato, corte e codice del corso di studio: ";
    public static final String PPS_ORIENTAMENTO_NOT_FOUND="E' necessario definire delle attivita didattiche di orientamento";
    public static final String PPS_ORIENTAMENTO_NOT_REQUIRED="Il manifesto degli studi non prevede degli insegnamenti di orientamento";
    public static final String PPS_ORIENTAMENTO_NOT_VALID = "l'orientamento specificato non è valido, controlla il tuo manifesto degli studi";
    public static final String PPS_ATTIVITA_DUPLICATA= "il pps contiene della attività duplicate";
    public static final String PPS_AUTOMATICA_APPROVAZIONE = "il pps contiente tutti insegnamenti di automatica approvazione, non è necessario compilare il modulo PPS";
    public static final String PPS_FINESTRA_COMPILAZIONE = "non è possibile compilare il modulo pps, informati sulla finestra temporale in cui è consentito farlo";
    public static final String PPS_NOT_FOUND = "impossibile trovare il modulo PPS richiesto";
    public static final String PPS_FORBIDDEN= "impossibile recuperare il pps di un altro utente";
    public static final String REGISTRATION_EMAIL = "E' possibile registrarsi solo tramite email unisannio";
    public static final String REGISTRATION_DUPLICATED = "sei già registrato, effettua il login";
    public static final String REGISTRATION_MATRICOLA = "matricola non valida, il sistema è aperto solo agli studenti del dipartimento di ingegneria";
    public static final String SAD_NON_VALIDO= "un operatore della segreteria non è associato a nessun corso di studio e non presenta matricola";
    public static final String DOCENTE_NON_VALIDO = "un docente deve essere associato almeno ad un corso di laurea e non presenta matricola";
    public static final String STUDENTE_NON_VALIDO = "uno studente deve essere associato ad un corso di laurea e deve presentare una matricola";




}
