package erp.chain.service.base;

import com.saas.common.exception.ServiceException;
import com.saas.common.util.LogUtil;
import erp.chain.mapper.SerialNumberCreatorMapper;
import erp.chain.utils.Args;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 接口
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/3/22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PublicWSService{
    @Autowired
    public SerialNumberCreatorMapper serialNumberCreatorMapper;

    /**
     * 条形码前缀
     */
    public static String BAR_PREFIX = "2";
    /**
     * 条形码长度
     */
    public static int BAR_SIZE = 13;

    public ApiRest barCreator(Map params){
        ApiRest apiRest = new ApiRest();
        if(params.get("BAR_PREFIX") != null && !"".equals(params.get("BAR_PREFIX"))){
            BAR_PREFIX = params.get("BAR_PREFIX").toString();
        }
        if(params.get("BAR_SIZE") != null && !"".equals(params.get("BAR_SIZE"))){
            BAR_SIZE = Integer.parseInt(params.get("BAR_SIZE").toString());
        }
        String tenStr = StringUtils.trimToNull(params.get("tenantId").toString());
        Args.isInteger(tenStr, "tenantId 必须为整数");
        String tenantId = params.get("tenantId").toString();
        String no = "";
        String prefix = "bar_2" + tenantId;
        no = serialNumberCreatorMapper.getToday(prefix, BAR_SIZE - 2);

        if(params.get("isBarCode") != null && "1".equals(params.get("isBarCode").toString())){
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String dateMark = format.format(new Date());
            no = dateMark.substring(2, dateMark.length()) + no;
        }
        int parityBit = parityBit(BAR_PREFIX + no);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("生成编码成功！");
        apiRest.setData(BAR_PREFIX + no + parityBit);
        return apiRest;
    }

    public static char[] getChars(CharSequence self){
        return self.toString().toCharArray();
    }

    /**
     * 根据序列位计算校验位
     *
     * @param sequence 序列位
     *                 <p>
     *                 校验码的计算步骤如下：
     *                 a、从代码位置序号1开始，所有偶数位的数字代码求和。
     *                 b、将步骤a的和乘以3。
     *                 c、从代码位置序号2开始，所有奇数位的数字代码求和。
     *                 d、将步骤b与步骤c的结果相加。
     *                 e、用大于或等于步骤d所得结果且为10最小整数倍的数减去步骤d所得结果，其差即为所求校验码的值。
     */
    private static int parityBit(String sequence){
        char[] seqChars = getChars(sequence);
        //a、自右向左顺序
        int evenSum = 0;
        //c、自右向左顺序
        int oddSum = 0;

        for(int x = seqChars.length - 1; x >= 0; x -= 2){
            evenSum += (int)seqChars[x];
            if(x != 0){
                oddSum += (int)seqChars[x - 1];
            }
        }
        // b、d
        int sum = evenSum * 3 + oddSum;
        int zs = sum;
        while(true){
            if(zs % 10 == 0){
                break;
            }
            zs++;
        }
        return (zs - sum);
    }
}
