package net.footballpredictions.footballstats.model;

/**
 * @author Daniel Dyer
 */
public interface TeamRecord
{
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

    int getPoints();

    int getDroppedPoints();

    double getAveragePoints();

    /**
     * @return A String representation of this teams current form.  Either home form,
     * away form or combined form depending on the method argument.
     */
    String getForm();

    Team getTeam();

    /**
     * Used to apply points adjustments (positive or negative) made by the league
     * administrators for rules infringements.
     * @param amount A positive number means points have been awarded, a negative value
     * means points have been deducted.
     */
    void adjustPoints(int amount);
}
