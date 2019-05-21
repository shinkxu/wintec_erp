package erp.chain.model.online.onlineconfig;

import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.GsonUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

/**
 * Created by liuyandong on 2018-04-19.
 */
public class SaveHomePageConfigModel extends BasicModel {

    @NotNull
    private BigInteger tenantId;//商户id

    @NotNull
    private BigInteger branchId;//机构id

    private List<SwiperInfo> swiperInfos;

    private List<ButtonInfo> buttonInfos;
    private List<BigInteger> deleteSwiperIds;
    private List<BigInteger> deleteButtonIds;

    @NotNull
    private VipSettingInfo vipSettingInfo;

    @NotNull
    private DetailSettingInfo detailSettingInfo;
    @NotEmpty
    private List<TabbarInfo> tabbarInfos;
    private List<BigInteger> deleteTabbarIds;

    public BigInteger getTenantId() {
        return tenantId;
    }

    public void setTenantId(BigInteger tenantId) {
        this.tenantId = tenantId;
    }

    public BigInteger getBranchId() {
        return branchId;
    }

    public void setBranchId(BigInteger branchId) {
        this.branchId = branchId;
    }

    public List<SwiperInfo> getSwiperInfos() {
        return swiperInfos;
    }

    public void setSwiperInfos(List<SwiperInfo> swiperInfos) {
        this.swiperInfos = swiperInfos;
    }

    public void setSwiperInfos(String swiperInfos) {
        ApplicationHandler.notBlank(swiperInfos, "swiperInfos");
        this.swiperInfos = GsonUtils.jsonToList(swiperInfos, SwiperInfo.class);
    }

    public List<ButtonInfo> getButtonInfos() {
        return buttonInfos;
    }

    public void setButtonInfos(List<ButtonInfo> buttonInfos) {
        this.buttonInfos = buttonInfos;
    }

    public void setButtonInfos(String buttonInfos) {
        ApplicationHandler.notBlank(buttonInfos, "swiperInfos");
        this.buttonInfos = GsonUtils.jsonToList(buttonInfos, ButtonInfo.class);
    }

    public List<BigInteger> getDeleteSwiperIds() {
        return deleteSwiperIds;
    }

    public void setDeleteSwiperIds(List<BigInteger> deleteSwiperIds) {
        this.deleteSwiperIds = deleteSwiperIds;
    }

    public List<BigInteger> getDeleteButtonIds() {
        return deleteButtonIds;
    }

    public void setDeleteButtonIds(List<BigInteger> deleteButtonIds) {
        this.deleteButtonIds = deleteButtonIds;
    }

    public VipSettingInfo getVipSettingInfo() {
        return vipSettingInfo;
    }

    public void setVipSettingInfo(VipSettingInfo vipSettingInfo) {
        this.vipSettingInfo = vipSettingInfo;
    }

    public DetailSettingInfo getDetailSettingInfo() {
        return detailSettingInfo;
    }

    public void setDetailSettingInfo(DetailSettingInfo detailSettingInfo) {
        this.detailSettingInfo = detailSettingInfo;
    }

    public List<TabbarInfo> getTabbarInfos() {
        return tabbarInfos;
    }

    public void setTabbarInfos(List<TabbarInfo> tabbarInfos) {
        this.tabbarInfos = tabbarInfos;
    }

    public List<BigInteger> getDeleteTabbarIds() {
        return deleteTabbarIds;
    }

    public void setDeleteTabbarIds(List<BigInteger> deleteTabbarIds) {
        this.deleteTabbarIds = deleteTabbarIds;
    }

    @Override
    public void validateAndThrow() {
        super.validateAndThrow();
        for (SwiperInfo swiperInfo : swiperInfos) {
            ApplicationHandler.isTrue(swiperInfo.validate(), "swiperInfos");
        }
        for (ButtonInfo buttonInfo : buttonInfos) {
            ApplicationHandler.isTrue(buttonInfo.validate(), "buttonInfo");
        }
        ApplicationHandler.isTrue(vipSettingInfo.validate(), "vipSettingInfo");
        ApplicationHandler.isTrue(detailSettingInfo.validate(), "detailSettingInfo");

        for (TabbarInfo tabbarInfo : tabbarInfos) {
            ApplicationHandler.isTrue(tabbarInfo.validate(), "tabbarInfos");
        }
    }

    public static class SwiperInfo extends BasicModel {
        private BigInteger id;
        @NotNull
        private Integer swiperIndex;

