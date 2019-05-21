package erp.chain.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询参数封装对象
 */

public class SearchModel extends Model {
    private Pager pager = new Pager();

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }


    public void setRows(Integer pageSize) {
        this.pager.setPageSize(pageSize);
    }

    public void setOrderProperty(String orderProperty) {
        pager.setOrderProperty(camelToUnderscore(orderProperty));
    }

    public void setOrder(String order) {
        pager.setOrder(order);
    }

    /**
     * 通过字符大写驼峰转为下划线
     */
    public static String camelToUnderscore(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile("[QWERTYUIOPLKJHGFDSAZXCVBNM]").matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() + (i++);
            sb.replace(position - 1, position, "_" + sb.substring(position -1, position).toLowerCase());
        }
        return sb.toString();
    }

    @NotNull
    @Min(1L)
//    @Max(200L)
    public Integer getRows() {
        return this.pager.getPageSize();
    }

    @NotNull
    @Min(1)
    public Integer getPage() {
        return this.pager.getCurrentPage();
    }

    public void setPage(Integer currentPage) {
        this.pager.setCurrentPage(currentPage);
    }

    public String like(String oldLike) {
        if (oldLike == null) {
            return null;
        }
        return String.format("%s%s%s", "%", mysqlEscapeSpecialCharacters(oldLike), "%");
    }

    public String prefixLike(String oldLike) {
        if (oldLike == null) {
            return null;
        }
        return String.format("%s%s", "%", mysqlEscapeSpecialCharacters(oldLike));
    }

    public String suffixLike(String oldLike) {
        if (oldLike == null) {
            return null;
        }
        return String.format("%s%s", mysqlEscapeSpecialCharacters(oldLike), "%");
    }

    /**
     * mysql字符串中特殊字符转义
     */
    public String mysqlEscapeSpecialCharacters(String val) {
        return val.replaceAll("\\\\", "\\\\\\\\").
                replaceAll("'", "\\\\'").replaceAll("%", "\\\\%").
                replaceAll("_", "\\\\_").replaceAll("\"", "\\\\\"");
    }


}
