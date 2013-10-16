package org.oztrack.data.model.types;

public class OaiPmhSet {
    private String setSpec;
    private String setName;
    public OaiPmhSet(String setSpec, String setName) {
        this.setSpec = setSpec;
        this.setName = setName;
    }
    public String getSetSpec() {
        return setSpec;
    }
    public void setSetSpec(String setSpec) {
        this.setSpec = setSpec;
    }
    public String getSetName() {
        return setName;
    }
    public void setSetName(String setName) {
        this.setName = setName;
    }
}
