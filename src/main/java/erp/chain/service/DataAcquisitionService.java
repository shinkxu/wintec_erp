package erp.chain.service;

import com.saas.common.util.PropertyUtils;
import com.saas.common.util.QueueUtils;
import erp.chain.mapper.DataAcquisitionMapper;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import saas.api.common.ApiRest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2017/4/20.
 */
@Service
public class DataAcquisitionService {
    @Autowired
    private DataAcquisitionMapper dataAcquisitionMapper;

    @Transactional
    public ApiRest graspDataSynchronization() throws IOException {
        ApiRest apiRest = null;
        String json = null;
        String topic = null;
        try {
            topic = PropertyUtils.getDefault("topic.grasp.sync") + "_" + PropertyUtils.getDefault("partition.code");
            json = QueueUtils.rpop(topic);
            ObjectMapper objectMapper = new ObjectMapper();

            while (StringUtils.isNotEmpty(json)) {
                Map<String, Object> jsonMap = objectMapper.readValue(json, Map.class);

                String cover = (String)jsonMap.get("cover");
                String timestamp = (String)jsonMap.get("timestamp");
                String tenantCode = (String)jsonMap.get("tenantCode");
                String table = (String)jsonMap.get("table");
                List<Map<String, String>> dataList = (List<Map<String, String>>) jsonMap.get("data");
                if (StringUtils.isEmpty(cover) || StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(tenantCode) || StringUtils.isEmpty(table) || dataList == null || dataList.isEmpty()) {
                    json = QueueUtils.rpop(topic);
                    continue;
                }
                String tableName = null;
                if ("Ptype".equals(table)) {
                    tableName = "daq_grasp_ptype";
                } else if ("Btype".equals(table)) {
                    tableName = "daq_grasp_btype";
                } else if ("T_Jxc_PtypeUnit".equals(table)){
                    tableName = "daq_grasp_unit";
                } else {
                    json = QueueUtils.rpop(topic);
                    continue;
                }
                if ("1".equals(jsonMap.get("cover"))) {
                    dataAcquisitionMapper.deleteWithCover(tableName, tenantCode, timestamp);
                    dataAcquisitionMapper.insert(tableName, tenantCode, dataList, timestamp);
                } else {
                    List<Map<String, String>> updateTableMap = new ArrayList<Map<String, String>>();
                    List<Map<String, String>> insertTableMap = new ArrayList<Map<String, String>>();
                    List<String> typeIds = new ArrayList<String>();

                    for (Map<String, String> tableMap : dataList) {
                        String op = tableMap.get("op");
                        if ("D".equals(op)) {
                            typeIds.add(tableMap.get("typeId"));
                        } else if ("U".equals(op)) {
                            updateTableMap.add(tableMap);
                        } else if ("I".equals(op)) {
                            insertTableMap.add(tableMap);
                        }
                    }
                    if (!typeIds.isEmpty()) {
                        dataAcquisitionMapper.deleteByTypeIds(tableName, tenantCode, typeIds);
                    }

                    if (!updateTableMap.isEmpty()) {
                        if ("Ptype".equals(table) || "Btype".equals(table)) {
//                            dataAcquisitionMapper.update(tableName, tenantCode, updateTableMap, timestamp);
                            for (Map<String, String> map : updateTableMap) {
                                dataAcquisitionMapper.update(tableName, tenantCode, timestamp, map.get("fullName"), map.get("typeId"));
                            }
                        } else if ("T_Jxc_PtypeUnit".equals(table)) {
                            List<String> updateTypeIds = new ArrayList<String>();
                            for (Map<String, String> map : updateTableMap) {
                                updateTypeIds.add(map.get("typeId"));
                            }
                            dataAcquisitionMapper.deleteByTypeIds(tableName, tenantCode, updateTypeIds);
                            dataAcquisitionMapper.insert(tableName, tenantCode, updateTableMap, timestamp);
                        }
                    }

                    if (!insertTableMap.isEmpty()) {
                        dataAcquisitionMapper.insert(tableName, tenantCode, insertTableMap, timestamp);
                    }
                }
                json = QueueUtils.rpop(topic);
            }
            apiRest = new ApiRest();
            apiRest.setMessage("执行成功！");
            apiRest.setIsSuccess(true);
        } catch (Exception e) {
            if (StringUtils.isNotEmpty(topic) && StringUtils.isNotEmpty(json)) {
                QueueUtils.lpush(topic, json);
            }
            throw e;
        }
        return apiRest;
    }
}
