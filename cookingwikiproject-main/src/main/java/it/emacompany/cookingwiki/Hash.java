package it.emacompany.cookingwiki;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
    
    // Metodo per l'hashing della password
    public static String getHashedPsw(String psw) throws NoSuchAlgorithmException{
        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        return translate(digest.digest(psw.getBytes(StandardCharsets.UTF_8)));
    }
    
    // Metodo per convertire byte[] in esadecimale
    public static String translate(byte[] psw) throws NoSuchAlgorithmException{
        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < psw.length; i++) {
          String hex = Integer.toHexString(0xff & psw[i]);
          if (hex.length() == 1) {
            hexString.append('0');
          }
          hexString.append(hex);
        }
        
        return hexString.toString();
    }
}
