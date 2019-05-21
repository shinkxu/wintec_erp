package erp.chain.utils;

import com.saas.common.util.LogUtil;
import com.saas.common.util.PropertyUtils;

import java.security.DigestException;
import java.security.MessageDigest;
import java.util.*;

/**
 * SHA1加密工具类
 * Created by wangms on 2017/3/13.
 */
public class SHA1Utils {
    /**
     * SHA1 安全加密算法
     * @param maps 参数key-value map集合
     * @return
     * @throws java.security.DigestException
     */
    public static String SHA1(Map maps) throws DigestException {
        //获取信息摘要 - 参数字典排序后字符串
        try {
            String decrypt = PropertyUtils.getDefault("meituanSignKey") + getOrderByLexicographic(maps);
            //指定sha1算法
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decrypt.getBytes("UTF-8"));
            //获取字节数组
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
//            return hexString.toString().toUpperCase();
            return hexString.toString().toLowerCase();

        } catch (Exception e) {
            LogUtil.logError(e.getMessage());
            throw new DigestException("签名错误！");
        }
    }
    /**
     * 获取参数的字典排序
     * @param maps 参数key-value map集合
     * @return String 排序后的字符串
     */
    private static String getOrderByLexicographic(Map<String,Object> maps){
        return splitParams(lexicographicOrder(getParamsName(maps)),maps);
    }
    /**
     * 获取参数名称 key
     * @param maps 参数key-value map集合
     * @return
     */
    private static List<String> getParamsName(Map<String,Object> maps){
        List<String> paramNames = new ArrayList<String>();
        for(Map.Entry<String,Object> entry : maps.entrySet()){
            paramNames.add(entry.getKey());
        }
        return paramNames;
    }
    /**
     * 参数名称按字典排序
     * @param paramNames 参数名称List集合
     * @return 排序后的参数名称List集合
     */
    private static List<String> lexicographicOrder(List<String> paramNames){
        Collections.sort(paramNames);
        return paramNames;
    }
    /**
     * 拼接排序好的参数名称和参数值
     * @param paramNames 排序后的参数名称集合
     * @param maps 参数key-value map集合
     * @return String 拼接后的字符串
     */
    private static String splitParams(List<String> paramNames,Map<String,Object> maps){
        StringBuilder paramStr = new StringBuilder();
        for(String paramName : paramNames){
            paramStr.append(paramName);
            for(Map.Entry<String,Object> entry : maps.entrySet()){
                if(paramName.equals(entry.getKey())){
                    paramStr.append(String.valueOf(entry.getValue()));
                }
            }
        }
        return paramStr.toString();
    }
    /**
     * 将参数组合成url
     * @param baseUrl
     * @param params
     */
    public static String combineParamsAsUrl(String baseUrl,Map params){
        Iterator it= params.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry)it.next();
            baseUrl += entry.getKey().toString() + "=" + (entry.getValue() == null ? "" : entry.getValue().toString()) + "&";
        }
        return baseUrl.substring(0, baseUrl.length() - 1);
    }

}
