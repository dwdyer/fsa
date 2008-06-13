package net.footballpredictions.footballstats.model;

import java.util.Comparator;


/**
 * {@link Comparator} for sorting a league table in order of fewest points dropped.
 * @author Daniel Dyer
 */
class DroppedPointsComparator implements Comparator<FullRecord>
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


    public final int compare(FullRecord team1, FullRecord team2)
    {
        int compare = getPointsDropped(where, team1) - getPointsDropped(where, team2); // Swap teams for descending order.
        if (compare == 0)
        {
            compare = team2.getGoalDifference(where) - team1.getGoalDifference(where); // Swap teams for descending order.
            if (compare == 0)
            {
                compare = team2.getAggregate(where, TeamRecord.AGGREGATE_SCORED)
                          - team1.getAggregate(where, TeamRecord.AGGREGATE_SCORED); // Swap teams for descending order.
                if (compare == 0)
                {
                    compare = team2.getAggregate(where, TeamRecord.AGGREGATE_WON)
                               - team1.getAggregate(where, TeamRecord.AGGREGATE_WON); // Swap teams for descending order.
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


    private int getPointsDropped(int where, FullRecord team)
    {
        return team.getAggregate(where, TeamRecord.AGGREGATE_PLAYED) * pointsForWin - getPoints(where, team);
    }


    private int getPoints(int where, FullRecord team)
    {
        int points = team.getAggregate(where, TeamRecord.AGGREGATE_WON) * pointsForWin
                     + team.getAggregate(where, TeamRecord.AGGREGATE_DRAWN) * pointsForDraw;
        points += team.getPointsAdjustment(where);
        return points;
    }
}
