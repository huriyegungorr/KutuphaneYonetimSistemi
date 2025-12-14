package com.example.kutuphaneyonetimsistemi.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SifreUtil {

    private static final int SALT_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String HASH_FORMAT = "$SIM_V1$";

    public static String hashSifre(String sifre) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] saltBytes = new byte[SALT_LENGTH];
            random.nextBytes(saltBytes);
            String saltString = Base64.getEncoder().encodeToString(saltBytes);

            String salted = sifre + saltString;

            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = md.digest(salted.getBytes());
            String hashString = Base64.getEncoder().encodeToString(hashBytes);

            return HASH_FORMAT + saltString + "$" + hashString;

        } catch (Exception e) {
            throw new RuntimeException("Hash üretilemedi", e);
        }
    }

    public static boolean checkSifre(String girilenSifre, String dbHash) {
        try {
            if (dbHash == null || !dbHash.startsWith(HASH_FORMAT)) {
                return false;
            }

            String[] parts = dbHash.split("\\$");
            if (parts.length != 4) return false;

            String salt = parts[2];
            String dbHashValue = parts[3];

            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            String salted = girilenSifre + salt;
            byte[] newHashBytes = md.digest(salted.getBytes());
            String newHashString = Base64.getEncoder().encodeToString(newHashBytes);

            return newHashString.equals(dbHashValue);

        } catch (Exception e) {
            return false;
        }
    }
}
