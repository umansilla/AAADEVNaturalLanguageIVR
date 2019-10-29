package service.AAADEVNaturalLanguageIVR.Security;

import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.avaya.collaboration.util.logger.Logger;

import service.AAADEVNaturalLanguageIVR.Util.Constants;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class AES {
	private final String secretKey = Constants.SECRET_KEY;
    private final String salt = Constants.SALT;
    private final BASE64Decoder base64Decoder = new BASE64Decoder();
    private final BASE64Encoder base64Encoder = new BASE64Encoder();
    private static final Logger logger = Logger.getLogger(AES.class);
    
    /**
     * Metodo creado para encriptar texto.
     * @param strToEncrypt Texto para encriptar
     * @return Texto encriptado
     */
    public String encrypt(String strToEncrypt) {
    	try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(StandardCharsets.ISO_8859_1), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return base64Encoder.encode(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.ISO_8859_1)));
        } catch (Exception e) {
        	logger.error("Error while encrypting: " + e.toString());
        }
        return null;
    }

    /**
     * Metodo para desencriptar texto.
     * @param strToDecrypt Texto encriptado.
     * @return Texto desencriptado.
     */
    public String decrypt(String strToDecrypt) {
    	 try {

             byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
             IvParameterSpec ivspec = new IvParameterSpec(iv);

             SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
             KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(StandardCharsets.ISO_8859_1), 65536, 256);
             SecretKey tmp = factory.generateSecret(spec);
             SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

             Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
             cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
             return new String(cipher.doFinal(base64Decoder.decodeBuffer(strToDecrypt)), StandardCharsets.ISO_8859_1);
         } catch (Exception e) {
        	 logger.error("Error while decrypting: " + e.toString());
         }
         return null;
    }
}
