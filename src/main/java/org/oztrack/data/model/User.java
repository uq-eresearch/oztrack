package org.oztrack.data.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;

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

    @NotNull
    @Column(unique = true)
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
        

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.user", cascade =
    {CascadeType.PERSIST, CascadeType.MERGE})
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
    org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
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



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public List<ProjectUser> getProjectUsers() {
        return this.projectUsers;
    }

    public void setProjectUsers(List<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
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
