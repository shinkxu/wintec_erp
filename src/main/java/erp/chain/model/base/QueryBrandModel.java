package erp.chain.model.base;
import erp.chain.model.Model;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;
/**
 * 分页查询品牌model
 * @author Administrator 2016-11-25
 */
public class QueryBrandModel extends Model{
    /**
     * 
     */
    @NotNull
    private Long id;
    /**
     * 编码
     */
    @NotNull
    private String code;
    /**
     * 名称
     */
    @NotNull
    private String name;
    /**
     * 状态：0正常1停用
     */
    @NotNull
    private Integer state;
    /**
     * 助记码
     */
    @NotNull
    private String mnemonic;
    /**
     * 备注
     */
    @NotNull
    private String memo;
    /**
     * 商户ID，该分店所属商户，tenant.id
     */
    @NotNull
    private BigInteger tenantId;
    /**
     * 
     */
    @NotNull
    private Date createAt;
    /**
     * 
     */
    @NotNull
    private String createBy;
    /**
     * 
     */
    @NotNull
    private Date lastUpdateAt;
    /**
     * 
     */
    @NotNull
    private String lastUpdateBy;
    /**
     * 
     */
    @NotNull
    private Boolean isDeleted;
    /**
     * 
     */
    @NotNull
    private BigInteger version;
    /**
     * 
     */
    @NotNull
    private BigInteger branchId;
    /**
     * 
     */
    @NotNull
    private BigInteger localId;
}