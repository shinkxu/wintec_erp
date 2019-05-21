package erp.chain.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Created by liuyandong on 2016/11/25.
 */
public class ZTree {
    private String id;
    private String pId;
    private String name;
    private String title;
    private String url;
    private boolean nocheck = false;
    private boolean isParent = false;
    private String nodeType;
    private String memo;
    private Integer childNumber;
    private boolean chkDisabled;
    private boolean checked;
    private boolean open = false;
    private boolean isHidden = false;
    private String groupCode;

    public ZTree() {}

    public ZTree(String id, String pId, String name, Integer childNumber, boolean isParent, String memo) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.childNumber = childNumber;
        this.isParent = isParent;
        this.memo = memo;
    }

    public ZTree(String id, String pid, String name, String nodeType, boolean nocheck, boolean isParent) {
        this.id = id;
        this.pId = pid;
        this.name = name;
        this.nodeType = nodeType;
        this.nocheck = nocheck;
        this.isParent = isParent;
    }

    public ZTree(String id, String pId, String name, boolean nocheck, String nodeType) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.nocheck = nocheck;
        this.nodeType = nodeType;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        if (StringUtils.isNotBlank(title)) {
            return title;
        } else {
            return name;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isNocheck() {
        return nocheck;
    }

    public void setNocheck(boolean nocheck) {
        this.nocheck = nocheck;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getChildNumber() {
        return childNumber;
    }

    public void setChildNumber(Integer childNumber) {
        this.childNumber = childNumber;
    }

    public boolean isChkDisabled() {
        return chkDisabled;
    }

    public void setChkDisabled(boolean chkDisabled) {
        this.chkDisabled = chkDisabled;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }
}
