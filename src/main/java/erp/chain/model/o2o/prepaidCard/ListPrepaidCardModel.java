package erp.chain.model.o2o.prepaidCard;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * 查询预付卡model
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/6/5
 */
public class ListPrepaidCardModel extends BasicModel{
    @NotNull
    private BigInteger tenantId;
    @NotNull
    private BigInteger branchId;
    @Min(1)
    private Integer page=1;
    @Max(1000)
    private Integer rows=20;
    private String cardCode;
    private BigInteger typeId;
    private String startTime;
    private String endTime;

    @Override
    public boolean validate(){
        return super.validate();
    }

    @Override
    public void validateAndThrow(){
        super.validateAndThrow();
    }

    public BigInteger getTenantId(){
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId){
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId(){
        return branchId;
    }

    public void setBranchId(BigInteger branchId){
        this.branchId = branchId;
    }

    public Integer getPage(){
        return page;
    }

    public void setPage(Integer page){
        this.page = page;
    }

    public Integer getRows(){
        return rows;
    }

    public void setRows(Integer rows){
        this.rows = rows;
    }

    public String getCardCode(){
        return cardCode;
    }

    public void setCardCode(String cardCode){
        this.cardCode = cardCode;
    }

    public BigInteger getTypeId(){
        return typeId;
    }

    public void setTypeId(BigInteger typeId){
        this.typeId = typeId;
    }

    public String getStartTime(){
        return startTime;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public String getEndTime(){
        return endTime;
    }

    public void setEndTime(String endTime){
        this.endTime = endTime;
    }
}
