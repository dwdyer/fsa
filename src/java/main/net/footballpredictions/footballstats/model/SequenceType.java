package net.footballpredictions.footballstats.model;

/**
 * @author Daniel Dyer
 */
public enum SequenceType
{
    WINS("Wins"),
    DRAWS("Draws"),
    DEFEATS("Defeats"),
    UNBEATEN("Games Without Defeat"),
    NO_WIN("Games Without Winning"),
    CLEANSHEETS("Cleansheets"),
    GAMES_SCORED_IN("Games Scored In"),
    GAMES_NOT_SCORED_IN("Games Without Scoring");
    
    private final String description;

    SequenceType(String description)
    {
        this.description = description;
    }


    @Override
    public String toString()
    {
        return description;
    }
}
