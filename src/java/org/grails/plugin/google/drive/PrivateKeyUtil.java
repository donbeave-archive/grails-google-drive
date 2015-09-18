package org.grails.plugin.google.drive;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Created by dstieglitz on 9/18/15.
 */
public class PrivateKeyUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static PrivateKey readPrivateKey(File keyFile) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // read key bytes
        FileInputStream in = new FileInputStream(keyFile);
        byte[] keyBytes = new byte[in.available()];
        in.read(keyBytes);
        in.close();

        String privateKey = new String(keyBytes, "UTF-8");
        privateKey = privateKey.replaceAll("(-+BEGIN RSA PRIVATE KEY-+\\r?\\n|-+END RSA PRIVATE KEY-+\\r?\\n?)", "");

        // don't use this for real projects!
        BASE64Decoder decoder = new BASE64Decoder();
        keyBytes = decoder.decodeBuffer(privateKey);

        // generate private key
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

//    public static PrivateKey readPrivateKey(String keyString) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        // read key bytes
//        byte[] keyBytes = keyString.getBytes();
//
//        String privateKey = new String(keyBytes, "UTF-8");
//        privateKey = privateKey.replaceAll("(-+BEGIN RSA PRIVATE KEY-+\\r?\\n|-+END RSA PRIVATE KEY-+\\r?\\n?)", "");
//
//        // don't use this for real projects!
//        BASE64Decoder decoder = new BASE64Decoder();
//        keyBytes = decoder.decodeBuffer(privateKey);
//
//        // generate private key
//        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        return keyFactory.generatePrivate(spec);
//    }

    public static PrivateKey readPrivateKey(String keyString) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        PemReader reader = new PemReader(new StringReader(keyString));
        PemObject pemObject = reader.readPemObject();
        reader.close();
        KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
        return factory.generatePrivate(privKeySpec);
    }
}
