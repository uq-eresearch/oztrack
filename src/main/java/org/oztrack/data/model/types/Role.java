package org.oztrack.data.model.types;

public enum Role {
    MANAGER("manager", "Manager", "Managers"),
    WRITER("writer", "Writer", "Writers"),
    READER("reader", "Reader", "Readers");
    
    private String identifier;
    private String title;
    private String pluralTitle;
    
    private Role(String identifier, String title, String pluralTitle) {
        this.identifier = identifier;
        this.title = title;
        this.pluralTitle = pluralTitle;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    public String getTitle() {
        return title;
    }

    public String getPluralTitle() {
        return pluralTitle;
    }
    
    public static Role fromIdentifier(String identifier) {
        for (Role role : Role.values()) {
            if (role.identifier.equals(identifier)) {
                return role;
            }
        }
        return null;
    }
}