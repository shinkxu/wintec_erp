package erp.chain.controller.supply.store;

import com.saas.common.util.LogUtil;
import erp.chain.model.supply.store.*;
import erp.chain.service.supply.store.impl.StoreOrderServiceImpl;
import erp.chain.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import saas.api.common.ApiRest;

import java.math.BigInteger;
import java.util.Map;

/**
 * 库存单据
 */
@Controller
@ResponseBody
@RequestMapping("/storeOrder")
public class StoreOrderController {
    @Autowired
    StoreOrderServiceImpl storeOrderService;

    /**
     * 保存采购单
     */
    @ResponseBody
    @RequestMapping(value = "/saveCgOrder", method = RequestMethod.POST)
    public String saveCgOrder() {
        ApiRest apiRest = new ApiRest();
        CgOrderModel model;
        try {
            model = AppHandler.bind(CgOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.saveCgOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存盘点单
     */
    @ResponseBody
    @RequestMapping(value = "/savePdOrder", method = RequestMethod.POST)
    public String savePdOrder() {
        ApiRest apiRest = new ApiRest();
        PdOrderModel model;
        try {
            model = AppHandler.bind(PdOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.savePdOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存领用单
     */
    @ResponseBody
    @RequestMapping(value = "/saveLyOrder", method = RequestMethod.POST)
    public String saveLyOrder() {
        ApiRest apiRest = new ApiRest();
        LyOrderModel model;
        try {
            model = AppHandler.bind(LyOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.saveLyOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改领用单
     */
    @ResponseBody
    @RequestMapping(value = "/updateLyOrder", method = RequestMethod.POST)
    public String updateLyOrder() {
        ApiRest apiRest = new ApiRest();
        LyUpdateOrderModel model;
        try {
            model = AppHandler.bind(LyUpdateOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.updateLyOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (OptimisticLockingFailureException | ResourceNotExistException | NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }
    /**
     * 修改Pd
     */
    @ResponseBody
    @RequestMapping(value = "/updatePdOrder", method = RequestMethod.POST)
    public String updatePdOrder() {
        ApiRest apiRest = new ApiRest();
        PdUpdateOrderModel model;
        try {
            model = AppHandler.bind(PdUpdateOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.updatePdOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (OptimisticLockingFailureException | ResourceNotExistException | NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改Cg
     */
    @ResponseBody
    @RequestMapping(value = "/updateCgOrder", method = RequestMethod.POST)
    public String updateCgOrder() {
        ApiRest apiRest = new ApiRest();
        CgUpdateOrderModel model;
        try {
            model = AppHandler.bind(CgUpdateOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.updateCgOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (OptimisticLockingFailureException | ResourceNotExistException | NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存损溢单
     */
    @ResponseBody
    @RequestMapping(value = "/saveSyOrder", method = RequestMethod.POST)
    public String saveSyOrder() {
        ApiRest apiRest = new ApiRest();
        SyOrderModel model;
        try {
            model = AppHandler.bind(SyOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.saveSyOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 修改损溢单
     */
    @ResponseBody
    @RequestMapping(value = "/updateSyOrder", method = RequestMethod.POST)
    public String updateSyOrder() {
        ApiRest apiRest = new ApiRest();
        SyUpdateOrderModel model;
        try {
            model = AppHandler.bind(SyUpdateOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.updateSyOrder(model);
            apiRest.setIsSuccess(true);
        } catch (BindParamsException e) {
            apiRest.setMessage("json格式错误");
        } catch (OptimisticLockingFailureException | ResourceNotExistException | NonUniqueResultException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询机构损溢单
     */
    @ResponseBody
    @RequestMapping(value = "/querySyOrderPager")
    public String querySyOrderPager() {
        ApiRest apiRest = new ApiRest();
        QuerySyOrderModel model = AppHandler.bindParam(QuerySyOrderModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(storeOrderService.querySyOrderPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 通过单据号查询损溢单单据信息
     */
    @ResponseBody
    @RequestMapping(value = "/querySyOrderInfo")
    public String querySyOrderInfo() {
        ApiRest apiRest = new ApiRest();
        try {
            String bId = AppHandler.params().get("branchId");
            String tIdStr = AppHandler.params().get("tenantId");
            String code = AppHandler.params().get("code");
            BigInteger branchId = null;
            BigInteger tId = null;
            try {
                branchId = new BigInteger(bId);
                tId = new BigInteger(tIdStr);
            } catch (Exception e) {
            }
            Validate.notNull(branchId, "branchId 不能为null");
            Validate.notNull(tId, "tenantId 不能为null");
            Validate.notNull(code, "code 不能为null");

            apiRest.setData(storeOrderService.querySyOrderInfo(tId, branchId, code));
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }

        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 审核单据
     */
    @RequestMapping(value = "/auditOrder")
    public String auditOrder() {
        ApiRest apiRest = new ApiRest();
        AuditOrderModel model;
        try {
            model = AppHandler.bindParam(AuditOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            boolean flag = storeOrderService.auditOrder(model);
            if(!flag){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("单据明细为空，不能审核");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest.setIsSuccess(true);
        } catch (OptimisticLockingFailureException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除领用单
     */
    @ResponseBody
    @RequestMapping(value = "/delOrder")
    public String delOrder() {
        ApiRest apiRest = new ApiRest();
        DelOrderModel model;
        try {
            model = AppHandler.bindParam(DelOrderModel.class);
            if (!model.validate()) {
                apiRest.setData(model.getErrors());
                return BeanUtils.toJsonStr(apiRest);
            }
            storeOrderService.delOrder(model);
            apiRest.setIsSuccess(true);
        } catch (OptimisticLockingFailureException | ResourceNotExistException e) {
            apiRest.setMessage(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询机构采购单
     */
    @ResponseBody
    @RequestMapping(value = "/queryCgOrderPager")
    public String queryCgOrderPager() {
        ApiRest apiRest = new ApiRest();
        QueryCgOrderModel model = AppHandler.bindParam(QueryCgOrderModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }

        apiRest.setData(storeOrderService.queryCgOrderPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询机构盘点单
     */
    @ResponseBody
    @RequestMapping(value = "/queryPdOrderPager")
    public String queryPdOrderPager() {
        ApiRest apiRest = new ApiRest();
        QueryPdOrderModel model = AppHandler.bindParam(QueryPdOrderModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }

        apiRest.setData(storeOrderService.queryPdOrderPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询机构领用单
     */
    @ResponseBody
    @RequestMapping(value = "/queryLyOrderPager")
    public String queryLyOrderPager() {
        ApiRest apiRest = new ApiRest();
        QueryLyOrderModel model = AppHandler.bindParam(QueryLyOrderModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }

        apiRest.setData(storeOrderService.queryLyOrderPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询机构领用单明细
     */
    @ResponseBody
    @RequestMapping(value = "/queryLyOrderDetailPager")
    public String queryLyOrderDetailPager() {
        ApiRest apiRest = new ApiRest();
        QueryLyOrderDetailModel model = AppHandler.bindParam(QueryLyOrderDetailModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }

        apiRest.setData(storeOrderService.queryLyOrderDetailPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 通过单据号查询领用单单据信息
     */
    @ResponseBody
    @RequestMapping(value = "/queryLyOrderInfo")
    public String queryLyOrderInfo() {
        ApiRest apiRest = new ApiRest();
        try {
            String bId = AppHandler.params().get("branchId");
            String tIdStr = AppHandler.params().get("tenantId");
            String code = AppHandler.params().get("code");
            BigInteger branchId = null;
            BigInteger tId = null;
            try {
                branchId = new BigInteger(bId);
                tId = new BigInteger(tIdStr);
            } catch (Exception e) {
            }
            Validate.notNull(branchId, "branchId 不能为null");
            Validate.notNull(tId, "tenantId 不能为null");
            Validate.notNull(code, "code 不能为null");

            apiRest.setData(storeOrderService.queryLyOrderInfo(tId, branchId, code));
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }

        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 通过单据号查询单据信息
     */
    @ResponseBody
    @RequestMapping(value = "/queryCgOrderInfo")
    public String queryCgOrderInfo() {
        ApiRest apiRest = new ApiRest();
        try {
            String bId = AppHandler.params().get("branchId");
            String tIdStr = AppHandler.params().get("tenantId");
            String code = AppHandler.params().get("code");
            BigInteger branchId = null;
            BigInteger tId = null;
            try {
                branchId = new BigInteger(bId);
                tId = new BigInteger(tIdStr);
            } catch (Exception e) {
            }
            Validate.notNull(branchId, "branchId 不能为null");
            Validate.notNull(tId, "tenantId 不能为null");
            Validate.notNull(code, "code 不能为null");

            apiRest.setData(storeOrderService.queryCgOrderInfo(tId, branchId, code));
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }

        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 通过单据号查询单据信息
     */
    @ResponseBody
    @RequestMapping(value = "/queryPdOrderInfo")
    public String queryPdOrderInfo() {
        ApiRest apiRest = new ApiRest();
        try {
            String bId = AppHandler.params().get("branchId");
            String tIdStr = AppHandler.params().get("tenantId");
            String code = AppHandler.params().get("code");
            BigInteger branchId = null;
            BigInteger tId = null;
            try {
                branchId = new BigInteger(bId);
                tId = new BigInteger(tIdStr);
            } catch (Exception e) {
            }
            Validate.notNull(branchId, "branchId 不能为null");
            Validate.notNull(tId, "tenantId 不能为null");
            Validate.notNull(code, "code 不能为null");

            apiRest.setData(storeOrderService.queryPdOrderInfo(tId, branchId, code));
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setMessage(e.getMessage());
        }

        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询机构采购单明细
     */
    @ResponseBody
    @RequestMapping(value = "/queryCgOrderDetailPager")
    public String queryCgOrderDetailPager() {
        ApiRest apiRest = new ApiRest();
        QueryCgOrderDetailModel model = AppHandler.bindParam(QueryCgOrderDetailModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }

        apiRest.setData(storeOrderService.queryCgOrderDetailPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询机构盘点单明细
     */
    @ResponseBody
    @RequestMapping(value = "/queryPdOrderDetailPager")
    public String queryPdOrderDetailPager() {
        ApiRest apiRest = new ApiRest();
        QueryPdOrderDetailModel model = AppHandler.bindParam(QueryPdOrderDetailModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }

        apiRest.setData(storeOrderService.queryPdOrderDetailPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 保存打包或拆包
     */
    @ResponseBody
    @RequestMapping(value = "/saveConvertGoodsStoreOrder")
    public String saveConvertGoodsStoreOrder() {
        ApiRest apiRest = new ApiRest();
        ConvertGoodsStoreOrderModel model = AppHandler.bindParam(ConvertGoodsStoreOrderModel.class);
        if (!model.validate()) {
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        storeOrderService.saveConvertGoodsStoreOrder(model);
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 获取下一个编号
     */
    @ResponseBody
    @RequestMapping(value = "/getNextCode")
    public String getNextCode() {
        ApiRest apiRest = new ApiRest();
        try {
            String type = StringUtils.trimToNull(AppHandler.request().getParameter("type"));
            String bId = AppHandler.params().get("branchId");
            String tIdStr = AppHandler.params().get("tenantId");
            BigInteger branchId = null;
            BigInteger tId = null;
            Integer typeInt = null;
            try {
                branchId = new BigInteger(bId);
                tId = new BigInteger(tIdStr);
                typeInt = Integer.valueOf(type);
            } catch (Exception e) {
            }
            Validate.notNull(branchId, "branchId 不能为null");
            Validate.notNull(tId, "tenantId 不能为null");
            Validate.notNull(typeInt, "type not null");

            String code = storeOrderService.getNextCode(tId, branchId, typeInt);
            apiRest.setData(code);
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            apiRest.setData(e.getMessage());
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询库存单据
     */
    @RequestMapping("/queryOrderPager")
    public String queryOrderPager() {
        ApiRest apiRest = new ApiRest();
        QueryOrderModel model = AppHandler.bindParam(QueryOrderModel.class);
        if (!model.validate()){
            apiRest.setData(model.getErrors());
            return BeanUtils.toJsonStr(apiRest);
        }
        apiRest.setData(storeOrderService.queryOrderPager(model).toMap());
        apiRest.setIsSuccess(true);
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 分页查询单品库存汇总
     * */
    @RequestMapping("/queryStoreSingleReport")
    public String queryStoreSingleReport(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if (params.get("page") == null || params.get("page").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数page不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if (params.get("rows") == null || params.get("rows").equals("")) {
                apiRest.setIsSuccess(false);
                apiRest.setError("参数rows不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("参数tenantId不能为空！");
                return  BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("occurType") == null || "".equals(params.get("occurType"))){
                apiRest.setIsSuccess(false);
                apiRest.setMessage("参数occurType不能为空！");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.queryStoreSingleReport(params);
        }catch (Exception e) {
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 导入采购单据
     * 根据excel内容查询出商品具体信息返回，不执行保存
     * 相当于验证excel中商品是否存在
     * */
    @RequestMapping("/importCgOrder")
    public String importCgOrder(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.importCgOrder(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 导入盘点单
     * 根据excel内容查询出商品具体信息返回，不执行保存
     * 相当于验证excel中商品是否存在
     * */
    @RequestMapping("/importPdOrder")
    public String importPdOrder(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能跟为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.importPdOrder(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询供应商结算单据
     * */
    @RequestMapping("/querySupplierSettlements")
    public String querySupplierSettlements(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId") != null)){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.querySupplierSettlements(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 根据Code查询结算单及其详情
     * */
    @RequestMapping("/queryJsOrderByCode")
    public String queryJsOrderByCode(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("jsCode") == null || "".equals(params.get("jsCode"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数jsCode不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.queryJsOrderByCode(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 新增或修改结算单
     * */
    @RequestMapping("/addOrUpdateJsOrder")
    public String addOrUpdateJsOrder(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("supplierId") == null || "".equals(params.get("supplierId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数supplierId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.addOrUpdateJsOrder(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 删除结算单
     * */
    @RequestMapping("/deleteJsOrder")
    public String deleteJsOrder(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("userId") == null || "".equals(params.get("userId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数userId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.deleteJsOrder(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 审核结算单
     * */
    @RequestMapping("/auditJsOrder")
    public String auditJsOrder(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("id") == null || "".equals(params.get("id"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数id不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.auditJsOrder(params);
        }catch (Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError(e.getMessage());
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 查询采购单据
     * */
    @RequestMapping("/queryCgOrderToJs")
    public String queryCgOrderToJs(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("branchId") == null || "".equals(params.get("branchId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数branchId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            if(params.get("supplierId") == null || "".equals(params.get("supplierId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数supplierId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.queryCgOrderToJs(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("查询采购单据失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }

    /**
     * 生成结算单单号
     * */
    /*@RequestMapping("/getJsNextCode")
    public String getJsNextCode(){
        ApiRest apiRest = new ApiRest();
        Map params = AppHandler.params();
        try{
            if(params.get("tenantId") == null || "".equals(params.get("tenantId"))){
                apiRest.setIsSuccess(false);
                apiRest.setError("参数tenantId不能为空");
                return BeanUtils.toJsonStr(apiRest);
            }
            apiRest = storeOrderService.getJsNextCode(params);
        }catch(Exception e){
            apiRest.setIsSuccess(false);
            apiRest.setError("获取结算单单据号失败");
            LogUtil.logError(e, params);
        }
        return BeanUtils.toJsonStr(apiRest);
    }*/
}
