package org.oztrack.data.model.types;

import java.util.Date;

public class OaiPmhRecord {
    private String oaiPmhIdentifier;
    private String objectIdentifier;
    private String isPartOfObjectIdentifier;
    private String isPresentedByObjectIdentifier;
    private String isAvailableThroughObjectIdentifier;
    private String title;
    private String description;
    private String url;
    private String creator;
    private Date createDate;
    private Date updateDate;
    private String dcType;
    private String rifCsObjectElemName;
    private String rifCsObjectTypeAttr;
    private String rifCsGroup;
    public OaiPmhRecord() {
    }
    public String getOaiPmhIdentifier() {
        return oaiPmhIdentifier;
    }
    public void setOaiPmhIdentifier(String oaiPmhIdentifier) {
        this.oaiPmhIdentifier = oaiPmhIdentifier;
    }
    public String getObjectIdentifier() {
        return objectIdentifier;
    }
    public void setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }
    public String getIsPartOfObjectIdentifier() {
        return isPartOfObjectIdentifier;
    }
    public void setIsPartOfObjectIdentifier(String isPartOfObjectIdentifier) {
        this.isPartOfObjectIdentifier = isPartOfObjectIdentifier;
    }
    public String getIsPresentedByObjectIdentifier() {
        return isPresentedByObjectIdentifier;
    }
    public void setIsPresentedByObjectIdentifier(String isPresentedByObjectIdentifier) {
        this.isPresentedByObjectIdentifier = isPresentedByObjectIdentifier;
    }
    public String getIsAvailableThroughObjectIdentifier() {
        return isAvailableThroughObjectIdentifier;
    }
    public void setIsAvailableThroughObjectIdentifier(String isAvailableThroughObjectIdentifier) {
        this.isAvailableThroughObjectIdentifier = isAvailableThroughObjectIdentifier;
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
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
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