package erp.chain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;


/**
 * 流水号生成器
 */
@Mapper
public interface SerialNumberCreatorMapper {
    /**
     * 获取唯一流水号，只在当前数据库范围内唯一，每天从1开始计数
     * @param prefix 前缀字符，每个前缀生成一个序列，不同序列分别计数
     * @param length 生成的序列字符串长度
     * @return 返回指定长度的流水号（以0填充），不包含前缀
     */
    public String getToday(@Param("prefix") String prefix, @Param("length")Integer length);
    /**
     * 获取唯一流水号，只在当前数据库范围内唯一，每天从1开始计数
     * @param prefix 前缀字符，每个前缀生成一个序列，不同序列分别计数
     * @param length 生成的序列字符串长度
     * @return 返回指定长度的流水号（以0填充），机构编号+流水号
     */
    public String getBranchToday(@Param("prefix") String prefix, @Param("length")Integer length, @Param("bId")BigInteger bId);
    /**
     * 获取唯一序号，只在当前数据库范围内唯一，商户内唯一
     * @param prefix 前缀字符，每个前缀生成一个序列，不同序列分别计数
     * @return begin,end
     */
    public String getNextVal(@Param("prefix") String prefix, @Param("size")Integer size);
}
