package org.oztrack.data.model.types;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.Range;

import com.vividsolutions.jts.geom.Envelope;

public class OaiPmhRecord {
    public static class Relation {
        private String relationType;
        private String relatedRifCsRecordIdentifier;
        private String relatedObjectIdentifier;
        public Relation(String relationType, String relatedRifCsRecordIdentifier, String relatedObjectIdentifier) {
            this.relationType = relationType;
            this.relatedRifCsRecordIdentifier = relatedRifCsRecordIdentifier;
            this.relatedObjectIdentifier = relatedObjectIdentifier;
        }
        public String getRelationType() {
            return relationType;
        }
        public void setRelationType(String relationType) {
            this.relationType = relationType;
        }
        public String getRelatedRifCsRecordIdentifier() {
            return relatedRifCsRecordIdentifier;
        }
        public void setRelatedRifCsRecordIdentifier(String relatedRifCsRecordIdentifier) {
            this.relatedRifCsRecordIdentifier = relatedRifCsRecordIdentifier;
        }
        public String getRelatedObjectIdentifier() {
            return relatedObjectIdentifier;
        }
        public void setRelatedObjectIdentifier(String relatedObjectIdentifier) {
            this.relatedObjectIdentifier = relatedObjectIdentifier;
        }
    }
    public static class Subject {
        private String subjectType;
        private String subjectText;
        public Subject(String subjectType, String subjectText) {
            this.subjectType = subjectType;
            this.subjectText = subjectText;
        }
        public String getSubjectType() {
            return subjectType;
        }
        public void setSubjectType(String subjectType) {
            this.subjectType = subjectType;
        }
        public String getSubjectText() {
            return subjectText;
        }
        public void setSubjectText(String subjectText) {
            this.subjectText = subjectText;
        }
    }
    private String oaiPmhRecordIdentifier;
    private String rifCsRecordIdentifier;
    private String objectIdentifier;
    private String title;
    private String description;
    private String url;
    private String creator;
    private Date createDate;
    private Date updateDate;
    private Range<Date> temporalCoverage;
    private Envelope spatialCoverage;
    private List<Relation> relations;
    private List<Subject> subjects;
    private String dcType;
    private String rifCsObjectElemName;
    private String rifCsObjectTypeAttr;
    private String rifCsGroup;
    public OaiPmhRecord() {
    }
    public String getOaiPmhRecordIdentifier() {
        return oaiPmhRecordIdentifier;
    }
    public void setOaiPmhRecordIdentifier(String oaiPmhRecordIdentifier) {
        this.oaiPmhRecordIdentifier = oaiPmhRecordIdentifier;
    }
    public String getRifCsRecordIdentifier() {
        return rifCsRecordIdentifier;
    }
    public void setRifCsRecordIdentifier(String rifCsRecordIdentifier) {
        this.rifCsRecordIdentifier = rifCsRecordIdentifier;
    }
    public String getObjectIdentifier() {
        return objectIdentifier;
    }
    public void setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
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
    public Range<Date> getTemporalCoverage() {
        return temporalCoverage;
    }
    public void setTemporalCoverage(Range<Date> temporalCoverage) {
        this.temporalCoverage = temporalCoverage;
    }
    public Envelope getSpatialCoverage() {
        return spatialCoverage;
    }
    public void setSpatialCoverage(Envelope spatialCoverage) {
        this.spatialCoverage = spatialCoverage;
    }
    public List<Relation> getRelations() {
        return relations;
    }
    public void setRelations(List<Relation> relations) {
        this.relations = relations;
    }
    public List<Subject> getSubjects() {
        return subjects;
    }
    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
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