        @NotNull
        private Integer type;

        @NotNull
        private String imageUrl;

        private String jumpUrl;

        private Integer functionType;

        @Length(max = 200)
        private String customPage;

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public Integer getSwiperIndex() {
            return swiperIndex;
        }

        public void setSwiperIndex(Integer swiperIndex) {
            this.swiperIndex = swiperIndex;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getJumpUrl() {
            return jumpUrl;
        }

        public void setJumpUrl(String jumpUrl) {
            this.jumpUrl = jumpUrl;
        }

        public Integer getFunctionType() {
            return functionType;
        }

        public void setFunctionType(Integer functionType) {
            this.functionType = functionType;
        }

        public String getCustomPage() {
            return customPage;
        }

        public void setCustomPage(String customPage) {
            this.customPage = customPage;
        }

        @Override
        public boolean validate() {
            boolean isValidate = super.validate();
            if (!isValidate) {
                return false;
            }
            if (type == 2) {
                if (StringUtils.isBlank(jumpUrl)) {
                    return false;
                }
            }
            if (type == 3) {
                if (functionType == null) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class ButtonInfo extends BasicModel {
        private BigInteger id;
        @NotNull
        private Integer buttonIndex;

        @NotNull
        @Length(max = 10)
        private String name;

        @NotNull
        private Integer type;

        private String jumpUrl;

        private Integer functionType;

        @Length(max = 200)
        private String customPage;

        @NotNull
        private Integer backgroundType;

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public Integer getButtonIndex() {
            return buttonIndex;
        }

        public void setButtonIndex(Integer buttonIndex) {
            this.buttonIndex = buttonIndex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getJumpUrl() {
            return jumpUrl;
        }

        public void setJumpUrl(String jumpUrl) {
            this.jumpUrl = jumpUrl;
        }

        public Integer getFunctionType() {
            return functionType;
        }

        public void setFunctionType(Integer functionType) {
            this.functionType = functionType;
        }

        public String getCustomPage() {
            return customPage;
        }

        public void setCustomPage(String customPage) {
            this.customPage = customPage;
        }

        public Integer getBackgroundType() {
            return backgroundType;
        }

        public void setBackgroundType(Integer backgroundType) {
            this.backgroundType = backgroundType;
        }

        @Override
        public boolean validate() {
            boolean isValidate = super.validate();
            if (!isValidate) {
                return false;
            }
            if (type == 1) {
                if (StringUtils.isBlank(jumpUrl)) {
                    return false;
                }
            }
            if (type == 2) {
                if (functionType == null) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class VipSettingInfo extends BasicModel {
        @NotNull
        private Boolean displayed;

        private List<Integer> displayItems;

        public Boolean getDisplayed() {
            return displayed;
        }

        public void setDisplayed(Boolean displayed) {
            this.displayed = displayed;
        }

        public List<Integer> getDisplayItems() {
            return displayItems;
        }

        public void setDisplayItems(List<Integer> displayItems) {
            this.displayItems = displayItems;
        }
    }

    public static class DetailSettingInfo extends BasicModel {
        @NotNull
        private Integer displayType;
        private List<Integer> displayItems;

        public Integer getDisplayType() {
            return displayType;
        }

        public void setDisplayType(Integer displayType) {
            this.displayType = displayType;
        }

        public List<Integer> getDisplayItems() {
            return displayItems;
        }

        public void setDisplayItems(List<Integer> displayItems) {
            this.displayItems = displayItems;
        }
    }

    public static class TabbarInfo extends BasicModel {
        private BigInteger id;
        @NotNull
        private Integer tabbarIndex;
        @NotNull
        @Length(max = 20)
        private String name;

        @NotNull
        private Integer functionType;

        @Length(max = 200)
        private String customPage;

        @NotNull
        private Integer iconType;

        public BigInteger getId() {
            return id;
        }

        public void setId(BigInteger id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getFunctionType() {
            return functionType;
        }

        public void setFunctionType(Integer functionType) {
            this.functionType = functionType;
        }

        public String getCustomPage() {
            return customPage;
        }

        public void setCustomPage(String customPage) {
            this.customPage = customPage;
        }

        public Integer getIconType() {
            return iconType;
        }

        public void setIconType(Integer iconType) {
            this.iconType = iconType;
        }

        public Integer getTabbarIndex() {
            return tabbarIndex;
        }

        public void setTabbarIndex(Integer tabbarIndex) {
            this.tabbarIndex = tabbarIndex;
        }
    }
}
