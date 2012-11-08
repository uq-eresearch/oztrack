package org.oztrack.data.model.types;

public enum Role {
    MANAGER(
        "manager",
        "Manager",
        "Managers",
        "Managers can read and write data in the project and assign roles to other users."
    ),
    WRITER(
        "writer",
        "Writer",
        "Writers",
        "Writers can read, upload, and modify data in the project."
    ),
    READER(
        "reader",
        "Reader",
        "Readers",
        "Readers can access data in restricted projects."
    );

    private String identifier;
    private String title;
    private String pluralTitle;
    private String explanation;

    private Role(String identifier, String title, String pluralTitle, String explanation) {
        this.identifier = identifier;
        this.title = title;
        this.pluralTitle = pluralTitle;
        this.explanation = explanation;
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

    public String getExplanation() {
        return explanation;
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