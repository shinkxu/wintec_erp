package erp.chain.service.online;

import erp.chain.common.Constants;
import erp.chain.domain.Branch;
import erp.chain.mapper.BranchMapper;
import erp.chain.utils.BaiDuMapUtils;
import erp.chain.utils.SearchModel;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2018-04-14.
 */
@Service
public class OnlineBranchService {
    @Autowired
    private BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public List<Branch> listCtBranches(BigInteger tenantId, Double longitude, Double latitude) {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        searchModel.addSearchCondition("is_tinyhall", Constants.SQL_OPERATION_SYMBOL_EQUALS, 1);
        List<Branch> branches = branchMapper.findAll(searchModel);

        Map<String, Object> resultMap = BaiDuMapUtils.geoConv(longitude + "," + latitude, "7aG5a3PuW7kg3Q3eS1Uq6ZdVkg8Zi0GG", 3, 5, null, null);
        Map<String, Object> result = ((List<Map<String, Object>>) resultMap.get("result")).get(0);
        double baiDuLongitude = MapUtils.getDoubleValue(result, "x");
        double baiDuLatitude = MapUtils.getDoubleValue(result, "y");

        for (Branch branch : branches) {
            String geolocation = branch.getGeolocation();
            String[] latitudeAndLongitude = geolocation.split(",");
            branch.setDistanceValue(computeDistance(baiDuLongitude, baiDuLatitude, Double.parseDouble(latitudeAndLongitude[0]), Double.parseDouble(latitudeAndLongitude[1])));
        }

        Collections.sort(branches);
        return branches;
    }

    private BigDecimal computeDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        double dx = longitude1 - longitude2;
        double dy = latitude1 - latitude2;
        double b = (latitude1 + latitude2) / 2.0;
        double Lx = Math.toRadians(dx) * 6367000.0 * Math.cos(Math.toRadians(b));
        double Ly = 6367000.0 * Math.toRadians(dy);
        return BigDecimal.valueOf(Math.sqrt(Lx * Lx + Ly * Ly));
    }
}
