package net.footballpredictions.footballstats.model;

/**
 * @author Daniel Dyer
 */
public enum VenueType
{
    HOME("Home "),
    AWAY("Away "),
    BOTH("");

    private final String description;

    private VenueType(String description)
    {
        this.description = description;
    }


    public String getDescription()
    {
        return description;
    }
}
