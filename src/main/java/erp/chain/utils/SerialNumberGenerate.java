package erp.chain.utils;

/**
 * 编码
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/11/18
 */
public class SerialNumberGenerate{
    /**
     * 取下一个编码
     * @param digit
     * @param curr
     * @return
     */
    public static String nextSerialNumber(int digit, String curr){
        int d = digit >= 0 ? digit : 8;
        curr = (curr == null||curr.equals("")) ? String.valueOf(0) : curr;
        if(curr.length() > d){
            return null;
        }
        String number = number(d);
        String num = String.valueOf(Integer.parseInt(curr) + 1);
        return number.substring(0, d - num.length()) + num;
    }

    /**
     * 菜品编码
     * @param curr
     * @return
     */
    public static String nextGoodsCode(String curr){
        String number = curr.substring(4, curr.length());
        String catCode = curr.substring(0, 4);
        int length = 4;
        if(curr.length() > 8){
            length = curr.length() - 4;
        }
        return catCode + nextSerialNumber(length, number);
    }

    /**
     * 菜牌编码
     *
     * @return
     */
    public static String nextMenuCode(String curr){
        String number;
        if(curr==null||curr.equals("")){
            number = nextSerialNumber(4, null);
        }
        else{
            number = nextSerialNumber(4, curr.substring(curr.length() - 4, curr.length()));
        }
        return "CP" + DateUtils.getSpaceDate("yyMMdd") + number;
    }

    /**
     * @param id
     * @param curr
     * @return
     */
    public static String getNextCode(int id, String curr){
        return nextSerialNumber(id, curr);
    }

    /**
     * ��ȡ��ˮ��ģ��
     *
     * @param digit
     */
    private static String number(int digit){
        StringBuilder sb = new StringBuilder();
        for( int i=0;i<=digit;i++){
            sb.append('0');
        }
        return sb.toString();
    }

    /**
     * 生成菜品单位二级编码
     *
     * @param catCode
     * @return
     */
    public static String getNextCatTwoCode(String catCode){
        if(catCode!=null&&!catCode.equals("")){
            return catCode.substring(0, 2) + nextSerialNumber(2, catCode.substring(2, 4));
        }
        else{
            return null;
        }
    }

    /**
     * 组装数据，不足前置补0
     * @param length 字符串长度
     * @param value 传入的值
     * @return
     */
    public static String generateCode(Integer length,Integer value){
        int d = length >= 0 ? length : 10;
        value = (value == null) ? 0 : value;
        if(value.toString().length() > d){
            return null;
        }
        String number = number(d);
        String num = String.valueOf(value);
        return number.substring(0, d - num.length()) + num;
    }
}
