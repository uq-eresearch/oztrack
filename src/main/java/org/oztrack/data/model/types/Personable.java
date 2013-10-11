package org.oztrack.data.model.types;

import java.util.Date;
import java.util.List;

import org.oztrack.data.model.Institution;

// At the moment, we delegate a bunch of getter/setters from User to Person.
// This interface aims to ensure that we maintain a consistent set of methods.
// Ultimately, we might merge User into Person or have User extend Person.
public interface Personable {
    public String getEmail();
    public void setEmail(String email);
    public String getTitle();
    public void setTitle(String title);
    public String getFirstName();
    public void setFirstName(String firstName);
    public String getLastName();
    public void setLastName(String lastName);
    public List<Institution> getInstitutions();
    public void setInstitutions(List<Institution> institutions);
    public String getFullName();
    public String getDataSpaceAgentURI();
    public void setDataSpaceAgentURI(String dataSpaceAgentURI);
    public String getDescription();
    public void setDescription(String description);
    public Date getDataSpaceAgentUpdateDate();
    public void setDataSpaceAgentUpdateDate(Date dataSpaceAgentUpdateDate);
}
