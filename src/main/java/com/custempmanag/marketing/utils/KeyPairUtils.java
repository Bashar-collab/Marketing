package com.custempmanag.marketing.utils;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyPairUtils {
    public static KeyPair getKeyPairFromFiles(String publicKeyPath, String privateKeyPath) throws IOException {
        // Read public key
        PublicKey publicKey = readPublicKey(publicKeyPath);

        // Read private key
        PrivateKey privateKey = readPrivateKey(privateKeyPath);

        return new KeyPair(publicKey, privateKey);
    }

    /*
    private static PublicKey readPublicKey(String filePath) throws IOException {
        try (PEMParser pemParser = new PEMParser(new FileReader(filePath))) {
            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
            return new JcaPEMKeyConverter().getPublicKey(subjectPublicKeyInfo);
        }
    }

    private static PrivateKey readPrivateKey(String filePath) throws IOException {
        try (PEMParser pemParser = new PEMParser(new FileReader(filePath))) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
        }
    }
     */

    private static PublicKey readPublicKey(String classpathFile) throws IOException {
        try (InputStream is = new ClassPathResource(classpathFile).getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             PEMParser pemParser = new PEMParser(isr)) {

            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
            return new JcaPEMKeyConverter().getPublicKey(subjectPublicKeyInfo);
        }
    }

    private static PrivateKey readPrivateKey(String classpathFile) throws IOException {
        try (InputStream is = new ClassPathResource(classpathFile).getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             PEMParser pemParser = new PEMParser(isr)) {

            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
        }
    }


}
