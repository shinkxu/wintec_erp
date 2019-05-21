package erp.chain.model.online.onlineweixin;

import erp.chain.model.online.BasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * Created by liuyandong on 2019-02-14.
 */
public class AddWxNewsArticleModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotNull
    private String title;

    @NotNull
    private String thumbMediaId;

    @NotNull
    @Length(max = 200)
    private String digest;

    @NotNull
    private String showCoverPic;

    @NotNull
    @Length(max = 20000)
    private String content;

    private String contentSourceUrl;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getShowCoverPic() {
        return showCoverPic;
    }

    public void setShowCoverPic(String showCoverPic) {
        this.showCoverPic = showCoverPic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentSourceUrl() {
        return contentSourceUrl;
    }

    public void setContentSourceUrl(String contentSourceUrl) {
        this.contentSourceUrl = contentSourceUrl;
    }
}
