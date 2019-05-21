package erp.chain.model.online.onlineweixin;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;

/**
 * Created by liuyandong on 2018-06-19.
 */
public class GeographyCoderModel extends BasicModel {
    @NotNull
    private String longitude;

    @NotNull
    private String latitude;

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
