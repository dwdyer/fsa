package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} used to order teams by form.
 * @author Daniel Dyer
 */
class FormTableComparator implements Comparator<Team>
{
    private final int pointsForWin;
    private final int pointsForDraw;
    private final int where;


    public FormTableComparator(int where,
                               int pointsForWin,
                               int pointsForDraw)
    {
        this.where = where;
        this.pointsForWin = pointsForWin;
        this.pointsForDraw = pointsForDraw;
    }


    public final int compare(Team team1, Team team2)
    {
        int compare = getPoints(where, team2) - getPoints(where, team1); // Swap teams for descending order.
        if (compare == 0)
        {
            compare = team2.getGoalDifference(where, true) - team1.getGoalDifference(where, true); // Swap teams for descending order.
            if (compare == 0)
            {
                compare = team2.getAggregate(where, Team.AGGREGATE_SCORED, true) - team1.getAggregate(where, Team.AGGREGATE_SCORED,
                                                                                                      true); // Swap teams for descending order.
                if (compare == 0)
                {
                    compare = team2.getAggregate(where, Team.AGGREGATE_WON, true) - team1.getAggregate(where, Team.AGGREGATE_WON,
                                                                                                       true); // Swap teams for descending order.
                    if (compare == 0)
                    {
                        // If records are the same, sort on alphabetical order.
                        compare = team1.getName().toLowerCase().compareTo(team2.getName().toLowerCase());
                    }
                }
            }
        }
        return compare;
    }


    private int getPoints(int where, Team team)
    {
        return team.getAggregate(where, Team.AGGREGATE_WON, true) * pointsForWin
               + team.getAggregate(where, Team.AGGREGATE_DRAWN, true) * pointsForDraw;
        // Don't apply points adjustments to form tables.
    }
}
