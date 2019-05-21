package erp.chain.service;

import com.saas.common.ResultJSON;
import com.saas.common.exception.ServiceException;
import erp.chain.domain.Branch;
import erp.chain.mapper.BranchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xumx on 2016/11/1.
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.Exception.class}, timeout = 120)
public class BranchService {

    @Autowired
    BranchMapper branchMapper;

    public boolean takeoutStatus(BigInteger branchId, int status) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", branchId);
        params.put("takeoutStatus", status);
        return branchMapper.updateByMap(params);
    }

    public List<Branch> findBranchByTenantId(Map params){
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", params.get("tenantId"));
        if(params.get("id") != null && !"".equals(params.get("id"))){
            map.put("id", params.get("id"));
        }
        if(params.get("branchId") != null && !"".equals(params.get("branchId"))){
            map.put("id", params.get("branchId"));
        }
        List<Branch> branches = branchMapper.findBranchByTenantId(map);
        return branches;
    }

    /**
     * ΢������ѯ�ŵ��б�
     * @param
     * @param
     * @param
     * @return
     */
    public ApiRest findBranchForCt(Map params) {
        ApiRest rest = new ApiRest();
        Map<String, Object> map = new HashMap<>();
        map.put("tenantId", params.get("tenantId"));
        if(params.get("branchId") != null){
            map.put("id", params.get("branchId"));
        }
        if(params.get("name") != null){
            map.put("name", params.get("name"));
        }
        List<Branch> branchList = branchMapper.findBranchByTenantId(map);
        if(branchList != null){
            if(params.get("currLat") != null && params.get("currLog") != null){
                for(Branch b:branchList){
                    if(b.getGeolocation() != null){
                        String[] geolocation = b.getGeolocation().split(",");
                        b.setDistanceValue(new BigDecimal(distanceSimplify(Double.parseDouble(geolocation[1]), Double.parseDouble(geolocation[0]), Double.parseDouble(params.get("currLat").toString()), Double.parseDouble(params.get("currLog").toString()))));
                    }
                }
            }
            Collections.sort(branchList);
            rest.setIsSuccess(true);
            rest.setMessage("查询成功！");
            rest.setData(branchList);
        }
        return rest;
    }

    /**
     * ��������λ��֮��ľ���
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    private double distanceSimplify(double lat1, double lng1, double lat2, double lng2) {
        double dx = lng1 - lng2; // ���Ȳ�ֵ
        double dy = lat1 - lat2; // γ�Ȳ�ֵ
        double b = (lat1 + lat2) / 2.0; // ƽ��γ��
        double Lx = Math.toRadians(dx) * 6367000.0* Math.cos(Math.toRadians(b)); // ��������
        double Ly = 6367000.0 * Math.toRadians(dy); // �ϱ�����
        return Math.sqrt(Lx * Lx + Ly * Ly);  // ��ƽ��ľ��ζԽǾ��빫ʽ�����ܾ���
    }
}
