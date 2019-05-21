package erp.chain.service.o2o;

import com.saas.common.util.Common;
import com.saas.common.util.PropertyUtils;
import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.domain.o2o.AlipayMarketingCardTemplate;
import erp.chain.domain.o2o.Vip;
import erp.chain.mapper.BranchMapper;
import erp.chain.mapper.o2o.AlipayMarketingCardTemplateMapper;
import erp.chain.mapper.o2o.VipMapper;
import erp.chain.utils.GsonUntil;
import erp.chain.utils.SearchModel;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by liuyandong on 2018-01-22.
 */
@Service
public class AlipayVipService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private AlipayMarketingCardTemplateMapper alipayMarketingCardTemplateMapper;
    @Autowired
    private VipMapper vipMapper;

    @Transactional(rollbackFor = Exception.class)
    public ApiRest createCardTemplate(BigInteger tenantId, BigInteger branchId) throws IOException {
        Branch headquartersBranch = branchMapper.findHeadquartersBranch(tenantId);
        String headquartersBranchId = headquartersBranch.getId().toString();

        Map<String, String> alipayMarketingCardTemplateCreateRequestParameters = new HashMap<String, String>();
        alipayMarketingCardTemplateCreateRequestParameters.put("tenantId", tenantId.toString());
        alipayMarketingCardTemplateCreateRequestParameters.put("branchId", branchId.toString());
        alipayMarketingCardTemplateCreateRequestParameters.put("headquartersBranchId", headquartersBranchId);
        alipayMarketingCardTemplateCreateRequestParameters.put("requestId", RandomStringUtils.randomAlphabetic(32));
        alipayMarketingCardTemplateCreateRequestParameters.put("cardType", "OUT_MEMBER_CARD");
        alipayMarketingCardTemplateCreateRequestParameters.put("writeOffType", "qrcode");

        Map<String, Object> templateStyleInfo = new HashMap<String, Object>();
        templateStyleInfo.put("cardShowName", "会员卡");
        templateStyleInfo.put("logoId", "1T8Pp00AT7eo9NoAJkMR3AAAACMAAQEC");
        templateStyleInfo.put("backgroundId", "AAFcxlr4SfG1n6XZZAC0DQAAACMAAQED");
        templateStyleInfo.put("bgColor", "rgb(55,112,179)");

        String partitionCode = PropertyUtils.getDefault(Constants.PARTITION_CODE);
        String outsideServiceDomain = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_CT, partitionCode);

        alipayMarketingCardTemplateCreateRequestParameters.put("templateStyleInfo", GsonUntil.toJson(templateStyleInfo));
        List<Map<String, Object>> columnInfoList = new ArrayList<Map<String, Object>>();
        Map<String, Object> receiveCouponColumnInfo = new HashMap<String, Object>();
        receiveCouponColumnInfo.put("code", "receive_coupon");
        receiveCouponColumnInfo.put("operateType", "openWeb");
        receiveCouponColumnInfo.put("title", "领取优惠券");
        receiveCouponColumnInfo.put("value", "");
        Map<String, Object> receiveCouponsColumnInfoMoreInfo = new HashMap<String, Object>();
        receiveCouponsColumnInfoMoreInfo.put("title", "领取优惠券");
        receiveCouponsColumnInfoMoreInfo.put("url", outsideServiceDomain + "/alipayVip/receiveCoupon");
        Map<String, Object> receiveCouponsColumnInfoMoreInfoParams = new HashMap<String, Object>();
        receiveCouponsColumnInfoMoreInfoParams.put("tenantId", tenantId.toString());
        receiveCouponsColumnInfoMoreInfoParams.put("branchId", branchId.toString());
        receiveCouponsColumnInfoMoreInfo.put("params", GsonUntil.toJson(receiveCouponsColumnInfoMoreInfoParams));
        receiveCouponColumnInfo.put("moreInfo", receiveCouponsColumnInfoMoreInfo);
        columnInfoList.add(receiveCouponColumnInfo);

        Map<String, Object> myCouponColumnInfo = new HashMap<String, Object>();
        myCouponColumnInfo.put("code", "my_coupon");
        myCouponColumnInfo.put("operateType", "openWeb");
        myCouponColumnInfo.put("title", "我的优惠券");
        myCouponColumnInfo.put("value", "");
        Map<String, Object> myCouponColumnInfoMoreInfo = new HashMap<String, Object>();
        myCouponColumnInfoMoreInfo.put("title", "我的优惠券");
        myCouponColumnInfoMoreInfo.put("url", outsideServiceDomain + "/alipayVip/myCoupon");
        Map<String, Object> myCouponColumnInfoMoreInfoParams = new HashMap<String, Object>();
        myCouponColumnInfoMoreInfoParams.put("tenantId", tenantId.toString());
        myCouponColumnInfoMoreInfoParams.put("branchId", branchId.toString());
        myCouponColumnInfoMoreInfo.put("params", GsonUntil.toJson(myCouponColumnInfoMoreInfoParams));
        myCouponColumnInfo.put("moreInfo", myCouponColumnInfoMoreInfo);
        columnInfoList.add(myCouponColumnInfo);

        Map<String, Object> vipCardDetailInfo = new HashMap<String, Object>();
        vipCardDetailInfo.put("code", "vip_card_detail");
        vipCardDetailInfo.put("operateType", "openWeb");
        vipCardDetailInfo.put("title", "会员卡详情");
        vipCardDetailInfo.put("value", "");
        Map<String, Object> vipCardDetailInfoMoreInfo = new HashMap<String, Object>();
        vipCardDetailInfoMoreInfo.put("title", "会员卡详情");
        vipCardDetailInfoMoreInfo.put("url", outsideServiceDomain + "/alipayVip/vipCardInfo");
        Map<String, Object> vipCardDetailInfoColumnInfoMoreInfoParams = new HashMap<String, Object>();
        vipCardDetailInfoColumnInfoMoreInfoParams.put("tenantId", tenantId.toString());
        vipCardDetailInfoColumnInfoMoreInfoParams.put("branchId", branchId.toString());
        vipCardDetailInfoMoreInfo.put("params", GsonUntil.toJson(vipCardDetailInfoColumnInfoMoreInfoParams));
        vipCardDetailInfo.put("moreInfo", vipCardDetailInfoMoreInfo);
        columnInfoList.add(vipCardDetailInfo);

        alipayMarketingCardTemplateCreateRequestParameters.put("columnInfoList", GsonUntil.toJson(columnInfoList));
