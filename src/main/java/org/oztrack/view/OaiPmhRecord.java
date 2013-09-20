package org.oztrack.view;

public class OaiPmhRecord {
    private String identifier;
    private String title;
    private String description;
    private String url;
    private String creator;
    private String createDate;
    private String updateDate;
    private String dcType;
    private String rifCsObjectElemName;
    private String rifCsObjectTypeAttr;
    private String rifCsGroup;
    public OaiPmhRecord() {
    }
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
    public String getDcType() {
        return dcType;
    }
    public void setDcType(String dcType) {
        this.dcType = dcType;
    }
    public String getRifCsObjectElemName() {
        return rifCsObjectElemName;
    }
    public void setRifCsObjectElemName(String rifCsObjectElemName) {
        this.rifCsObjectElemName = rifCsObjectElemName;
    }
    public String getRifCsObjectTypeAttr() {
        return rifCsObjectTypeAttr;
    }
    public void setRifCsObjectTypeAttr(String rifCsObjectTypeAttr) {
        this.rifCsObjectTypeAttr = rifCsObjectTypeAttr;
    }
    public String getRifCsGroup() {
        return rifCsGroup;
    }
    public void setRifCsGroup(String rifCsGroup) {
        this.rifCsGroup = rifCsGroup;
    }
}