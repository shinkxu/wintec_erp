package erp.chain.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * hxh
 * 2016/5/7.
 */

public class Pager implements Serializable {
    public static final int DEFAULT_PAGE_SIZE = 20;
    /**
     * 结果集
     */
    private List rows = new ArrayList<>();
    /**
     * 合计集
     */
    private List footer = new ArrayList<>();
    /**
     * 记录总数
     */
    private long total = 0;
    /**
     * 当前页
     */
    private Integer currentPage = 1;
    /**
     * 每页记录数,默认20条
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;
    /**
     * 排序字段名称,多个字段中间使用,分隔
     */
    private String orderProperty;
    /**
     * 排序方式asc或desc,多个字段中间使用,分隔
     */
    private String order;


    public Integer getFirstPage() {
        return (this.currentPage - 1) * this.pageSize;
    }

    public Integer getLastPage() {
        return this.pageSize;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public List getFooter() {
        return footer;
    }

    public void setFooter(List footer) {
        this.footer = footer;
    }
    public void setFooter(Object footer) {
        List<Object> list = new ArrayList <>();
        list.add(footer);
        this.footer = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
//        if (currentPage <= 0) {
//            this.currentPage = 1;
//        } else {
//            this.currentPage = currentPage;
//        }
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
//        if (pageSize <= 0) {
//            this.pageSize = 1;
//        } else {
//        }
        this.pageSize = pageSize;
    }

    public String getOrderProperty() {
        return orderProperty;
    }

    public void setOrderProperty(String orderProperty) {
        this.orderProperty = orderProperty;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> res = new HashMap<>();
        res.put("total", this.total);
        res.put("rows", this.rows);
        res.put("footer", this.footer);
        return res;
    }
}
