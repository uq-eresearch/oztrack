package org.oztrack.data.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity(name="institution")
public class Institution extends OzTrackBaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="institution_id_seq")
    @SequenceGenerator(name="institution_id_seq", sequenceName="institution_id_seq", allocationSize=1)
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="title", unique=true, nullable=false)
    private String title;

    @Column(name="domainname", unique=true)
    private String domainName;

    @ManyToOne
    @JoinColumn(name="country_id")
    private Country country;

    @ManyToMany(fetch=FetchType.LAZY, mappedBy="institutions")
    private List<Person> people;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }
}
