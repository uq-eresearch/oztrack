package org.oztrack.data.model;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updatedateforoaipmh")
    private Date updateDateForOaiPmh;

    @Column(name="includeinoaipmh")
    private boolean includeInOaiPmh;

    @ElementCollection(fetch=FetchType.LAZY)
    @CollectionTable(name="institution_oaipmhset", joinColumns=@JoinColumn(name="institution_id"))
    @Column(name="oaipmhset")
    @Sort(type=SortType.NATURAL)
    private SortedSet<String> oaiPmhSets;

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

    public Date getUpdateDateForOaiPmh() {
        return updateDateForOaiPmh;
    }

    public void setUpdateDateForOaiPmh(Date updateDateForOaiPmh) {
        this.updateDateForOaiPmh = updateDateForOaiPmh;
    }

    public boolean getIncludeInOaiPmh() {
        return includeInOaiPmh;
    }

    public void setIncludeInOaiPmh(boolean includeInOaiPmh) {
        this.includeInOaiPmh = includeInOaiPmh;
    }

    public SortedSet<String> getOaiPmhSets() {
        return oaiPmhSets;
    }

    public void setOaiPmhSets(SortedSet<String> oaiPmhSets) {
        this.oaiPmhSets = oaiPmhSets;
    }
}
