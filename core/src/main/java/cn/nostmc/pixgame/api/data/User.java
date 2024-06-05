package cn.nostmc.pixgame.api.data;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * 它的特殊性是，它的hashCode是根据name和headUrl生成的MD5的前18位数字转换成long再乘以2得到的。
 */
public class User {

    public String name;
    public String headUrl;
    public String id;

    public User(String name, String head, String id) {
        this.name = name;
        this.headUrl = head;
        this.id = id;
    }


    @Override
    public int hashCode() {
        try {
            String input = name + headUrl;
            // Compute MD5 hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            String subDigits = getString(messageDigest);
            // Convert substring to long and double
            return 2 << Long.parseLong(subDigits);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getString(byte[] messageDigest) {
        BigInteger no = new BigInteger(1, messageDigest);
        // Convert MD5 hash into hexadecimal
        StringBuilder hashText = new StringBuilder(no.toString(16));
        while (hashText.length() < 32) {
            hashText.insert(0, "0");
        }
        // Extract all digits
        String digits = hashText.toString().replaceAll("\\D", "");
        // Take a substring of the digits
        String subDigits = digits.substring(0, Math.min(18, digits.length()));
        return subDigits;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return this.hashCode() == obj.hashCode();
        } else {
            return Objects.equals(this.toString(), obj.toString());
        }
    }


    @Override
    public String toString() {
        return "{name: " + name + ", Url: " + headUrl + "}";
    }

}
