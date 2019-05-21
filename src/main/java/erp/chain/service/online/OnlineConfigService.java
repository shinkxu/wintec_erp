package erp.chain.service.online;

import erp.chain.common.Constants;
import erp.chain.domain.online.*;
import erp.chain.model.online.onlineconfig.ListModel;
import erp.chain.model.online.onlineconfig.ObtainTabBarsModel;
import erp.chain.model.online.onlineconfig.SaveHomePageConfigModel;
import erp.chain.utils.DatabaseHelper;
import erp.chain.utils.SearchModel;
import erp.chain.utils.UpdateModel;
import erp.chain.utils.ValidateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-19.
 */
@Service
public class OnlineConfigService {
    /**
     * 保存主页配置
     *
     * @param saveHomePageConfigModel
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveHomePageConfig(SaveHomePageConfigModel saveHomePageConfigModel) throws Exception {
        BigInteger tenantId = saveHomePageConfigModel.getTenantId();
        BigInteger branchId = saveHomePageConfigModel.getBranchId();
        List<SaveHomePageConfigModel.SwiperInfo> swiperInfos = saveHomePageConfigModel.getSwiperInfos();
        List<SaveHomePageConfigModel.ButtonInfo> buttonInfos = saveHomePageConfigModel.getButtonInfos();
        List<BigInteger> deleteSwiperIds = saveHomePageConfigModel.getDeleteSwiperIds();
        List<BigInteger> deleteButtonIds = saveHomePageConfigModel.getDeleteButtonIds();
        SaveHomePageConfigModel.VipSettingInfo vipSettingInfo = saveHomePageConfigModel.getVipSettingInfo();
        SaveHomePageConfigModel.DetailSettingInfo detailSettingInfo = saveHomePageConfigModel.getDetailSettingInfo();
        List<SaveHomePageConfigModel.TabbarInfo> tabbarInfos = saveHomePageConfigModel.getTabbarInfos();
        List<BigInteger> deleteTabbarIds = saveHomePageConfigModel.getDeleteTabbarIds();

        Date date = new Date();
        if (CollectionUtils.isNotEmpty(swiperInfos)) {
            SearchModel miniProgramSwiperSearchModel = new SearchModel(true);
            miniProgramSwiperSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            miniProgramSwiperSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            List<MiniProgramSwiper> miniProgramSwipers = DatabaseHelper.findAll(MiniProgramSwiper.class, miniProgramSwiperSearchModel);
            Map<BigInteger, MiniProgramSwiper> miniProgramSwiperMap = new HashMap<BigInteger, MiniProgramSwiper>();
            for (MiniProgramSwiper miniProgramSwiper : miniProgramSwipers) {
                miniProgramSwiperMap.put(miniProgramSwiper.getId(), miniProgramSwiper);
            }

            for (SaveHomePageConfigModel.SwiperInfo swiperInfo : swiperInfos) {
                BigInteger id = swiperInfo.getId();
                if (id == null) {
                    MiniProgramSwiper miniProgramSwiper = new MiniProgramSwiper();
                    miniProgramSwiper.setTenantId(tenantId);
                    miniProgramSwiper.setBranchId(branchId);
                    miniProgramSwiper.setSwiperIndex(swiperInfo.getSwiperIndex());
                    miniProgramSwiper.setImageUrl(swiperInfo.getImageUrl());
                    miniProgramSwiper.setType(swiperInfo.getType());
                    miniProgramSwiper.setJumpUrl(swiperInfo.getJumpUrl());
                    miniProgramSwiper.setFunctionType(swiperInfo.getFunctionType());
                    miniProgramSwiper.setCustomPage(swiperInfo.getCustomPage());
                    miniProgramSwiper.setCreateAt(date);
                    miniProgramSwiper.setLastUpdateAt(date);
                    miniProgramSwiper.setIsDeleted(false);
                    DatabaseHelper.insert(miniProgramSwiper);
                } else {
                    MiniProgramSwiper miniProgramSwiper = miniProgramSwiperMap.get(id);
                    ValidateUtils.notNull(miniProgramSwiper, "轮播图不存在！");
                    miniProgramSwiper.setSwiperIndex(swiperInfo.getSwiperIndex());
                    miniProgramSwiper.setImageUrl(swiperInfo.getImageUrl());
                    miniProgramSwiper.setType(swiperInfo.getType());
                    miniProgramSwiper.setJumpUrl(swiperInfo.getJumpUrl());
                    miniProgramSwiper.setFunctionType(swiperInfo.getFunctionType());
                    miniProgramSwiper.setCustomPage(swiperInfo.getCustomPage());
                    miniProgramSwiper.setLastUpdateAt(date);
                    DatabaseHelper.update(miniProgramSwiper);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(deleteSwiperIds)) {
            UpdateModel updateModel = new UpdateModel(true);
            updateModel.setTableName("mini_program_swiper");
            updateModel.addContentValue("is_deleted", 1, "#");
            updateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deleteSwiperIds);
            DatabaseHelper.universalUpdate(updateModel);
        }

        if (CollectionUtils.isNotEmpty(buttonInfos)) {
            SearchModel miniProgramButtonSearchModel = new SearchModel(true);
            miniProgramButtonSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
            miniProgramButtonSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
            List<MiniProgramButton> miniProgramButtons = DatabaseHelper.findAll(MiniProgramButton.class, miniProgramButtonSearchModel);
            Map<BigInteger, MiniProgramButton> miniProgramButtonMap = new HashMap<BigInteger, MiniProgramButton>();
            for (MiniProgramButton miniProgramButton : miniProgramButtons) {
                miniProgramButtonMap.put(miniProgramButton.getId(), miniProgramButton);
            }

            for (SaveHomePageConfigModel.ButtonInfo buttonInfo : buttonInfos) {
                BigInteger id = buttonInfo.getId();
                if (id == null) {
                    MiniProgramButton miniProgramButton = new MiniProgramButton();
                    miniProgramButton.setTenantId(tenantId);
                    miniProgramButton.setBranchId(branchId);
                    miniProgramButton.setButtonIndex(buttonInfo.getButtonIndex());
                    miniProgramButton.setName(buttonInfo.getName());
                    miniProgramButton.setType(buttonInfo.getType());
                    miniProgramButton.setJumpUrl(buttonInfo.getJumpUrl());
                    miniProgramButton.setFunctionType(buttonInfo.getFunctionType());
                    miniProgramButton.setCustomPage(buttonInfo.getCustomPage());
                    miniProgramButton.setBackgroundType(buttonInfo.getBackgroundType());
                    miniProgramButton.setCreateAt(date);
                    miniProgramButton.setLastUpdateAt(date);
                    DatabaseHelper.insert(miniProgramButton);
                } else {
                    MiniProgramButton miniProgramButton = miniProgramButtonMap.get(id);
                    ValidateUtils.notNull(miniProgramButton, "功能按钮不存在！");
                    miniProgramButton.setButtonIndex(buttonInfo.getButtonIndex());
                    miniProgramButton.setName(buttonInfo.getName());
                    miniProgramButton.setType(buttonInfo.getType());
                    miniProgramButton.setJumpUrl(buttonInfo.getJumpUrl());
                    miniProgramButton.setFunctionType(buttonInfo.getFunctionType());
                    miniProgramButton.setCustomPage(buttonInfo.getCustomPage());
                    miniProgramButton.setBackgroundType(buttonInfo.getBackgroundType());
                    miniProgramButton.setLastUpdateAt(date);
                    DatabaseHelper.update(miniProgramButton);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(deleteButtonIds)) {
            UpdateModel updateModel = new UpdateModel(true);
            updateModel.setTableName("mini_program_button");
            updateModel.addContentValue("is_deleted", 1, "#");
            updateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deleteButtonIds);
            DatabaseHelper.universalUpdate(updateModel);
        }

        SearchModel miniProgramVipSettingSearchModel = new SearchModel(true);
        miniProgramVipSettingSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramVipSettingSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        MiniProgramVipSetting miniProgramVipSetting = DatabaseHelper.find(MiniProgramVipSetting.class, miniProgramVipSettingSearchModel);

        List<Integer> displayItems = vipSettingInfo.getDisplayItems();

        boolean displayed = vipSettingInfo.getDisplayed();
        BigInteger displayContent = BigInteger.ZERO;

        if (displayed && CollectionUtils.isNotEmpty(displayItems)) {
            displayContent = BigInteger.ONE;
            for (Integer displayItem : displayItems) {
                displayContent = displayContent.multiply(BigInteger.valueOf(displayItem));
            }
        }
        if (miniProgramVipSetting == null) {
            miniProgramVipSetting = new MiniProgramVipSetting();
            miniProgramVipSetting.setTenantId(tenantId);
            miniProgramVipSetting.setBranchId(branchId);
            miniProgramVipSetting.setDisplayed(displayed);
            miniProgramVipSetting.setDisplayContent(displayContent);
            miniProgramVipSetting.setCreateAt(date);
            miniProgramVipSetting.setLastUpdateAt(date);
            DatabaseHelper.insert(miniProgramVipSetting);
        } else {
            miniProgramVipSetting.setDisplayed(displayed);
            miniProgramVipSetting.setDisplayContent(displayContent);
            miniProgramVipSetting.setLastUpdateAt(date);
            DatabaseHelper.update(miniProgramVipSetting);
        }

        SearchModel miniProgramDetailSettingSearchModel = new SearchModel(true);
        miniProgramDetailSettingSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramDetailSettingSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        MiniProgramDetailSetting miniProgramDetailSetting = DatabaseHelper.find(MiniProgramDetailSetting.class, miniProgramDetailSettingSearchModel);

        BigInteger miniProgramDetailSettingDisplayContent = BigInteger.ZERO;
        if (CollectionUtils.isNotEmpty(detailSettingInfo.getDisplayItems())) {
            miniProgramDetailSettingDisplayContent = BigInteger.ONE;
            for (int displayItem : detailSettingInfo.getDisplayItems()) {
                miniProgramDetailSettingDisplayContent = miniProgramDetailSettingDisplayContent.multiply(BigInteger.valueOf(displayItem));
            }
        }

        if (miniProgramDetailSetting == null) {
            miniProgramDetailSetting = new MiniProgramDetailSetting();
            miniProgramDetailSetting.setTenantId(tenantId);
            miniProgramDetailSetting.setBranchId(branchId);
            miniProgramDetailSetting.setDisplayType(detailSettingInfo.getDisplayType());
            miniProgramDetailSetting.setDisplayContent(miniProgramDetailSettingDisplayContent);
            miniProgramDetailSetting.setCreateAt(date);
            miniProgramDetailSetting.setLastUpdateAt(date);
            DatabaseHelper.insert(miniProgramDetailSetting);
        } else {
            miniProgramDetailSetting.setDisplayType(detailSettingInfo.getDisplayType());
            miniProgramDetailSetting.setDisplayContent(miniProgramDetailSettingDisplayContent);
            miniProgramDetailSetting.setLastUpdateAt(date);
            DatabaseHelper.update(miniProgramDetailSetting);
        }


        SearchModel miniProgramTabbarSearchModel = new SearchModel(true);
        miniProgramTabbarSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramTabbarSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<MiniProgramTabbar> miniProgramTabbars = DatabaseHelper.findAll(MiniProgramTabbar.class, miniProgramTabbarSearchModel);
        Map<BigInteger, MiniProgramTabbar> miniProgramTabbarMap = new HashMap<BigInteger, MiniProgramTabbar>();
        for (MiniProgramTabbar miniProgramTabbar : miniProgramTabbars) {
            miniProgramTabbarMap.put(miniProgramTabbar.getId(), miniProgramTabbar);
        }

        for (SaveHomePageConfigModel.TabbarInfo tabbarInfo : tabbarInfos) {
            BigInteger id = tabbarInfo.getId();
            if (id == null) {
                MiniProgramTabbar miniProgramTabbar = new MiniProgramTabbar();
                miniProgramTabbar.setTenantId(tenantId);
                miniProgramTabbar.setBranchId(branchId);
                miniProgramTabbar.setName(tabbarInfo.getName());
                miniProgramTabbar.setFunctionType(tabbarInfo.getFunctionType());
                miniProgramTabbar.setCustomPage(tabbarInfo.getCustomPage());
                miniProgramTabbar.setIconType(tabbarInfo.getIconType());
                miniProgramTabbar.setTabbarIndex(tabbarInfo.getTabbarIndex());
                miniProgramTabbar.setCreateAt(date);
                miniProgramTabbar.setLastUpdateAt(date);
                DatabaseHelper.insert(miniProgramTabbar);
            } else {
                MiniProgramTabbar miniProgramTabbar = miniProgramTabbarMap.get(id);
                ValidateUtils.notNull(miniProgramTabbar, "导航不存在！");
                miniProgramTabbar.setTabbarIndex(tabbarInfo.getTabbarIndex());
                miniProgramTabbar.setName(tabbarInfo.getName());
                miniProgramTabbar.setFunctionType(tabbarInfo.getFunctionType());
                miniProgramTabbar.setCustomPage(tabbarInfo.getCustomPage());
                miniProgramTabbar.setIconType(tabbarInfo.getIconType());
                miniProgramTabbar.setLastUpdateAt(date);
                DatabaseHelper.update(miniProgramTabbar);
            }
        }

        if (CollectionUtils.isNotEmpty(deleteTabbarIds)) {
            UpdateModel updateModel = new UpdateModel(true);
            updateModel.setTableName("mini_program_tabbar");
            updateModel.addContentValue("is_deleted", 1, "#");
            updateModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, deleteTabbarIds);
            DatabaseHelper.universalUpdate(updateModel);
        }

        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存成功");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 查询首页配置
     *
     * @param listModel
     * @return
     */
    public ApiRest list(ListModel listModel) {
        BigInteger tenantId = listModel.getTenantId();
        BigInteger branchId = listModel.getBranchId();

        SearchModel miniProgramSwiperSearchModel = new SearchModel(true);
        miniProgramSwiperSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramSwiperSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<MiniProgramSwiper> miniProgramSwipers = DatabaseHelper.findAll(MiniProgramSwiper.class, miniProgramSwiperSearchModel);

        SearchModel miniProgramButtonSearchModel = new SearchModel(true);
        miniProgramButtonSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramButtonSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<MiniProgramButton> miniProgramButtons = DatabaseHelper.findAll(MiniProgramButton.class, miniProgramButtonSearchModel);

        SearchModel miniProgramVipSettingSearchModel = new SearchModel(true);
        miniProgramVipSettingSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramVipSettingSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        MiniProgramVipSetting miniProgramVipSetting = DatabaseHelper.find(MiniProgramVipSetting.class, miniProgramVipSettingSearchModel);

        SearchModel miniProgramDetailSettingSearchModel = new SearchModel(true);
        miniProgramDetailSettingSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramDetailSettingSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        MiniProgramDetailSetting miniProgramDetailSetting = DatabaseHelper.find(MiniProgramDetailSetting.class, miniProgramDetailSettingSearchModel);

        SearchModel miniProgramTabbarSearchModel = new SearchModel(true);
        miniProgramTabbarSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramTabbarSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        miniProgramTabbarSearchModel.setOrderBy("tabbar_index ASC");
        List<MiniProgramTabbar> miniProgramTabbars = DatabaseHelper.findAll(MiniProgramTabbar.class, miniProgramTabbarSearchModel);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("swipers", miniProgramSwipers);
        data.put("buttons", miniProgramButtons);
        data.put("vipSetting", miniProgramVipSetting);
        data.put("detailSetting", miniProgramDetailSetting);
        data.put("tabbars", miniProgramTabbars);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(data);
        apiRest.setMessage("查询成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainTabBars(ObtainTabBarsModel obtainTabBarsModel) {
        BigInteger tenantId = obtainTabBarsModel.getTenantId();
        BigInteger branchId = obtainTabBarsModel.getBranchId();

        SearchModel miniProgramTabbarSearchModel = new SearchModel(true);
        miniProgramTabbarSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        miniProgramTabbarSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<MiniProgramTabbar> miniProgramTabbars = DatabaseHelper.findAll(MiniProgramTabbar.class, miniProgramTabbarSearchModel);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(miniProgramTabbars);
        apiRest.setMessage("获取底部导航成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
