package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity(name="country")
public class Country {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="country_id_seq")
    @SequenceGenerator(name="country_id_seq", sequenceName="country_id_seq", allocationSize=1)
    @Column(name="id", nullable=false)
    private Long id;

    @Column(name="code", unique=true, nullable=false)
    private String code;

    @Column(name="title", unique=true, nullable=false)
    private String title;

    @Column(name="domainname", unique=true)
    private String domainName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