//        alipayMarketingCardTemplateCreateRequestParameters.put("columnInfoList", "[{\"code\": \"lingquyouhuiquan\", \"operate_type\": \"openWeb\", \"title\": \"领取优惠券\", \"value\": \"\", moreInfo: {\"title\": \"领取优惠券\", \"url\": \"http://www.baidu.com\"}},{\"code\": \"lingquyouhuiquan\", \"operate_type\": \"openWeb\", \"title\": \"我的优惠券\", \"value\": \"\", moreInfo: {\"title\": \"我的优惠券\", \"url\": \"http://www.baidu.com\"}},{\"code\": \"lingquyouhuiquan\", \"operate_type\": \"openWeb\", \"title\": \"会员卡详情\", \"value\": \"\", moreInfo: {\"title\": \"会员卡详情\", \"url\": \"http://www.baidu.com\"}}]");
        alipayMarketingCardTemplateCreateRequestParameters.put("fieldRuleList", "[{\"fieldName\": \"OpenDate\", \"ruleName\": \"DATE_IN_FUTURE\", \"ruleValue\": \"10m\"}]");
        alipayMarketingCardTemplateCreateRequestParameters.put("bizNoSuffixLen", "10");
        alipayMarketingCardTemplateCreateRequestParameters.put("pubChannels", "[{\"pubChannel\": \"PAYMENT_RESULT\", \"extInfo\": \"\"}]");
