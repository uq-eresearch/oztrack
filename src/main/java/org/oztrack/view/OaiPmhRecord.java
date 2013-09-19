package org.oztrack.view;

public class OaiPmhRecord {
    private final String identifier;
    private final String title;
    private final String description;
    private final String url;
    private final String creator;
    private final String createDate;
    private final String updateDate;
    private final String dcType;
    private final String rifCsObjectElemName;
    private final String rifCsObjectTypeAttrValue;
    private final String rifCsGroup;
    public OaiPmhRecord(
        String identifier,
        String title,
        String description,
        String url,
        String creator,
        String createDate,
        String updateDate,
        String dcType,
        String rifCsObjectElemName,
        String rifCsObjectTypeAttrValue,
        String rifCsGroup
    ) {
        this.identifier = identifier;
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.dcType = dcType;
        this.url = url;
        this.rifCsObjectElemName = rifCsObjectElemName;
        this.rifCsObjectTypeAttrValue = rifCsObjectTypeAttrValue;
        this.rifCsGroup = rifCsGroup;
    }
    public String getIdentifier() {
        return identifier;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getUrl() {
        return url;
    }
    public String getCreator() {
        return creator;
    }
    public String getCreateDate() {
        return createDate;
    }
    public String getUpdateDate() {
        return updateDate;
    }
    public String getDcType() {
        return dcType;
    }
    public String getRifCsObjectElemName() {
        return rifCsObjectElemName;
    }
    public String getRifCsObjectTypeAttrValue() {
        return rifCsObjectTypeAttrValue;
    }
    public String getRifCsGroup() {
        return rifCsGroup;
    }
}