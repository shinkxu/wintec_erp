package erp.chain.utils;

import com.saas.common.util.LogUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 非对称加密
 * Created by Xumx on 2015/10/28.
 */
public class RSAUtils {

    private static final int KEYSIZE = 1024;

    private static final String ALGORITHM = "RSA";

    private static final String PADDING = "RSA/ECB/PKCS1Padding";

    private static final String PROVIDER = "SC";

    /**
     * 生成非对称加密公密钥对
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public synchronized static Map<String, Object> generate() throws NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<String, Object>();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(KEYSIZE);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        map.put("public", publicKey);
        map.put("private", privateKey);
        return map;
    }

    /**
     * 使用模和指数生成RSA公钥
     *
     * @param modulus
     * @param exponent
     * @return
     */
    public static RSAPublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            return null;
        }
    }

    /**
     * 使用模和指数生成RSA私钥
     *
     * @param modulus
     * @param exponent
     * @return
     */
    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
        }
        return null;
    }

    /**
     * 公钥加密字符串
     *
     * @param text
     * @param key
     * @return
     */
    public static String encrypt(String text, RSAPublicKey key) {
        return encrypt(text, key, key.getModulus().bitLength());
    }

    /**
     * 私钥加密字符串
     *
     * @param text
     * @param key
     * @return
     */
    public static String encrypt(String text, RSAPrivateKey key) {
        return encrypt(text, key, key.getModulus().bitLength());
    }

    /**
     * 密钥加密字符串
     *
     * @param text
     * @param key
     * @param modulusLen
     * @return
     */
    static String encrypt(String text, Key key, int modulusLen) {
        try {
            final Cipher cipher = Cipher.getInstance(PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //加密数据长度 <= 模长-11 【Ps.默认算法提供者的长度计算，BC 提供的加密算法能够支持到的 RSA 明文长度最长为密钥长度】
            //如果明文长度大于模长-11则要分组加密
            List<byte[]> splitTexts = splitBytes(text.getBytes(), (int) (modulusLen / 8 - 11));
            List<Byte> btyeList = new ArrayList<>();
            for (byte[] s : splitTexts) {
                btyeList.addAll(arrayAsList(cipher.doFinal(s)));
            }
            return new Base64().encodeToString(listAsArray(btyeList));
        } catch (Exception e) {
            throw new IllegalArgumentException("密钥加密字符串:", e);
        }
    }

    private static List<Byte> arrayAsList(byte[] arr) {
        List<Byte> byteList = new ArrayList<>(arr.length);
        for (byte b : arr) {
            byteList.add(b);
        }
        return byteList;
    }

    private static byte[] listAsArray(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int x = 0; x < list.size(); x++) {
            bytes[x] = list.get(x);
        }
        return bytes;
    }

    /**
     * 公钥解密字符串
     *
     * @param text
     * @param key
     * @return
     */
    public static String decrypt(String text, RSAPublicKey key) throws Exception {
        return decrypt(text, key, key.getModulus().bitLength());
    }

    /**
     * 私钥解密字符串
     *
     * @param text
     * @param key
     * @return
     */
    public static String decrypt(String text, RSAPrivateKey key) throws Exception {
        return decrypt(text, key, key.getModulus().bitLength());
    }

    /**
     * 密钥解密字符串
     *
     * @param data
     * @param key
     * @param modulusLen
     * @return
     * @throws Exception
     */
    static String decrypt(String data, Key key, int modulusLen) throws Exception {
        if (StringUtils.isNotEmpty(data) && data.indexOf("%") > -1) {
            data = URLDecoder.decode(data, "utf-8");
        }
        final Cipher cipher = Cipher.getInstance(PADDING);
        cipher.init(Cipher.DECRYPT_MODE, key);

        List<byte[]> splitTexts = splitBytes(new Base64().decode(data), (int) (modulusLen / 8));

        //如果密文长度大于模长则要分组解密
        List<Byte> btyeList = new ArrayList<>();
        for (byte[] s : splitTexts) {
            btyeList.addAll(arrayAsList(cipher.doFinal(s)));
        }
        return new String(listAsArray(btyeList));
    }

    /**
     * 拆分字符串
     *
     * @param bytes
     * @param splitLen
     * @return
     */
    private synchronized static List<byte[]> splitBytes(byte[] bytes, int splitLen) {
        List<byte[]> bytesList = new ArrayList<>();
        int strLen = bytes.length;
        byte[] splitBytes = null;
        for (int i = 0; i < strLen; i++) {
            if (i == 0 || i % splitLen == 0) {
                if (splitBytes != null) {
                    bytesList.add(splitBytes);
                }
                if (strLen - i > splitLen) {
                    splitBytes = new byte[splitLen];
                } else {
                    splitBytes = new byte[strLen - i];
                }
            }
            splitBytes[i % splitLen] = bytes[i];
            if (i == (strLen - 1)) {
                bytesList.add(splitBytes);
            }
        }
        return bytesList;
    }

    public static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

    public static RSAPublicKey getPublicKey(String publicKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }

}
