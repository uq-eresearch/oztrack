package org.oztrack.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Cascade;

/**
 * Author: alabri
 * Date: 10/11/2010
 * Time: 3:02:42 PM
 */

@Entity(name = "AppUser")
public class User implements Serializable {

	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userid_seq")
    @SequenceGenerator(name = "userid_seq", sequenceName = "userid_seq", allocationSize = 1)
    @Column(nullable=false)
    private Long id;

    @Column(unique = true, nullable=false)
    private String username;
    private String email;
    private String title;
    private String firstName;
    private String lastName;
    private String organisation;
    private String password;
    private Boolean admin;
    private String dataSpaceAgentURI;
    private String dataSpaceAgentDescription;
    private Date dataSpaceAgentUpdateDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.user", cascade =
    {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval=true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<ProjectUser> projectUsers = new LinkedList<ProjectUser>();

    /*
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "id")
    private Set<Project> projects = new HashSet<Project>();
    */

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

    /*
    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
    */
}
