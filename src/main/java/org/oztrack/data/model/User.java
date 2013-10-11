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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.oztrack.data.model.types.Personable;

@Entity(name="appuser")
public class User extends OzTrackBaseEntity implements Personable {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="userid_seq")
    @SequenceGenerator(name="userid_seq", sequenceName="userid_seq", allocationSize=1)
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="username", unique=true, nullable=false)
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="passwordresettoken", unique=true)
    private String passwordResetToken;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="passwordresetexpiresat")
    private Date passwordResetExpiresAt;

    @Column(name="aafid")
    private String aafId;

    @Column(name="admin")
    private Boolean admin;

    @OneToOne(fetch=FetchType.EAGER, cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, orphanRemoval=false)
    @JoinColumn(name="person_id", nullable=false)
    private Person person = new Person();

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

    @Override
    public void setCreateDate(Date createDate) {
        super.setCreateDate(createDate);
        person.setCreateDate(createDate);
    }

    @Override
    public void setCreateUser(User createUser) {
        super.setCreateUser(createUser);
        person.setCreateUser(this);
    }

    @Override
    public void setUpdateDate(Date updateDate) {
        super.setUpdateDate(updateDate);
        person.setUpdateDate(updateDate);
    }

    @Override
    public void setUpdateUser(User updateUser) {
        super.setUpdateUser(updateUser);
        person.setUpdateUser(updateUser);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAafId() {
        return aafId;
    }

    public void setAafId(String aafId) {
        this.aafId = aafId;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<ProjectUser> getProjectUsers() {
        return this.projectUsers;
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
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

    @Override
    public String getEmail() {
        return person.getEmail();
    }

    @Override
    public void setEmail(String email) {
        person.setEmail(email);
    }

    @Override
    public String getTitle() {
        return person.getTitle();
    }

    @Override
    public void setTitle(String title) {
        person.setTitle(title);
    }

    @Override
    public String getFirstName() {
        return person.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        person.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        return person.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        person.setLastName(lastName);
    }

    @Override
    public List<Institution> getInstitutions() {
        return person.getInstitutions();
    }

    @Override
    public void setInstitutions(List<Institution> institutions) {
        person.setInstitutions(institutions);
    }

    @Override
    public String getFullName() {
        return person.getFullName();
    }

    @Override
    public String getDataSpaceAgentURI() {
        return person.getDataSpaceAgentURI();
    }

    @Override
    public void setDataSpaceAgentURI(String dataSpaceAgentURI) {
        person.setDataSpaceAgentURI(dataSpaceAgentURI);
    }

    @Override
    public String getDescription() {
        return person.getDescription();
    }

    @Override
    public void setDescription(String description) {
        person.setDescription(description);
    }

    @Override
    public Date getDataSpaceAgentUpdateDate() {
        return person.getDataSpaceAgentUpdateDate();
    }

    @Override
    public void setDataSpaceAgentUpdateDate(Date dataSpaceAgentUpdateDate) {
        person.setDataSpaceAgentUpdateDate(dataSpaceAgentUpdateDate);
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
}
