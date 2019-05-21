package erp.chain.beans.weixin;

public class WeiXinAuthorizerInfo {
    /**
     * 授权方昵称
     */
    private String nickName;
    /**
     * 授权方公众号的原始ID
     */
    private String originalId;
    /**
     * app id
     */
    private String authorizerAppId;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getAuthorizerAppId() {
        return authorizerAppId;
    }

    public void setAuthorizerAppId(String authorizerAppId) {
        this.authorizerAppId = authorizerAppId;
    }
}
