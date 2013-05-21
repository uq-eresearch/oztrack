package org.oztrack.data.model.types;

public enum ArgosClass {
    CLASS_Z("Z", "Invalid location", null),
    CLASS_B("B", "No accuracy estimation (1 or 2 messages)", null),
    CLASS_A("A", "No accuracy estimation (3 messages)", null),
    CLASS_0("0", "Estimated error greater than 1500 m", null),
    CLASS_1("1", "Estimated error less than 1500 m", 1500d),
    CLASS_2("2", "Estimated error less than 500 m", 500d),
    CLASS_3("3", "Estimated error less than 250 m", 250d);

    private final String code;
    private final String description;
    private final Double radius;

    ArgosClass(String code, String description, Double radius) {
        this.code = code;
        this.description = description;
        this.radius = radius;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Double getRadius() {
        return radius;
    }

    public static ArgosClass fromCode(String code) {
        for (ArgosClass argosClass : values()) {
            if (argosClass.getCode().equals(code)) {
                return argosClass;
            }
        }
        return null;
    }
}
