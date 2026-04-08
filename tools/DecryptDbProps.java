import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;

public class DecryptDbProps {
    public static void main(String[] args) throws Exception {
        String propsPath = (args.length > 0) ? args[0] : "C:/ProgramData/spr/notas/db.properties";
        String masterPath = (args.length > 1) ? args[1] : "C:/ProgramData/spr/master.key";

        byte[] masterBytes = Files.readAllBytes(Paths.get(masterPath));
        String master = new String(masterBytes, StandardCharsets.UTF_8).trim();

        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(propsPath)) { p.load(in); }

        for (String name : p.stringPropertyNames()) {
            String v = p.getProperty(name);
            if (v != null && v.startsWith("ENC(") && v.endsWith(")")) {
                String inner = v.substring(4, v.length() - 1);
                String dec = decrypt(inner, master);
                System.out.println(name + "=" + dec);
            } else {
                System.out.println(name + "=" + (v==null?"":v));
            }
        }
    }

    private static String decrypt(String base64Cipher, String password) throws Exception {
        byte[] all = Base64.getDecoder().decode(base64Cipher);
        if (all.length < 8) return "";
        byte[] salt = new byte[8];
        System.arraycopy(all, 0, salt, 0, 8);
        byte[] cipherText = new byte[all.length - 8];
        System.arraycopy(all, 8, cipherText, 0, cipherText.length);

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        KeySpec ks = new PBEKeySpec(password.toCharArray());
        SecretKey sk = skf.generateSecret(ks);
        PBEParameterSpec pbeParam = new PBEParameterSpec(salt, 1000);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(Cipher.DECRYPT_MODE, sk, pbeParam);
        byte[] dec = cipher.doFinal(cipherText);
        return new String(dec, StandardCharsets.UTF_8);
    }
}
