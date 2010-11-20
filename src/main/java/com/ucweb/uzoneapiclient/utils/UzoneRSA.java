/**
 * UC乐园Rest接口 PHP SDK - 基本工具 - 写log
 *
 * @category   rsa
 * @package    com.ucweb.uzoneapiclient.utils
 * @author     Jiuhong Deng <dengjiuhong@gmail.com>
 * @version    $Id:$
 * @copyright  Jiuhong Deng
 * @link       http://u.uc.cn/
 * @since      File available since Release 1.0.0
 */
package com.ucweb.uzoneapiclient.utils;

import java.io.*;
import java.security.*;
import java.security.interfaces.*;
import java.math.*;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.encoders.Hex;
public class UzoneRSA {

    final public static int RAW   = 1;
    final public static int PKCS1 = 2;

    /*
    * 产生RSA公私钥对
    */
    public static KeyPair genRSAKeyPair() {
        KeyPairGenerator rsaKeyGen = null;
        KeyPair rsaKeyPair = null;
        try {
            System.out.println("Generating a pair of RSA key ... ");
            rsaKeyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            random.nextBytes(new byte[1]);
            rsaKeyGen.initialize(1024, new SecureRandom());
            rsaKeyPair = rsaKeyGen.genKeyPair();
            PublicKey rsaPublic = rsaKeyPair.getPublic();
            PrivateKey rsaPrivate = rsaKeyPair.getPrivate();
            System.out.println("1024-bit RSA key GENERATED.");
        } catch (Exception e) {
            System.out.println("Exception in keypair generation. Reason: " + e);
        }

        return rsaKeyPair;
    }

