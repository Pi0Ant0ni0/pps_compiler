package it.unisannio.studenti.p.perugini.pps_compiler.API.ValueObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unisannio.studenti.p.perugini.pps_compiler.Exception.EmailNonCorrettaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.StringTokenizer;

@ToString
@EqualsAndHashCode
public class Email {
    @Getter @NonNull
    private String email;

    @JsonCreator
    public Email(@JsonProperty("email") String email) throws EmailNonCorrettaException {
        StringTokenizer tokenizer = new StringTokenizer(email,"@");
        if (tokenizer.countTokens() != 2)
            throw new EmailNonCorrettaException("Indirizzo email non è nel formato corretto");
       this.email = email;
    }

    public String getNomeDominio() {
        StringTokenizer tokenizer = new StringTokenizer(email,"@");
        tokenizer.nextToken();
        return tokenizer.nextToken();
    }

}
