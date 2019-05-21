package erp.chain.model.online.onlineweixin;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by liuyandong on 2019-02-14.
 */
public class DeleteWxResourceMediaModel extends BasicModel {
    @NotNull
    private BigInteger tenantId;

    @NotEmpty
    private List<MediaInfo> mediaInfos;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public List<MediaInfo> getMediaInfos() {
        return mediaInfos;
    }

    public void setMediaInfos(List<MediaInfo> mediaInfos) {
        this.mediaInfos = mediaInfos;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (MediaInfo mediaInfo : mediaInfos) {
            ApplicationHandler.isTrue(mediaInfo.validate(), "mediaInfos");
        }
    }

    public static class MediaInfo extends BasicModel {
        @NotNull
        private String mediaId;

        @NotNull
        private BigInteger imgId;

        public String getMediaId() {
            return mediaId;
        }

        public void setMediaId(String mediaId) {
            this.mediaId = mediaId;
        }

        public BigInteger getImgId() {
            return imgId;
        }

        public void setImgId(BigInteger imgId) {
            this.imgId = imgId;
        }
    }
}
