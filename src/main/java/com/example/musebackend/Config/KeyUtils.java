package com.example.musebackend.Config;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    private KeyUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Parses a raw PEM-formatted String into a PrivateKey.
     * This method strips headers/footers and handles Base64 decoding.
     */
    public static PrivateKey parsePrivateKey(String keyContent) throws Exception {
        String cleanKey = keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", ""); // Removes newlines and spaces

        byte[] decoded = Base64.getDecoder().decode(cleanKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    /**
     * Parses a raw PEM-formatted String into a PublicKey.
     */
    public static PublicKey parsePublicKey(String keyContent) throws Exception {
        String cleanKey = keyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Removes newlines and spaces

        byte[] decoded = Base64.getDecoder().decode(cleanKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}