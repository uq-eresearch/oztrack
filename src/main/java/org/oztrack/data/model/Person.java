package org.oztrack.data.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.oztrack.data.model.types.Personable;

@Entity(name="person")
public class Person extends OzTrackBaseEntity implements Personable {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="person_id_seq")
    @SequenceGenerator(name="person_id_seq", sequenceName="person_id_seq", allocationSize=1)
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="uuid", unique=true, nullable=false)
    @org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
    private UUID uuid;

    @Column(name="email", unique=true, nullable=false)
    private String email;

    @Column(name="title")
    private String title;

    @Column(name="firstName")
    private String firstName;

    @Column(name="lastName")
    private String lastName;

    @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    @JoinTable(
        name="person_institution",
        joinColumns=@JoinColumn(name="person_id"),
        inverseJoinColumns=@JoinColumn(name="institution_id")
    )
    @OrderColumn(name="ordinal", nullable=false)
    private List<Institution> institutions;

    @OneToMany(mappedBy="contributor", fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval=true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<ProjectContribution> projectContributions = new LinkedList<ProjectContribution>();

    @Column(name="description")
    private String description;

    @Column(name="dataspaceagenturi")
    private String dataSpaceAgentURI;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dataspaceagentupdatedate")
    private Date dataSpaceAgentUpdateDate;

    @OneToOne(mappedBy="person", fetch=FetchType.EAGER)
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public List<Institution> getInstitutions() {
        return institutions;
    }

    @Override
    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    public List<ProjectContribution> getProjectContributions() {
        return projectContributions;
    }

    public void setProjectContributions(List<ProjectContribution> projectContributions) {
        this.projectContributions = projectContributions;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDataSpaceAgentURI() {
        return dataSpaceAgentURI;
    }

    @Override
    public void setDataSpaceAgentURI(String dataSpaceAgentURI) {
        this.dataSpaceAgentURI = dataSpaceAgentURI;
    }

    @Override
    public Date getDataSpaceAgentUpdateDate() {
        return dataSpaceAgentUpdateDate;
    }

    @Override
    public void setDataSpaceAgentUpdateDate(Date dataSpaceAgentUpdateDate) {
        this.dataSpaceAgentUpdateDate = dataSpaceAgentUpdateDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
