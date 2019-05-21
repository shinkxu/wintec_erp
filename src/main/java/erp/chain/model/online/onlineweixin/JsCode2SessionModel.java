package erp.chain.model.online.onlineweixin;

import erp.chain.model.online.BasicModel;

import javax.validation.constraints.NotNull;

/**
 * Created by liuyandong on 2018-04-11.
 */
public class JsCode2SessionModel extends BasicModel {
    @NotNull
    private String appId;

    @NotNull
    private String jsCode;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getJsCode() {
        return jsCode;
    }

    public void setJsCode(String jsCode) {
        this.jsCode = jsCode;
    }
}