    /*
    * 列出密钥库中指定的条目
    */
    public static void showAllEntry(String filename, String pass) {
        try {
            FileInputStream inKeyStoreFile = new FileInputStream(filename);
            char[] password = pass.toCharArray();
            KeyStore from = KeyStore.getInstance("JKS", "SUN");
            from.load(null, null);
            from.load(inKeyStoreFile, password);
            Enumeration e = from.aliases();
            System.out.println("Entry List:");
            while (e.hasMoreElements()) {
                System.out.println((String) e.nextElement());
            }
            inKeyStoreFile.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /*
    * 列出密钥库中所有的条目
    */
    public static Vector getAllEntry(String filename, String pass) {
        Vector v = new Vector();
        try {
            FileInputStream inKeyStoreFile = new FileInputStream(filename);
            char[] password = pass.toCharArray();
            KeyStore from = KeyStore.getInstance("JKS", "SUN");
            from.load(null, null);
            from.load(inKeyStoreFile, password);
            Enumeration e = from.aliases();
            System.out.println("Entry List:");
            while (e.hasMoreElements()) {
                v.addElement((String) e.nextElement());

            }
            inKeyStoreFile.close();
            return v;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /*
    * 获得私钥
    */
    public static RSAPrivateKey loadPrivateKey(String filename, String keyName,
            String pass) throws Exception {
        FileInputStream inKeyStoreFile = new FileInputStream(filename);
        char[] password = pass.toCharArray();
        KeyStore from = KeyStore.getInstance("JKS", "SUN");
        from.load(null, null);
        from.load(inKeyStoreFile, password);
        Key testkey = from.getKey(keyName, password);
        RSAPrivateKey pvtKey = (RSAPrivateKey) testkey;
        System.out.println("Private key exponent =\r\n"
                + pvtKey.getPrivateExponent().toString(16) + "\r\n");
        inKeyStoreFile.close();
        return pvtKey;
    }

    /*
    * 获得公钥
    */
    public static RSAPublicKey loadPublicKey(String filename, String keyName,
            String pass) throws Exception {
        FileInputStream inKeyStoreFile = new FileInputStream(filename);
        char[] password = pass.toCharArray();
        KeyStore from = KeyStore.getInstance("JKS", "SUN");
        from.load(null, null);
        from.load(inKeyStoreFile, password);
        java.security.cert.Certificate c = from.getCertificate(keyName);
        RSAPublicKey pubKey = (RSAPublicKey) c.getPublicKey();
        System.out.println("Public key exponent =\r\n"
                + pubKey.getPublicExponent().toString(16) + "\r\n");
        inKeyStoreFile.close();
        return pubKey;
    }

    /*
    * 使用公钥加密
    */
    public static byte[] rsaPubEncrypt(RSAPublicKey PubKey, byte[] clearBytes,
            int type) {

        BigInteger mod = PubKey.getModulus();
        BigInteger pubExp = PubKey.getPublicExponent();

        RSAKeyParameters pubParameters = new RSAKeyParameters(false, mod,
                pubExp);

        System.out.println("mod:\r\n" + mod.toString(16));
        System.out.println("pubExp:\r\n" + pubExp.toString(16));
        AsymmetricBlockCipher eng = new RSAEngine();
        if (type == PKCS1)
            eng = new PKCS1Encoding(eng);
        eng.init(true, pubParameters);
        byte[] data = null;
        try {
            System.out.println("clearBytes:\r\n"
                    + new String(Hex.encode(clearBytes)));
            data = eng.processBlock(clearBytes, 0, clearBytes.length);
            System.out.println("EncBytes:\r\n" + new String(Hex.encode(data)));
            return data;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /*
    * 公钥解密
    */
    public static byte[] rsaPubDecrypt(RSAPublicKey PubKey, byte[] clearBytes,
            int type) {

        BigInteger mod = PubKey.getModulus();
        BigInteger pubExp = PubKey.getPublicExponent();

        RSAKeyParameters pubParameters = new RSAKeyParameters(false, mod,
                pubExp);

        System.out.println("mod:\r\n" + mod.toString(16));
        System.out.println("pubExp:\r\n" + pubExp.toString(16));
        AsymmetricBlockCipher eng = new RSAEngine();
        if (type == PKCS1)
            eng = new PKCS1Encoding(eng);
        eng.init(false, pubParameters);
        byte[] data = null;
        try {
            System.out.println("clearBytes:\r\n"
                    + new String(Hex.encode(clearBytes)));
            data = eng.processBlock(clearBytes, 0, clearBytes.length);
            System.out.println("EncBytes:\r\n" + new String(Hex.encode(data)));
            return data;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /*
    * 私钥解密
    */
    public static byte[] rsaPriDecrypt(RSAPrivateKey prvKeyIn,
            byte[] encodedBytes, int type) {

        RSAPrivateCrtKey prvKey = (RSAPrivateCrtKey) prvKeyIn;

        BigInteger mod = prvKey.getModulus();
        BigInteger pubExp = prvKey.getPublicExponent();
        BigInteger privExp = prvKey.getPrivateExponent();
        BigInteger pExp = prvKey.getPrimeExponentP();
        BigInteger qExp = prvKey.getPrimeExponentQ();
        BigInteger p = prvKey.getPrimeP();
        BigInteger q = prvKey.getPrimeQ();
        BigInteger crtCoef = prvKey.getCrtCoefficient();

        RSAKeyParameters privParameters = new RSAPrivateCrtKeyParameters(mod,
                pubExp, privExp, p, q, pExp, qExp, crtCoef);

        AsymmetricBlockCipher eng = new RSAEngine();
        if (type == PKCS1)
            eng = new PKCS1Encoding(eng);

        eng.init(false, privParameters);
        byte[] data = null;
        try {
            data = eng.processBlock(encodedBytes, 0, encodedBytes.length);
            return data;
        } catch (Exception e) {
            return null;
        }

    }

    /*
    * 使用私钥加密
    */
    public static byte[] rsaPriEncrypt(RSAPrivateKey prvKeyIn,
            byte[] encodedBytes, int type) {
        RSAPrivateCrtKey prvKey = (RSAPrivateCrtKey) prvKeyIn;
        BigInteger mod = prvKey.getModulus();
        BigInteger pubExp = prvKey.getPublicExponent();
        BigInteger privExp = prvKey.getPrivateExponent();
        BigInteger pExp = prvKey.getPrimeExponentP();
        BigInteger qExp = prvKey.getPrimeExponentQ();
        BigInteger p = prvKey.getPrimeP();
        BigInteger q = prvKey.getPrimeQ();
        BigInteger crtCoef = prvKey.getCrtCoefficient();
        RSAKeyParameters privParameters = new RSAPrivateCrtKeyParameters(mod,
                pubExp, privExp, p, q, pExp, qExp, crtCoef);
        AsymmetricBlockCipher eng = new RSAEngine();
        if (type == PKCS1)
            eng = new PKCS1Encoding(eng);
        eng.init(true, privParameters);
        byte[] data = null;
        try {
            data = eng.processBlock(encodedBytes, 0, encodedBytes.length);
            return data;
        } catch (Exception e) {
            return null;
        }

    }
}