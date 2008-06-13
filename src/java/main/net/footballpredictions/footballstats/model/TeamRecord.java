package net.footballpredictions.footballstats.model;

/**
 * @author Daniel Dyer
 */
public interface TeamRecord
{
    // Constants for home/away/both.
    int HOME = 0;
    int AWAY = 1;
    int BOTH = 2;// Constants for standards stats.
    int AGGREGATE_PLAYED = 0;
    int AGGREGATE_WON = 1;
    int AGGREGATE_DRAWN = 2;
    int AGGREGATE_LOST = 3;
    int AGGREGATE_SCORED = 4;
    int AGGREGATE_CONCEDED = 5;

    String getName();

    void addResult(Result result);

    int getAggregate(int where, int aggregate);

    /**
     * @return The difference between the number of goals scored by this team and the number
     * conceded.  A positive value indicates more goals scored than conceded, a negative value
     * indicates more conceded than scored.
     */
    int getGoalDifference(int where);

    /**
     * @return A String representation of this teams current form.  Either home form,
     * away form or combined form depending on the method argument.
     */
    String getForm(int where);


    int getPointsAdjustment(int where);
}
