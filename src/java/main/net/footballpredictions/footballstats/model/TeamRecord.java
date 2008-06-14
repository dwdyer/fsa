package net.footballpredictions.footballstats.model;

/**
 * @author Daniel Dyer
 */
public interface TeamRecord
{
    // Constants for home/away/both.
    int HOME = 0;
    int AWAY = 1;
    int BOTH = 2;

    String getName();

    void addResult(Result result);

    int getPlayed();

    int getWon();

    int getDrawn();

    int getLost();

    int getScored();

    int getConceded();

    /**
     * @return The difference between the number of goals scored by this team and the number
     * conceded.  A positive value indicates more goals scored than conceded, a negative value
     * indicates more conceded than scored.
     */
    int getGoalDifference();

    /**
     * @return A String representation of this teams current form.  Either home form,
     * away form or combined form depending on the method argument.
     */
    String getForm();

    Team getTeam();
}
