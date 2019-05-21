package erp.chain.service.o2o;

import com.saas.common.Constants;
import com.saas.common.util.*;
import erp.chain.domain.o2o.WxMenu;
import erp.chain.domain.o2o.WxMenuItem;
import erp.chain.mapper.o2o.WechatMenuItemMapper;
import erp.chain.mapper.o2o.WechatMenuMapper;
import erp.chain.utils.DatabaseHelper;
import erp.chain.utils.ValidateUtils;
import erp.chain.utils.WeiXinUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.ProxyApi;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.*;

/**
 * 微信菜单
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2016/12/28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WechatMenuService {
    @Autowired
    private WechatMenuMapper wechatMenuMapper;
    @Autowired
    private WechatMenuItemMapper wechatMenuItemMapper;

    /**
     * 获取菜单列表
     *
     * @param params
     * @return
     */
    public ApiRest listWxMenu(Map params) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
        List<Map> wxMenusOne = wechatMenuMapper.listWxMenu(tenantId, "one", null, null);
        List<Map> wxMenusTwo = wechatMenuMapper.listWxMenu(tenantId, "two", null, null);
        Map map = new HashMap();
        map.put("wxMenusOne", wxMenusOne);
        map.put("wxMenusTwo", wxMenusTwo);
        apiRest.setData(map);
        apiRest.setIsSuccess(true);
        apiRest.setMessage("菜单查询成功!");
        return apiRest;
    }

    /**
     * 根据id查询一级菜单和其下面的二级菜单
     *
     * @return
     * @params params
     */
    public ApiRest findWxMenuById(Map params) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
        BigInteger menuId = BigInteger.valueOf(Long.parseLong((String) params.get("menuId")));
        WxMenu wxMenusOne = wechatMenuMapper.getWxMenuById(tenantId, menuId);
        if (wxMenusOne != null) {
            List<Map> wxMenusTwo = wechatMenuMapper.listWxMenu(tenantId, "two", wxMenusOne.getId(), null);
            Map map = new HashMap();
            map.put("wxMenuInfo", wxMenusOne);
            map.put("childMenus", wxMenusTwo);
            apiRest.setData(map);
        } else {
            apiRest.setData(null);
        }
        apiRest.setMessage("查询一级菜单及其二级菜单成功！");
        apiRest.setIsSuccess(true);
        return apiRest;
    }

    /**
     * 删除微信菜单
     *
     * @return
     * @params params
     */
    public ApiRest delWxMenu(Map params) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
        BigInteger menuId = BigInteger.valueOf(Long.parseLong((String) params.get("menuId")));
        WxMenu wxMenu = wechatMenuMapper.getWxMenuById(tenantId, menuId);
        if (wxMenu == null) {
            apiRest.setIsSuccess(false);
            apiRest.setError("菜单不存在或已经被删除");
            return apiRest;
        } else if (Objects.equals(wxMenu.getParentId(), BigInteger.ZERO)) {
            List<WxMenu> wxMenuList = wechatMenuMapper.getWxMenu(tenantId, null, wxMenu.getId(), null);
            for (WxMenu wxMenu1 : wxMenuList) {
                wxMenu1.setDeleted(true);
                wxMenu1.setLastUpdateAt(new Date());
                wxMenu1.setLastUpdateBy("System");
                int k = wechatMenuMapper.update(wxMenu1);
                if (k <= 0) {
                    apiRest.setError("删除菜单子菜单失败！");
                    apiRest.setIsSuccess(false);
                    return apiRest;
                }
            }
            if (Objects.equals(wxMenu.getRank(), BigInteger.TEN)) {
                WxMenu firstWxMenu = wechatMenuMapper.getWxMenuByRank(tenantId, BigInteger.valueOf(20));
                if (firstWxMenu != null) {
                    List<WxMenu> wxMenus = wechatMenuMapper.getWxMenu(tenantId, null, firstWxMenu.getId(), null);
                    for (WxMenu wxMenu1 : wxMenus) {
                        wxMenu1.setRank(wxMenu1.getRank().subtract(BigInteger.TEN));
                        wxMenu1.setLastUpdateAt(new Date());
                        wxMenu1.setLastUpdateBy("System");
                        int k = wechatMenuMapper.update(wxMenu1);
                        if (k <= 0) {
                            apiRest.setError("修改子菜单级别失败！");
                            apiRest.setIsSuccess(false);
                            return apiRest;
                        }
                    }
                    firstWxMenu.setRank(BigInteger.TEN);
                    firstWxMenu.setLastUpdateAt(new Date());
                    firstWxMenu.setLastUpdateBy("System");
                    int k = wechatMenuMapper.update(firstWxMenu);
                    if (k <= 0) {
                        apiRest.setError("修改一级菜单级别失败！");
                        apiRest.setIsSuccess(false);
                        return apiRest;
                    }
                }

                WxMenu firstWxMenu1 = wechatMenuMapper.getWxMenuByRank(tenantId, BigInteger.valueOf(30));
                if (firstWxMenu1 != null) {
                    List<WxMenu> wxMenus = wechatMenuMapper.getWxMenu(tenantId, null, firstWxMenu1.getId(), null);
                    for (WxMenu wxMenu1 : wxMenus) {
                        wxMenu1.setRank(wxMenu1.getRank().subtract(BigInteger.TEN));
                        wxMenu1.setLastUpdateAt(new Date());
                        wxMenu1.setLastUpdateBy("System");
                        int k = wechatMenuMapper.update(wxMenu1);
                        if (k <= 0) {
                            apiRest.setError("修改子菜单级别失败！");
                            apiRest.setIsSuccess(false);
                            return apiRest;
                        }
                    }
                    firstWxMenu1.setRank(BigInteger.valueOf(20));
                    firstWxMenu1.setLastUpdateAt(new Date());
                    firstWxMenu1.setLastUpdateBy("System");
                    int k = wechatMenuMapper.update(firstWxMenu1);
                    if (k <= 0) {
                        apiRest.setError("修改一级菜单级别失败！");
                        apiRest.setIsSuccess(false);
                        return apiRest;
                    }
                }
            }
            if (Objects.equals(wxMenu.getRank(), BigInteger.valueOf(20))) {
                WxMenu firstWxMenu = wechatMenuMapper.getWxMenuByRank(tenantId, BigInteger.valueOf(30));
                if (firstWxMenu != null) {
                    List<WxMenu> wxMenus = wechatMenuMapper.getWxMenu(tenantId, null, firstWxMenu.getId(), null);
                    for (WxMenu wxMenu1 : wxMenus) {
                        wxMenu1.setRank(wxMenu1.getRank().subtract(BigInteger.TEN));
                        wxMenu1.setLastUpdateAt(new Date());
                        wxMenu1.setLastUpdateBy("System");
                        int k = wechatMenuMapper.update(wxMenu1);
                        if (k <= 0) {
                            apiRest.setError("修改子菜单级别失败！");
                            apiRest.setIsSuccess(false);
                            return apiRest;
                        }
                    }
                    firstWxMenu.setRank(BigInteger.valueOf(20));
                    firstWxMenu.setLastUpdateAt(new Date());
                    firstWxMenu.setLastUpdateBy("System");
                    int k = wechatMenuMapper.update(firstWxMenu);
                    if (k <= 0) {
                        apiRest.setError("修改一级菜单级别失败！");
                        apiRest.setIsSuccess(false);
                        return apiRest;
                    }
                }
            }
            wxMenu.setDeleted(true);
            int j = wechatMenuMapper.update(wxMenu);
            if (j <= 0) {
                apiRest.setError("删除菜单失败！");
                apiRest.setIsSuccess(false);
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("菜单删除成功！");
        } else if (!Objects.equals(wxMenu.getParentId(), BigInteger.ZERO)) {
            wxMenu.setDeleted(true);
            wxMenu.setLastUpdateAt(new Date());
            wxMenu.setLastUpdateBy("System");
            int j = wechatMenuMapper.update(wxMenu);
            if (j <= 0) {
                apiRest.setError("删除菜单失败！");
                apiRest.setIsSuccess(false);
                return apiRest;
            }
            BigInteger rank = wxMenu.getRank().add(BigInteger.ONE);
            BigInteger rankMax = rank.add(BigInteger.valueOf(4));
            for (BigInteger a = new BigInteger(rank.toString()); a.compareTo(rankMax) < 0; a = a.add(BigInteger.ONE)) {
                WxMenu wxMenu1 = wechatMenuMapper.getWxMenuByRank(tenantId, a);
                if (wxMenu1 != null) {
                    wxMenu1.setRank(wxMenu1.getRank().subtract(BigInteger.ONE));
                    wxMenu1.setLastUpdateAt(new Date());
                    wxMenu1.setLastUpdateBy("System");
                    int b = wechatMenuMapper.update(wxMenu1);
                    if (b <= 0) {
                        apiRest.setError("修改菜单级别失败！");
                        apiRest.setIsSuccess(false);
                        return apiRest;
                    }
                }
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("菜单删除成功！");
        }
        return apiRest;
    }

    /**
     * 新增或修改微信菜单
     *
     * @param params
     * @return
     */
    public ApiRest addOrUpdateWxMenu(Map params) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
        String name = params.get("name").toString();
        Integer menuType = Integer.valueOf(params.get("menuType").toString());
        if (params.get("menuId") != null && !params.get("menuId").equals("")) {//修改
            BigInteger menuId = BigInteger.valueOf(Long.parseLong((String) params.get("menuId")));
            WxMenu wxMenu = wechatMenuMapper.getWxMenuById(tenantId, menuId);
            wxMenu.setName(name);
            List<Map> wxMenuList = wechatMenuMapper.listWxMenu(tenantId, null, wxMenu.getId(), null);
            if (menuType == 1) {
                wxMenu.setItemId(BigInteger.ONE);
                wxMenu.setMenuType(1);
                wxMenu.setMediaId("");
                wxMenu.setUrl("");
                if (wxMenuList.size() <= 0) {
                    wxMenu.setMsgContent(params.get("msgContent") == null ? "" : params.get("msgContent").toString());
                }
            } else if (menuType == 2) {
                wxMenu.setMsgContent("");
                wxMenu.setMenuType(2);
                if (wxMenuList.size() <= 0) {
                    wxMenu.setMediaId(params.get("mediaId") == null ? "" : params.get("mediaId").toString());
                    wxMenu.setUrl(params.get("url") == null ? "" : params.get("url").toString());
                }
            } else if (menuType == 3) {
                wxMenu.setMenuType(3);
                wxMenu.setMediaId("");
                wxMenu.setMsgContent("");
                if (wxMenuList.size() <= 0) {
                    wxMenu.setUrl(params.get("chainedAddress") == null ? "" : params.get("chainedAddress").toString());
                }
            } else if (menuType == 4) {
                wxMenu.setMsgContent("");
                wxMenu.setMenuType(4);
                wxMenu.setMediaId("");
                wxMenu.setUrl("");
                if (wxMenuList.size() <= 0) {
                    wxMenu.setItemId(BigInteger.valueOf(Long.valueOf(params.get("itemId").toString())));
                }
            } else if (menuType == 5) {
                String miniProgramAppId = params.get("miniProgramAppId").toString();
                Object miniProgramOriginalId = params.get("miniProgramOriginalId");
                BigInteger miniProgramItemId = BigInteger.valueOf(Long.valueOf(params.get("miniProgramItemId").toString()));
                String url = params.get("url").toString();
                wxMenu.setMiniProgramAppId(miniProgramAppId);
                wxMenu.setMiniProgramItemId(miniProgramItemId);
                wxMenu.setMenuType(5);
                wxMenu.setUrl(url);
                wxMenu.setMiniProgramOriginalId(miniProgramOriginalId == null ? "" : miniProgramOriginalId.toString());
            } else if (menuType == 6) {
                String miniProgramAppId = params.get("miniProgramAppId").toString();
                Object miniProgramOriginalId = params.get("miniProgramOriginalId");
                String pagePath = params.get("pagePath").toString();
                String url = params.get("url").toString();
                wxMenu.setMiniProgramAppId(miniProgramAppId);
                wxMenu.setPagePath(pagePath);
                wxMenu.setUrl(url);
                wxMenu.setMenuType(6);
                wxMenu.setMiniProgramOriginalId(miniProgramOriginalId == null ? "" : miniProgramOriginalId.toString());
            }
            wxMenu.setDeleted(false);
            wxMenu.setLastUpdateAt(new Date());
            wxMenu.setLastUpdateBy("System");
//            int t = wechatMenuMapper.update(wxMenu);
            long t = DatabaseHelper.update(wxMenu);
            if (t <= 0) {
                apiRest.setError("修改微信菜单失败！");
                apiRest.setIsSuccess(false);
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("微信菜单修改成功！");
            apiRest.setData(wxMenu);
        } else {//新增
            WxMenu wxMenu = new WxMenu();
            wxMenu.setTenantId(tenantId);
            BigInteger parentId = BigInteger.valueOf(Long.parseLong((String) params.get("parentId")));
            wxMenu.setParentId(parentId);
            if (!Objects.equals(parentId, BigInteger.ZERO)) {
                List<Map> wxMenuList = wechatMenuMapper.listWxMenu(tenantId, null, parentId, null);
                if (wxMenuList.size() >= 5) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("二级菜单已经满足五个，不能继续添加");
                    return apiRest;
                }
                WxMenu wxMenu1 = wechatMenuMapper.getWxMenuById(tenantId, parentId);
                if (wxMenu1 == null) {
                    apiRest.setError("查询上级菜单失败！");
                    apiRest.setIsSuccess(false);
                    return apiRest;
                }
                String rank = wxMenu1.getRank().toString().substring(0, 1) + (wxMenuList.size() + 1);
                wxMenu.setRank(BigInteger.valueOf(Long.valueOf(rank)));
            } else {
                List<Map> wxMenus = wechatMenuMapper.listWxMenu(tenantId, "one", null, null);
                if (wxMenus.size() > 2) {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("一级菜单已经满足三个，不能继续添加");
                    return apiRest;
                }
                BigInteger rank = new BigInteger((wxMenus.size() + 1) + "0");
                wxMenu.setRank(rank);
            }
            wxMenu.setName(name);
            wxMenu.setMenuType(menuType);
            if (menuType == 1) {
                wxMenu.setMsgContent(params.get("msgContent") == null ? "" : params.get("msgContent").toString());
                wxMenu.setItemId(BigInteger.ONE);
            } else if (menuType == 2) {
                wxMenu.setMediaId(params.get("mediaId") == null ? "" : params.get("mediaId").toString());
                wxMenu.setUrl(params.get("url") == null ? "" : params.get("url").toString());
                wxMenu.setMsgContent("");
                wxMenu.setItemId(BigInteger.ONE);
            } else if (menuType == 3) {
                wxMenu.setMsgContent("");
                wxMenu.setUrl(params.get("chainedAddress") == null ? "" : params.get("chainedAddress").toString());
                wxMenu.setItemId(BigInteger.ONE);
            } else if (menuType == 4) {
                BigInteger itemId = BigInteger.valueOf(Long.parseLong((String) params.get("itemId")));
                wxMenu.setItemId(itemId);
                wxMenu.setMsgContent("");
            } else if (menuType == 5) {
                String miniProgramAppId = params.get("miniProgramAppId").toString();
                BigInteger miniProgramItemId = BigInteger.valueOf(Long.valueOf(params.get("miniProgramItemId").toString()));
                String url = params.get("url").toString();
                wxMenu.setMiniProgramAppId(miniProgramAppId);
                wxMenu.setMiniProgramItemId(miniProgramItemId);
                wxMenu.setUrl(url);
            } else if (menuType == 6) {
                String miniProgramAppId = params.get("miniProgramAppId").toString();
                String pagePath = params.get("pagePath").toString();
                String url = params.get("url").toString();
                wxMenu.setMiniProgramAppId(miniProgramAppId);
                wxMenu.setPagePath(pagePath);
                wxMenu.setUrl(url);
            }
            wxMenu.setDeleted(false);
            wxMenu.setLastUpdateAt(new Date());
            wxMenu.setLastUpdateBy("System");
            wxMenu.setCreateAt(new Date());
            wxMenu.setCreateBy("System");
//            int t = wechatMenuMapper.insert(wxMenu);
            long t = DatabaseHelper.insert(wxMenu);
            if (t <= 0) {
                apiRest.setError("新增微信菜单失败！");
                apiRest.setIsSuccess(false);
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("新增微信菜单成功！");
            apiRest.setData(wxMenu);
        }
        return apiRest;
    }

    /**
     * 清空一级菜单内容
     *
     * @author szq
     */
    public ApiRest emptyWxMenuOneContent(Map params) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
        BigInteger menuId = BigInteger.valueOf(Long.parseLong((String) params.get("menuId")));
        WxMenu wxMenu = wechatMenuMapper.getWxMenuById(tenantId, menuId);
        if (wxMenu != null) {
            if (wxMenu.getMenuType() == 1) {
                wxMenu.setMsgContent("");
            } else if (wxMenu.getMenuType() == 2) {
                wxMenu.setMediaId(null);
                wxMenu.setUrl(null);
            } else if (wxMenu.getMenuType() == 3 || wxMenu.getMenuType() == 4) {
                wxMenu.setUrl(null);
            }
            wxMenu.setLastUpdateAt(new Date());
            wxMenu.setLastUpdateBy("System");
            int k = wechatMenuMapper.update(wxMenu);
            if (k <= 0) {
                apiRest.setIsSuccess(false);
                apiRest.setError("修改微信菜单失败！");
                return apiRest;
            }
            apiRest.setIsSuccess(true);
            apiRest.setMessage("清空一级菜单内容成功！");
            apiRest.setData(wxMenu);
        } else {
            apiRest.setIsSuccess(false);
            apiRest.setError("查询菜单失败！");
            return apiRest;
        }
        return apiRest;
    }

    /**
     * 查看跳转功能的名称
     */
    public ApiRest listMenuItemName() {
        ApiRest apiRest = new ApiRest();
        List<Map> menuItemList = wechatMenuMapper.wxMenuItem();
        apiRest.setIsSuccess(true);
        apiRest.setMessage("查询wxItem成功！");
        apiRest.setData(menuItemList);
        return apiRest;
    }

    /**
     * 菜单排序
     */
    public ApiRest editRank(Map params) {
        ApiRest apiRest = new ApiRest();
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
        String[] rankArr = params.get("rankArr").toString().split(",");
        List<WxMenu> wxMenuList = new ArrayList<>();
        for (String aRankArr : rankArr) {
            BigInteger id = new BigInteger(aRankArr.split("a")[0]);
            BigInteger rank = new BigInteger(aRankArr.split("a")[1]);
            if (!Objects.equals(id, BigInteger.ZERO)) {
                WxMenu wxMenu = wechatMenuMapper.getWxMenuById(tenantId, id);
                if (wxMenu != null) {
                    wxMenu.setRank(rank);
                    wxMenu.setLastUpdateAt(new Date());
                    wxMenu.setLastUpdateBy("System");
                    int k = wechatMenuMapper.update(wxMenu);
                    if (k <= 0) {
                        apiRest.setIsSuccess(false);
                        apiRest.setError("更新微信菜单失败！");
                        return apiRest;
                    }
                    wxMenuList.add(wxMenu);
                } else {
                    apiRest.setIsSuccess(false);
                    apiRest.setError("id为" + id + "的菜单不存在或已被删除");
                    return apiRest;
                }
            }
        }
        apiRest.setIsSuccess(true);
        apiRest.setMessage("修改微信菜单排序成功!");
        apiRest.setData(wxMenuList);
        return apiRest;
    }

    /**
     * 查看跳转功能的名称
     */
    /*public ApiRest pushMenu(Map params) throws IOException {
        ApiRest apiRest = new ApiRest();
        String jsonResult;
        params.put("sysUserId", params.get("userId"));
        params.put("methodKey", "listWechatInfo");
        Map mapList = new HashMap<>();
        mapList.put("sysUserId", params.get("userId"));
        mapList.put("methodKey", "listWechatInfo");
        jsonResult = SaaSApi.listWechatInfo(mapList);
        JSONObject j = JSONObject.fromObject(jsonResult);
        JSONObject jj = JSONObject.fromObject(j.get("jsonMap"));
        JSONArray bb = (JSONArray) jj.get("rows");
        //获得appid、secret、originalId并开始推送菜单
        if (bb != null && bb.size() > 0) {
            params.put("appid", ((Map) bb.get(0)).get("appId"));
            params.put("secret", ((Map) bb.get(0)).get("appSecret"));
            params.put("originalId", ((Map) bb.get(0)).get("originalId"));
            params.put("wechatInfoId", ((Map) bb.get(0)).get("id"));
            apiRest = pushMenuService(params);
        }
        return apiRest;
    }*/
    public ApiRest pushMenu(Map params) throws IOException {
        String tenantId = params.get("tenantId").toString();
        Map<String, String> wechatInfo = WeiXinUtils.obtainWechatInfo(null, tenantId, null);
        ValidateUtils.notNull(wechatInfo, "未配置微信公众号！");
        params.put("appid", wechatInfo.get("appId"));
        params.put("secret", wechatInfo.get("secret"));
        params.put("originalId", wechatInfo.get("originalId"));
        params.put("wechatInfoId", wechatInfo.get("id"));
        return pushMenuService(params);
    }

    /**
     * 推送微信菜单
     *
     * @param params
     * @return
     */
    public ApiRest pushMenuService(Map params) throws IOException {
        ApiRest apiRest = new ApiRest();
        String result = "";
        String appid = params.get("appid").toString();
        if (appid == null || appid.equals("") || appid.equals("null")) {
            apiRest.setIsSuccess(false);
            apiRest.setError("appId为空，推送失败");
            return apiRest;
        }
        String secret = params.get("secret").toString();
        String originalId = params.get("originalId").toString();
        if (originalId == null || originalId.equals("") || originalId.equals("null")) {
            apiRest.setIsSuccess(false);
            apiRest.setError("originalId为空，推送失败");
            return apiRest;
        }
        BigInteger tenantId = BigInteger.valueOf(Long.parseLong((String) params.get("tenantId")));
        //2018-05-14修改，读取配置文件分区号，凡是能进入此服务的，应该都取本程序配置分区号
        String pCode = PropertyUtils.getDefault("partition.code");
            /*ApiRest rest = SaaSApi.findTenantById(tenantId);
            if (rest.getIsSuccess()) {
                Tenant tenant = ApiBaseServiceUtils.MapToObject((Map) ((Map) rest.getData()).get("tenant"), Tenant.class);
                if(tenant != null){
                    pCode = tenant.getPartitionCode();
                }
            }*/
        //一级菜单
        List<WxMenu> wxMenusOne = wechatMenuMapper.listWxMenuInfo(tenantId, 1);
        //二级菜单
        List<WxMenu> wxMenusTwo = wechatMenuMapper.listWxMenuInfo(tenantId, 2);
        JSONObject jsonObject1 = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (WxMenu wxMenu : wxMenusOne) {
            JSONObject jsonObject2 = new JSONObject();
            JSONArray jsonArray1 = new JSONArray();
            jsonObject2.put("name", wxMenu.getName());
            //循环组装二级菜单
            if (wxMenusTwo.size() > 0) {
                for (WxMenu wxMenu1 : wxMenusTwo) {
                    if (Objects.equals(wxMenu1.getParentId(), wxMenu.getId())) {
                        jsonArray1.add(getMenuObjItem(wxMenu1, appid, originalId, pCode));
                    }
                }
            }
            //组装一级菜单
            if (jsonArray1.size() > 0) {
                jsonObject2.put("sub_button", jsonArray1);
            } else {
                jsonObject2 = getMenuObjItem(wxMenu, appid, originalId, pCode);
            }
            jsonArray.add(jsonObject2);
        }
        jsonObject1.put("button", jsonArray);
        //推送
//            result = connectWxServicePushMenu(appid,secret,jsonObject1.toString());
        Map pMap = new HashMap();
        pMap.put("wxWechatInfoId", params.get("wechatInfoId"));
        pMap.put("body", jsonObject1.toString());
        ApiRest jsonResult = ProxyApi.proxyPost("out", "wechat", "pushMenu", pMap);
        if (Constants.REST_RESULT_SUCCESS.equals(jsonResult.getResult())) {
            apiRest.setIsSuccess(true);
            apiRest.setMessage("菜单推送成功");
        } else {
            apiRest.setIsSuccess(false);
            apiRest.setMessage("微信菜单推送失败，请检查账号配置");
            apiRest.setError("微信菜单推送失败，请检查账号配置");
            LogUtil.logInfo("微信菜单推送失败，请检查账号配置" + jsonResult.getMessage());
        }
        return apiRest;

    }

    /**
     * 组装单个菜单
     *
     * @param wxMenu1
     * @return
     */
    public JSONObject getMenuObjItem(WxMenu wxMenu1, String appid, String orignalId, String pCode) throws IOException {
        JSONObject jsonObject = new JSONObject();
        if (wxMenu1.getMenuType() == 3) {
            //跳转链接
            jsonObject = wxMenu1.getJsonObj("view");
        } else if (wxMenu1.getMenuType() == 1) {
            //文本
            jsonObject = wxMenu1.getJsonObj("click");
        } else if (wxMenu1.getMenuType() == 2) {
            // 图文
            jsonObject = wxMenu1.getJsonObj("view_limited");
        } else if (wxMenu1.getMenuType() == 4) {
            //跳转内部链接
            WxMenuItem wxMenuItem = wechatMenuItemMapper.getMenuItemById(wxMenu1.getItemId());
            StringBuffer url = getCombineUrl(appid, wxMenuItem, orignalId, pCode);
            wxMenu1.setUrl(url.toString());
            wechatMenuMapper.update(wxMenu1);
            jsonObject = wxMenu1.getJsonObj("view");
        }
        return jsonObject;
    }

    /**
     * 拼接URL
     *
     * @return
     */
    public StringBuffer getCombineUrl(String appid, WxMenuItem wxMenuItem, String orignalId, String pCode) throws IOException {
        StringBuffer url = new StringBuffer();
        String domainName = "";
        if (pCode != null && !"".equals(pCode)) {
            domainName = CacheUtils.hget(SessionConstants.KEY_SERVICE_DOMAIN, SessionConstants.KEY_OUTSIDE_SERVICE_NAME + PropertyUtils.getDefault("deploy.env") + "_" + Constants.SERVICE_NAME_CT + "_" + pCode);
        } else {
            domainName = Common.getOutsideServiceDomain(Constants.SERVICE_NAME_CT);
        }
        url.append("https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=");
        String redirectUrl = "";
        if (wxMenuItem.getParams() != null) {
            redirectUrl = domainName + "/" + wxMenuItem.getController() + "/" + wxMenuItem.getAction() + "?orignalId=" + orignalId + "&" + wxMenuItem.getParams();
        } else {
            redirectUrl = domainName + "/" + wxMenuItem.getController() + "/" + wxMenuItem.getAction() + "?orignalId=" + orignalId;
        }
        url.append(URLEncoder.encode(redirectUrl, "UTF-8"));
        url.append("&response_type=code&scope=snsapi_base&#wechat_redirect");
        return url;
    }
}
