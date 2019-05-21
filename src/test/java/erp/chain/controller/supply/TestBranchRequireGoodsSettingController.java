package erp.chain.controller.supply;


import com.fasterxml.jackson.core.JsonProcessingException;
import erp.chain.Application;
import erp.chain.utils.DataSet;
import erp.chain.utils.JsonDateSetTest;
import erp.chain.utils.BeanUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


/**
 * BranchRequireGoodsSettingController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@TransactionConfiguration
@DataSet(key = "123", path = "baseWS.properties")
public class TestBranchRequireGoodsSettingController extends JsonDateSetTest {

    MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationConnect;

    @Before
    public void setUp() throws JsonProcessingException {
//        mvc = MockMvcBuilders.standaloneSetup(new BranchRequireGoodsSettingController()).build();
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationConnect).build();
    }

    @Test
    public void testSave() throws Exception {
        String uri = "/branchRequireGoodsSetting/save";
        Map<String, Object> para = new HashMap<>();
        Map<String, Object> goods = new HashMap<>();
        List list = new ArrayList();
        para.put("tenantId", 439);
        para.put("branchId", 17);
        para.put("distributionCenterId", 772);
        para.put("empId", 12);
        para.put("empId", 12);
        list.add(goods);
        para.put("details", list);
        goods.put("price", 123.3);
        goods.put("goodsId", 866);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).content(BeanUtils.toJsonStr(para).getBytes("utf-8")))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String context = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("错误，正确的返回值为200", status == 200);
    }

    @Test
    public void testGet() throws Exception {
        String uri = "/branchRequireGoodsSetting/get?id=-99999901";

        mvc.perform(get(uri))
                .andExpect(status().isOk())
//                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.id").value(-99999901))
        ;
//        int status = mvcResult.getResponse().getStatus();
//        String context = mvcResult.getResponse().getContentAsString();
//        Assert.assertTrue("错误，正确的返回值为200", status == 200);
        mvc.perform(get(uri)).andReturn();
    }

    @Test
    public void testUpdate() throws Exception {
        String uri = "/branchRequireGoodsSetting/update?id=-99999901&empId=12&version=0&price=123";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String context = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("错误，正确的返回值为200", status == 200);
//        testGet();
    }

    @Test
    public void testDel() throws Exception {
        String uri = "/branchRequireGoodsSetting/delete?id=-99999901&empId=12&version=0";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String context = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("错误，正确的返回值为200", status == 200);
//        testGet();
    }

    @Test
    public void testPager() throws Exception {
        String uri = "/branchRequireGoodsSetting/queryPager?tenantId=439&distributionCenterId=772&branchIds=1,17&orderProperty=create_at&order=desc";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String context = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("错误，正确的返回值为200", status == 200);
    }

    @Test
    public void testYhPager() throws Exception {
        String uri = "/yhOrder/queryYhOrderPager?tenantId=439&distributionCenterId=772&branchIds=1,17&orderProperty=create_at&order=desc";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String context = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("错误，正确的返回值为200", status == 200);
    }

    @Test
    public void testQueryYhGoodsSumPager() throws Exception {
        String uri = "/yhOrder/queryYhGoodsSumPager?tenantId=439&psBranchId=772&startDate=2016-10-10&endDate=2017-10-10";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        String context = mvcResult.getResponse().getContentAsString();
        Assert.assertTrue("错误，正确的返回值为200", status == 200);
    }

}