//        alipayMarketingCardTemplateCreateRequestParameters.put("cardActionList", "[{\"code\": \"nihao\", \"text\": \"你好\", \"url\": \"http://www.baidu.com\"}]");
        ApiRest alipayMarketingCardTemplateCreateApiRest = ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT, "alipay", "alipayMarketingCardTemplateCreate", alipayMarketingCardTemplateCreateRequestParameters);
        Validate.isTrue(alipayMarketingCardTemplateCreateApiRest.getIsSuccess(), alipayMarketingCardTemplateCreateApiRest.getError());

        Map<String, Object> alipayMarketingCardTemplateCreateApiRestData = (Map<String, Object>) alipayMarketingCardTemplateCreateApiRest.getData();

        String templateId = alipayMarketingCardTemplateCreateApiRestData.get("templateId").toString();

        Map<String, String> alipayMarketingCardFormTemplateSetRequestParameters = new HashMap<String, String>();
        alipayMarketingCardFormTemplateSetRequestParameters.put("tenantId", tenantId.toString());
        alipayMarketingCardFormTemplateSetRequestParameters.put("branchId", branchId.toString());
        alipayMarketingCardFormTemplateSetRequestParameters.put("headquartersBranchId", headquartersBranchId);
        alipayMarketingCardFormTemplateSetRequestParameters.put("fields", "{\"required\":{\"common_fields\":[\"OPEN_FORM_FIELD_MOBILE\"]}}");
        alipayMarketingCardFormTemplateSetRequestParameters.put("templateId", templateId);
        ApiRest alipayMarketingCardFormTemplateSetApiRest = ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT, "alipay", "alipayMarketingCardFormTemplateSet", alipayMarketingCardFormTemplateSetRequestParameters);
        Validate.isTrue(alipayMarketingCardFormTemplateSetApiRest.getIsSuccess(), alipayMarketingCardFormTemplateSetApiRest.getError());

        Map<String, String> alipayMarketingCardActivateUrlApplyRequestParameters = new HashMap<String, String>();
        alipayMarketingCardActivateUrlApplyRequestParameters.put("tenantId", tenantId.toString());
        alipayMarketingCardActivateUrlApplyRequestParameters.put("branchId", branchId.toString());
        alipayMarketingCardActivateUrlApplyRequestParameters.put("headquartersBranchId", headquartersBranchId);
        alipayMarketingCardActivateUrlApplyRequestParameters.put("templateId", templateId);

        alipayMarketingCardActivateUrlApplyRequestParameters.put("callback", outsideServiceDomain + "/alipayVip/alipayMarketingCardOpen");
        alipayMarketingCardActivateUrlApplyRequestParameters.put("outString", tenantId + "Z" + branchId + "Z" + headquartersBranchId);
        ApiRest alipayMarketingCardActivateUrlApplyApiRest = ProxyApi.proxyPost(Constants.SERVICE_NAME_OUT, "alipay", "alipayMarketingCardActivateUrlApply", alipayMarketingCardActivateUrlApplyRequestParameters);
        Validate.isTrue(alipayMarketingCardActivateUrlApplyApiRest.getIsSuccess(), alipayMarketingCardActivateUrlApplyApiRest.getError());
        Map<String, Object> alipayMarketingCardActivateUrlApplyApiRestData = (Map<String, Object>) alipayMarketingCardActivateUrlApplyApiRest.getData();

        String applyCardUrl = URLDecoder.decode(alipayMarketingCardActivateUrlApplyApiRestData.get("applyCardUrl").toString(), Constants.CHARSET_UTF8);

        AlipayMarketingCardTemplate alipayMarketingCardTemplate = new AlipayMarketingCardTemplate();
        alipayMarketingCardTemplate.setTenantId(tenantId);
        alipayMarketingCardTemplate.setBranchId(branchId);
        alipayMarketingCardTemplate.setTemplateId(templateId);
        alipayMarketingCardTemplate.setApplyCardUrl(applyCardUrl);
        alipayMarketingCardTemplate.setCreateAt(new Date());
        alipayMarketingCardTemplate.setLastUpdateAt(new Date());
        alipayMarketingCardTemplateMapper.insert(alipayMarketingCardTemplate);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(alipayMarketingCardTemplate);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("会员卡模板创建成功！");
        return apiRest;
    }

    @Transactional(readOnly = true)
    public ApiRest obtainAlipayMarketingCardTemplate(BigInteger tenantId, BigInteger branchId) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        AlipayMarketingCardTemplate alipayMarketingCardTemplate = alipayMarketingCardTemplateMapper.find(searchModel);

        ApiRest apiRest = new ApiRest();
        apiRest.setData(alipayMarketingCardTemplate);
        apiRest.setMessage("查询支付宝会员模板成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    public ApiRest obtainVipInfo(BigInteger tenantId, BigInteger branchId, String vipCode) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        searchModel.addSearchCondition("vip_code", Constants.SQL_OPERATION_SYMBOL_EQUALS, vipCode);

        Vip vip = vipMapper.find(searchModel);
        Validate.notNull(vip, "会员不存在！");

        ApiRest apiRest = new ApiRest();
        apiRest.setData(vip);
        apiRest.setMessage("查询会员信息成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }
}
