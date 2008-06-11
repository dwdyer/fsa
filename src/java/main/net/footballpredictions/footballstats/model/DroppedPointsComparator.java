package net.footballpredictions.footballstats.model;

import java.util.Comparator;


/**
 * {@link Comparator} for sorting a league table in order of fewest points dropped.
 * @author Daniel Dyer
 */
class DroppedPointsComparator implements Comparator<Team>
{
    private final int pointsForWin;
    private final int pointsForDraw;
    private final int where;


    public DroppedPointsComparator(int where,
                                   int pointsForWin,
                                   int pointsForDraw)
    {
        this.where = where;
        this.pointsForWin = pointsForWin;
        this.pointsForDraw = pointsForDraw;
    }


    public final int compare(Team team1, Team team2)
    {
        int compare = getPointsDropped(where, team1) - getPointsDropped(where, team2); // Swap teams for descending order.
        if (compare == 0)
        {
            compare = team2.getGoalDifference(where, false) - team1.getGoalDifference(where, false); // Swap teams for descending order.
            if (compare == 0)
            {
                compare = team2.getAggregate(where, Team.AGGREGATE_SCORED, false) - team1.getAggregate(where, Team.AGGREGATE_SCORED,
                                                                                                       false); // Swap teams for descending order.
                if (compare == 0)
                {
                    compare = team2.getAggregate(where, Team.AGGREGATE_WON, false) - team1.getAggregate(where, Team.AGGREGATE_WON,
                                                                                                        false); // Swap teams for descending order.
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


    private int getPointsDropped(int where, Team team)
    {
        return team.getAggregate(where, Team.AGGREGATE_PLAYED, false) * pointsForWin - getPoints(where, team);
    }


    private int getPoints(int where, Team team)
    {
        int points = team.getAggregate(where, Team.AGGREGATE_WON, false) * pointsForWin
                     + team.getAggregate(where, Team.AGGREGATE_DRAWN, false) * pointsForDraw;
        points += team.getPointsAdjustment(where);
        return points;
    }
}
