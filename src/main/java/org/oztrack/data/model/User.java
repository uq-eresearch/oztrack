package org.oztrack.data.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

@Entity(name="appuser")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="userid_seq")
    @SequenceGenerator(name="userid_seq", sequenceName="userid_seq", allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @Column(unique=true, nullable=false)
    private String username;

    @Column(unique=true, nullable=false)
    private String email;

    private String title;
    private String firstName;
    private String lastName;
    private String organisation;
    private String password;
    private Boolean admin;
    private String aafId;

    private String dataSpaceAgentURI;
    private String dataSpaceAgentDescription;
    private Date dataSpaceAgentUpdateDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name="passwordresettoken", unique=true)
    private String passwordResetToken;

    @Column(name="passwordresetexpiresat")
    private Date passwordResetExpiresAt;

    @OneToMany(mappedBy="user", fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval=true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<ProjectUser> projectUsers = new LinkedList<ProjectUser>();

    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="appuser_logindate", joinColumns=@JoinColumn(name="user_id"))
    @Column(name="logindate")
    @Sort(type=SortType.NATURAL)
    private SortedSet<Date> loginDates;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        return getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return (getId() != null) ? getId().intValue() : super.hashCode();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public String getAafId() {
        return aafId;
    }

    public void setAafId(String aafId) {
        this.aafId = aafId;
    }

    public List<ProjectUser> getProjectUsers() {
        return this.projectUsers;
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public String getDataSpaceAgentURI() {
        return dataSpaceAgentURI;
    }

    public void setDataSpaceAgentURI(String dataSpaceAgentURI) {
        this.dataSpaceAgentURI = dataSpaceAgentURI;
    }

    public String getDataSpaceAgentDescription() {
        return dataSpaceAgentDescription;
    }

    public void setDataSpaceAgentDescription(String dataSpaceAgentDescription) {
        this.dataSpaceAgentDescription = dataSpaceAgentDescription;
    }

    public Date getDataSpaceAgentUpdateDate() {
        return dataSpaceAgentUpdateDate;
    }

    public void setDataSpaceAgentUpdateDate(Date dataSpaceAgentUpdateDate) {
        this.dataSpaceAgentUpdateDate = dataSpaceAgentUpdateDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public Date getPasswordResetExpiresAt() {
        return passwordResetExpiresAt;
    }

    public void setPasswordResetExpiresAt(Date passwordResetExpiresAt) {
        this.passwordResetExpiresAt = passwordResetExpiresAt;
    }

    public SortedSet<Date> getLoginDates() {
        return loginDates;
    }

    public Date getLastLoginDate() {
        return loginDates.isEmpty() ? null : loginDates.last();
    }

    public void setLoginDates(SortedSet<Date> loginDates) {
        this.loginDates = loginDates;
    }
}
