package ge.bestline.delivery.ws.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Provider {
    public static String md5(String input) {
        String md5;
        if (null == input || input.isEmpty()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes(), 0, input.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return md5;
    }

    public static String doubleMd5(String text) {
        return md5(md5(text));
    }

}
