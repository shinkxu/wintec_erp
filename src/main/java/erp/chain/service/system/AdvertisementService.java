package erp.chain.service.system;

import erp.chain.domain.system.AdBanner;
import erp.chain.domain.system.AdContent;
import erp.chain.mapper.system.AdvertisementMapper;
import erp.chain.utils.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能模块说明
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2018/11/2
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AdvertisementService{
    @Autowired
    private AdvertisementMapper advertisementMapper;

    public ApiRest listAdBanner(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        Integer page = 1;
        Integer rows = 20;
        if(StringUtils.isNotEmpty(params.get("page"))){
            page = Integer.valueOf(params.get("page"));
        }
        if(StringUtils.isNotEmpty(params.get("rows"))){
            rows = Integer.valueOf(params.get("rows"));
        }
        Integer offset = (page - 1) * rows;
        params.put("offset", offset.toString());
        if(params.get("noPage") != null && params.get("noPage").equals("1")){
            params.remove("page");
            params.remove("rows");
            params.remove("offset");
        }
        List<AdBanner> adBannerList = advertisementMapper.listAdBanner(params);
        long count = advertisementMapper.countListAdBanner(params);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", adBannerList);
        resultMap.put("total", count);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询广告位成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    public ApiRest listAdContent(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        Integer page = 1;
        Integer rows = 20;
        if(StringUtils.isNotEmpty(params.get("page"))){
            page = Integer.valueOf(params.get("page"));
        }
        if(StringUtils.isNotEmpty(params.get("rows"))){
            rows = Integer.valueOf(params.get("rows"));
        }
        if(params.get("field") != null && !params.get("field").equals("") && params.get("order") != null && !params.get("order").equals("")){
            params.put("field", CommonUtils.formatOrderField(params.get("field")));
            params.put("order", params.get("order"));
        }
        Integer offset = (page - 1) * rows;
        params.put("offset", offset.toString());
        if(params.get("noPage") != null && params.get("noPage").equals("1")){
            params.remove("page");
            params.remove("rows");
            params.remove("offset");
        }
        List<Map> contentList = advertisementMapper.listAdContent(params);
        long count = advertisementMapper.countListAdContent(params);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("rows", contentList);
        resultMap.put("total", count);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询广告位成功！");
        apiRest.setData(resultMap);
        return apiRest;
    }

    public ApiRest bannerDetail(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        String bannerId = params.get("bannerId");
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        AdBanner adBanner = advertisementMapper.findBannerById(bannerId, tenantId, branchId);
        if(adBanner != null){
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询广告位详情成功！");
            apiRest.setData(adBanner);
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("查询广告位详情失败！");
        }
        return apiRest;
    }

    public ApiRest contentDetail(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        String contentId = params.get("contentId");
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        Map<String, Object> resultMap = new HashMap<>();
        Map content = advertisementMapper.findContentById(contentId, tenantId, branchId);
        if(content != null){
            List<Map> branchMap = advertisementMapper.contentBranchR(BigInteger.valueOf(Long.valueOf(content.get("id").toString())), BigInteger.valueOf(Long.valueOf(tenantId)));
            String branchIds = "";
            String branchNames = "";
            if(branchMap.size() > 0){
                for(Map map : branchMap){
                    branchIds += (map.get("branchId").toString() + ",");
                    branchNames += (map.get("branchName").toString() + ",");
                }
                if(branchIds.length() > 0 && branchIds.substring(branchIds.length() - 1, branchIds.length()).equals(",")){
                    branchIds = branchIds.substring(0, branchIds.length() - 1);
                }
                if(branchNames.length() > 0 && branchNames.substring(branchNames.length() - 1, branchNames.length()).equals(",")){
                    branchNames = branchNames.substring(0, branchNames.length() - 1);
                }
            }
            resultMap.put("branchIds", branchIds);
            resultMap.put("branchNames", branchNames);
            resultMap.put("content", content);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("查询广告详情成功！");
            apiRest.setData(resultMap);
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setError("查询广告详情失败！");
        }
        return apiRest;
    }

    public ApiRest deleteBanner(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        String bannerId = params.get("bannerId");
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        String empId = params.get("empId");
        AdBanner adBanner = advertisementMapper.findBannerById(bannerId, tenantId, branchId);
        if(adBanner != null){
            long count = advertisementMapper.countAdContentListByBannerId(bannerId, tenantId);
            if(count > 0){
                apiRest.setIsSuccess(false);
                apiRest.setError("广告位已被使用，无法删除");
                return apiRest;
            }
            advertisementMapper.deleteBanner(bannerId, tenantId, branchId, empId);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除广告位成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setMessage("未查询到广告位信息！");
        }
        return apiRest;
    }

    public ApiRest deleteContent(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        String contentId = params.get("contentId");
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        String empId = params.get("empId");
        Map adContent = advertisementMapper.findContentById(contentId, tenantId, branchId);
        if(adContent != null){
            advertisementMapper.deleteContent(contentId, tenantId, branchId, empId);
            advertisementMapper.deleteContentBranchR(BigInteger.valueOf(Long.valueOf(adContent.get("id").toString())), BigInteger.valueOf(Long.valueOf(tenantId)));
            apiRest.setIsSuccess(true);
            apiRest.setMessage("删除广告成功！");
        }
        else{
            apiRest.setIsSuccess(false);
            apiRest.setMessage("未查询到广告信息！");
        }
        return apiRest;
    }

    public ApiRest addOrUpdateBanner(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        String name = params.get("name");
        String description = params.get("description");
        String bannerType = params.get("bannerType");
        String displayMode = params.get("displayMode");
        String width = params.get("width");
        String height = params.get("height");
        String active = params.get("active");
        String sortCode = params.get("sortCode");
        String empId = params.get("empId");
        if(StringUtils.isEmpty(params.get("bannerId"))){//新增
            AdBanner adBanner = new AdBanner();
            adBanner.setTenantId(BigInteger.valueOf(Long.valueOf(tenantId)));
            adBanner.setBranchId(BigInteger.valueOf(Long.valueOf(branchId)));
            adBanner.setCreatedBy(BigInteger.valueOf(Long.valueOf(empId)));
            adBanner.setUpdatedBy(BigInteger.valueOf(Long.valueOf(empId)));
            adBanner.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            adBanner.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            adBanner.setName(name);
            adBanner.setDescription(description);
            adBanner.setBannerType(bannerType);
            adBanner.setDisplayMode(displayMode);
            adBanner.setWidth(Integer.valueOf(width));
            adBanner.setHeight(Integer.valueOf(height));
            adBanner.setActive(active.equals("true") || active.equals("1"));
            adBanner.setSortCode(Integer.valueOf(sortCode));
            advertisementMapper.insertBanner(adBanner);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增广告位成功！");
        }
        else{//修改
            String bannerId = params.get("bannerId");
            AdBanner adBanner = advertisementMapper.findBannerById(bannerId, tenantId, branchId);
            if(adBanner != null){
                adBanner.setUpdatedBy(BigInteger.valueOf(Long.valueOf(empId)));
                adBanner.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                adBanner.setName(name);
                adBanner.setDescription(description);
                adBanner.setBannerType(bannerType);
                adBanner.setDisplayMode(displayMode);
                adBanner.setWidth(Integer.valueOf(width));
                adBanner.setHeight(Integer.valueOf(height));
                adBanner.setActive(active.equals("true") || active.equals("1"));
                adBanner.setSortCode(Integer.valueOf(sortCode));
                advertisementMapper.updateBanner(adBanner);
                apiRest.setIsSuccess(true);
                apiRest.setMessage("修改广告位成功！");
            }
            else{
                apiRest.setIsSuccess(false);
                apiRest.setMessage("未查询到广告位信息！");
            }
        }
        return apiRest;
    }

    public ApiRest addOrUpdateContent(Map<String, String> params) throws ParseException{
        ApiRest apiRest = new ApiRest();
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        String empId = params.get("empId");
        String bannerId = "";
        if(StringUtils.isEmpty(params.get("bannerId"))){
            List<AdBanner> banners = advertisementMapper.listAdBanner(params);
            if(banners.size() > 0){
                bannerId = banners.get(0).getId().toString();
            }
            else{
                AdBanner adBanner = new AdBanner();
                adBanner.setTenantId(BigInteger.valueOf(Long.valueOf(tenantId)));
                adBanner.setBranchId(BigInteger.valueOf(Long.valueOf(branchId)));
                adBanner.setCreatedBy(BigInteger.valueOf(Long.valueOf(empId)));
                adBanner.setUpdatedBy(BigInteger.valueOf(Long.valueOf(empId)));
                adBanner.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                adBanner.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                adBanner.setName("pos.side.left");
                adBanner.setDescription("POS副屏左侧横幅广告");
                adBanner.setBannerType("any");
                adBanner.setDisplayMode("slide");
                adBanner.setWidth(0);
                adBanner.setHeight(0);
                adBanner.setActive(true);
                adBanner.setSortCode(1);
                advertisementMapper.insertBanner(adBanner);
                bannerId = adBanner.getId().toString();
            }
        }
        else{
            bannerId = params.get("bannerId");
        }

        String name = params.get("name");
        String mediaType = params.get("mediaType");
        String content = params.get("content") == null ? "" : params.get("content");
        String resourceUrl = params.get("resourceUrl");
        String jumptoUrl = params.get("jumptoUrl") == null ? "" : params.get("jumptoUrl");
        String beginDate = params.get("beginDate");
        String endDate = params.get("endDate");
        String plays = params.get("plays") == null ? "0" : params.get("plays");
        String hits = params.get("hits") == null ? "0" : params.get("hits");
        String sortCode = params.get("sortCode");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        AdContent adContent = new AdContent();
        if(StringUtils.isEmpty(params.get("contentId"))){//新增
            adContent.setTenantId(BigInteger.valueOf(Long.valueOf(tenantId)));
            adContent.setBranchId(BigInteger.valueOf(Long.valueOf(branchId)));
            adContent.setBannerId(BigInteger.valueOf(Long.valueOf(bannerId)));
            adContent.setCreatedBy(BigInteger.valueOf(Long.valueOf(empId)));
            adContent.setUpdatedBy(BigInteger.valueOf(Long.valueOf(empId)));
            adContent.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            adContent.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            adContent.setName(name);
            adContent.setMediaType(mediaType);
            adContent.setContent(content);
            adContent.setResourceUrl(resourceUrl);
            adContent.setJumptoUrl(jumptoUrl);
            adContent.setBeginDate(sdf.parse(beginDate));
            adContent.setEndDate(sdf.parse(endDate));
            adContent.setSortCode(Integer.valueOf(sortCode));
            adContent.setPlays(Integer.valueOf(plays));
            adContent.setHits(Integer.valueOf(hits));
            advertisementMapper.insertContent(adContent);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增广告成功！");
        }
        else{//修改
            String contentId = params.get("contentId");
            adContent = advertisementMapper.findContent(contentId, tenantId, branchId);
            if(adContent == null){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("未查询到广告信息！");
                return apiRest;
            }
            adContent.setTenantId(BigInteger.valueOf(Long.valueOf(tenantId)));
            adContent.setBranchId(BigInteger.valueOf(Long.valueOf(branchId)));
            adContent.setBannerId(BigInteger.valueOf(Long.valueOf(bannerId)));
            adContent.setUpdatedBy(BigInteger.valueOf(Long.valueOf(empId)));
            adContent.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            adContent.setName(name);
            adContent.setMediaType(mediaType);
            adContent.setContent(content);
            adContent.setResourceUrl(resourceUrl);
            adContent.setJumptoUrl(jumptoUrl);
            adContent.setBeginDate(sdf.parse(beginDate));
            adContent.setEndDate(sdf.parse(endDate));
            adContent.setSortCode(Integer.valueOf(sortCode));
            adContent.setPlays(Integer.valueOf(plays));
            adContent.setHits(Integer.valueOf(hits));
            advertisementMapper.updateContent(adContent);
            apiRest.setIsSuccess(true);
            apiRest.setMessage("修改广告成功！");
        }
        if(StringUtils.isNotEmpty(params.get("branchIds"))){
            List<String> branchIdList = Arrays.asList(params.get("branchIds").split(","));
            advertisementMapper.deleteContentBranchR(adContent.getId(), adContent.getTenantId());
            advertisementMapper.insertContentBranchR(branchIdList, adContent.getId(), adContent.getTenantId());
        }
        return apiRest;
    }

    public ApiRest loadBranchAdContent(Map<String, String> params){
        ApiRest apiRest = new ApiRest();
        String tenantId = params.get("tenantId");
        String branchId = params.get("branchId");
        List<Map> downloadData = advertisementMapper.loadBranchAdContent(branchId, tenantId);
        apiRest.setIsSuccess(true);
        apiRest.setData(downloadData);
        apiRest.setMessage("查询数据成功！");
        return apiRest;
    }
}
