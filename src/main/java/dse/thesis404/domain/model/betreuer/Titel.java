package dse.thesis404.domain.model.betreuer;

public enum Titel {
    NONE(""),
    PROF("Prof."),
    DR("Dr."),
    PROF_DR("Prof. Dr.");

    private final String displayName;

    Titel(String displayName) {
        this.displayName = displayName;
    }

    
    public String getDisplayName() {
        return displayName;
    }

    
    public String getDisplayNameWithSpace() {
        return displayName.isEmpty() ? "" : displayName + " ";
    }
}
