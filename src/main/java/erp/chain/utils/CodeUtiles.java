package erp.chain.utils;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/4/3
 */
public class CodeUtiles{

    /**
     * 获取随机数，最多10位
     */
    public static String getRandomString(int length) {
        String index="1";
        if(length>0&&length<=10){
            for(int i=0;i<length-1;i++){
                index=index+"0";
            }
        }
        int in=Integer.valueOf(index);
        return String.valueOf((int)((Math.random()*9+1)*in));
    }
}
