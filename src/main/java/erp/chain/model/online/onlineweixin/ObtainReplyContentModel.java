package erp.chain.model.online.onlineweixin;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2019-02-14.
 */
public class ObtainReplyContentModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;
    @NotNull
    private String content;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
