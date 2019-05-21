package erp.chain.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Xumx on 2015/10/29.
 */
public class AESUtils {

    private static final String ALGORITHM = "AES";

    public synchronized static SecretKey generate() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
        generator.init(128);
        return generator.generateKey();
    }

    public synchronized static SecretKey getKey(String base64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getKey(new Base64().decode(base64));
    }

    public synchronized static SecretKey getKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return new SecretKeySpec(bytes, ALGORITHM);
    }

    public synchronized static String encrypt(String data, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return new Base64().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public synchronized static String decrypt(String data, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
        if (StringUtils.isNotEmpty(data) && data.indexOf("%") > -1) {
            data = URLDecoder.decode(data, "utf-8");
        }
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(new Base64().decode(data)));
    }


    public static final String KEY_ALGORITHM = "AES";

    public static String encrypt(String data, String aesKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        SecretKey secretKey = new SecretKeySpec(DigestUtils.md5(aesKey), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.encodeBase64String(cipher.doFinal(data.getBytes("UTF-8")));
    }

    public static String decrypt(String encryptedData, String aesKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        SecretKey secretKey = new SecretKeySpec(DigestUtils.md5(aesKey), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.decodeBase64(encryptedData)), "UTF-8");
    }

    /*******************************************************************************************************
     ***************************AES加密、解密工具修改，2018年4月11日18:16:54(刘艳东)***************************
     *******************************************************************************************************/
    public static final String ALGORITHM_AES_ECB_PKCS7PADDING = "AES/ECB/PKCS7Padding";
    public static final String ALGORITHM_AES_CBC_PKCS7PADDING = "AES/CBC/PKCS7Padding";
    public static final String PROVIDER_NAME_BC = "BC";

    public static byte[] encrypt(byte[] data, byte[] aesKey, String algorithm, String providerName) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey secretKey = new SecretKeySpec(aesKey, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(algorithm, providerName);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(byte[] data, byte[] aesKey, String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey secretKey = new SecretKeySpec(aesKey, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] aesKey, String algorithm, String providerName) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey secretKey = new SecretKeySpec(aesKey, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(algorithm, providerName);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] aesKey, String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey secretKey = new SecretKeySpec(aesKey, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] aesKey, byte[] iv, String algorithm, String providerName) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        SecretKey secretKey = new SecretKeySpec(aesKey, KEY_ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(algorithm, providerName);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        return cipher.doFinal(encryptedData);
    }
}
