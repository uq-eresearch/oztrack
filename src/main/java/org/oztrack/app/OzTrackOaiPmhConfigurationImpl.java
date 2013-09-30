package org.oztrack.app;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OzTrackOaiPmhConfigurationImpl implements OzTrackOaiPmhConfiguration {
    private final SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private boolean oaiPmhEnabled;

    private String oaiPmhRecordIdentifierPrefix;
    private String rifCsRecordIdentifierPrefix;
    private String objectIdentifierPrefix;
    private String rifCsGroup;

    private String repositoryCreator;
    private String repositoryServiceTitle;
    private String repositoryServiceDescription;
    private Date repositoryServiceCreateDate;
    private Date repositoryServiceUpdateDate;

    private String oaiPmhServiceTitle;
    private String oaiPmhServiceDescription;
    private Date oaiPmhServiceCreateDate;
    private Date oaiPmhServiceUpdateDate;
    private String oaiPmhServiceAdminEmail;

    private String repositoryCollectionTitle;
    private String repositoryCollectionDescription;
    private String repositoryCollectionRightsStatement;
    private Date repositoryCollectionCreateDate;
    private Date repositoryCollectionUpdateDate;

    @Override
    public boolean isOaiPmhEnabled() {
        return oaiPmhEnabled;
    }

    public void setOaiPmhEnabled(boolean oaiPmhEnabled) {
        this.oaiPmhEnabled = oaiPmhEnabled;
    }

    @Override
    public String getOaiPmhRecordIdentifierPrefix() {
        return oaiPmhRecordIdentifierPrefix;
    }

    public void setOaiPmhRecordIdentifierPrefix(String oaiPmhRecordIdentifierPrefix) {
        this.oaiPmhRecordIdentifierPrefix = oaiPmhRecordIdentifierPrefix;
    }

    @Override
    public String getRifCsRecordIdentifierPrefix() {
        return rifCsRecordIdentifierPrefix;
    }

    public void setRifCsRecordIdentifierPrefix(String rifCsRecordIdentifierPrefix) {
        this.rifCsRecordIdentifierPrefix = rifCsRecordIdentifierPrefix;
    }

    @Override
    public String getObjectIdentifierPrefix() {
        return objectIdentifierPrefix;
    }

    public void setObjectIdentifierPrefix(String objectIdentifierPrefix) {
        this.objectIdentifierPrefix = objectIdentifierPrefix;
    }

    @Override
    public String getRifCsGroup() {
        return rifCsGroup;
    }

    public void setRifCsGroup(String rifCsGroup) {
        this.rifCsGroup = rifCsGroup;
    }

    @Override
    public String getRepositoryCreator() {
        return repositoryCreator;
    }

    public void setRepositoryCreator(String repositoryCreator) {
        this.repositoryCreator = repositoryCreator;
    }

    @Override
    public String getRepositoryServiceTitle() {
        return repositoryServiceTitle;
    }

    public void setRepositoryServiceTitle(String repositoryServiceTitle) {
        this.repositoryServiceTitle = repositoryServiceTitle;
    }

    @Override
    public String getRepositoryServiceDescription() {
        return repositoryServiceDescription;
    }

    public void setRepositoryServiceDescription(String repositoryServiceDescription) {
        this.repositoryServiceDescription = repositoryServiceDescription;
    }

    @Override
    public Date getRepositoryServiceCreateDate() {
        return repositoryServiceCreateDate;
    }

    public void setRepositoryServiceCreateDate(Date repositoryServiceCreateDate) {
        this.repositoryServiceCreateDate = repositoryServiceCreateDate;
    }

    public void setRepositoryServiceCreateDateString(String repositoryServiceCreateDateString) throws ParseException {
        this.repositoryServiceCreateDate = utcDateTimeFormat.parse(repositoryServiceCreateDateString);
    }

    @Override
    public Date getRepositoryServiceUpdateDate() {
        return repositoryServiceUpdateDate;
    }

    public void setRepositoryServiceUpdateDate(Date repositoryServiceUpdateDate) {
        this.repositoryServiceUpdateDate = repositoryServiceUpdateDate;
    }

    public void setRepositoryServiceUpdateDateString(String repositoryServiceUpdateDateString) throws ParseException {
        this.repositoryServiceUpdateDate = utcDateTimeFormat.parse(repositoryServiceUpdateDateString);
    }

    @Override
    public String getOaiPmhServiceTitle() {
        return oaiPmhServiceTitle;
    }

    public void setOaiPmhServiceTitle(String oaiPmhServiceTitle) {
        this.oaiPmhServiceTitle = oaiPmhServiceTitle;
    }

    @Override
    public String getOaiPmhServiceDescription() {
        return oaiPmhServiceDescription;
    }

    public void setOaiPmhServiceDescription(String oaiPmhServiceDescription) {
        this.oaiPmhServiceDescription = oaiPmhServiceDescription;
    }

    @Override
    public Date getOaiPmhServiceCreateDate() {
        return oaiPmhServiceCreateDate;
    }

    public void setOaiPmhServiceCreateDate(Date oaiPmhServiceCreateDate) {
        this.oaiPmhServiceCreateDate = oaiPmhServiceCreateDate;
    }

    public void setOaiPmhServiceCreateDateString(String oaiPmhServiceCreateDateString) throws ParseException {
        this.oaiPmhServiceCreateDate = utcDateTimeFormat.parse(oaiPmhServiceCreateDateString);
    }

    @Override
    public Date getOaiPmhServiceUpdateDate() {
        return oaiPmhServiceUpdateDate;
    }

    public void setOaiPmhServiceUpdateDate(Date oaiPmhServiceUpdateDate) {
        this.oaiPmhServiceUpdateDate = oaiPmhServiceUpdateDate;
    }

    public void setOaiPmhServiceUpdateDateString(String oaiPmhServiceUpdateDateString) throws ParseException {
        this.oaiPmhServiceUpdateDate = utcDateTimeFormat.parse(oaiPmhServiceUpdateDateString);
    }

    @Override
    public String getOaiPmhServiceAdminEmail() {
        return oaiPmhServiceAdminEmail;
    }

    public void setOaiPmhServiceAdminEmail(String oaiPmhServiceAdminEmail) {
        this.oaiPmhServiceAdminEmail = oaiPmhServiceAdminEmail;
    }

    @Override
    public String getRepositoryCollectionTitle() {
        return repositoryCollectionTitle;
    }

    public void setRepositoryCollectionTitle(String repositoryCollectionTitle) {
        this.repositoryCollectionTitle = repositoryCollectionTitle;
    }

    @Override
    public String getRepositoryCollectionDescription() {
        return repositoryCollectionDescription;
    }

    public void setRepositoryCollectionDescription(String repositoryCollectionDescription) {
        this.repositoryCollectionDescription = repositoryCollectionDescription;
    }

    @Override
    public String getRepositoryCollectionRightsStatement() {
        return repositoryCollectionRightsStatement;
    }

    public void setRepositoryCollectionRightsStatement(String repositoryCollectionRightsStatement) {
        this.repositoryCollectionRightsStatement = repositoryCollectionRightsStatement;
    }

    @Override
    public Date getRepositoryCollectionCreateDate() {
        return repositoryCollectionCreateDate;
    }

    public void setRepositoryCollectionCreateDate(Date repositoryCollectionCreateDate) {
        this.repositoryCollectionCreateDate = repositoryCollectionCreateDate;
    }

    @Override
    public Date getRepositoryCollectionUpdateDate() {
        return repositoryCollectionUpdateDate;
    }

    public void setRepositoryCollectionCreateDateString(String repositoryCollectionCreateDateString) throws ParseException {
        this.repositoryCollectionCreateDate = utcDateTimeFormat.parse(repositoryCollectionCreateDateString);
    }

    public void setRepositoryCollectionUpdateDate(Date repositoryCollectionUpdateDate) {
        this.repositoryCollectionUpdateDate = repositoryCollectionUpdateDate;
    }

    public void setRepositoryCollectionUpdateDateString(String repositoryCollectionUpdateDateString) throws ParseException {
        this.repositoryCollectionUpdateDate = utcDateTimeFormat.parse(repositoryCollectionUpdateDateString);
    }
}
