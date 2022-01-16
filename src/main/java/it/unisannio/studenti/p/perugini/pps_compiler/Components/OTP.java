package it.unisannio.studenti.p.perugini.pps_compiler.Components;

import it.unisannio.studenti.p.perugini.pps_compiler.Utils.CONSTANTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class OTP {

    @Value("${otp.otpKey}")
    private String key;
    private Logger logger = LoggerFactory.getLogger(OTP.class);

    public String  makeOtp(){
        final StringBuilder otp = new StringBuilder(8);
        for (int i = 0; i < 6; i++) {
            try {
                otp.append(SecureRandom.getInstance(CONSTANTS.otp_algorithm).nextInt(9));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return otp.toString();
    }

    public String cryptOTP(String otp) {
        String hash="";
        try {
            SecretKeySpec keyspec=new SecretKeySpec(key.getBytes(),"HmacSHA256");
            Mac mac =Mac.getInstance("HmacSHA256");
            mac.init(keyspec);
            byte[] encrypted=mac.doFinal(otp.getBytes());
            hash= Base64.getEncoder().encodeToString(encrypted);
            logger.info("ecco otp cryptato: "+hash);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return hash;
    }


}
