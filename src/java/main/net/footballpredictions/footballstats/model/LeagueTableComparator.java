package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} used to order teams in standard league table order.
 * @author Daniel Dyer
 */
class LeagueTableComparator implements Comparator<TeamRecord>
{
    private final int pointsForWin;
    private final int pointsForDraw;
    private final int where;


    public LeagueTableComparator(int where,
                                 int pointsForWin,
                                 int pointsForDraw)
    {
        this.where = where;
        this.pointsForWin = pointsForWin;
        this.pointsForDraw = pointsForDraw;
    }


    public final int compare(TeamRecord team1, TeamRecord team2)
    {
        int compare = getPoints(where, team2) - getPoints(where, team1);// Swap teams for descending order.
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


    private int getPoints(int where, TeamRecord team)
    {
        int points = team.getAggregate(where, TeamRecord.AGGREGATE_WON) * pointsForWin
                     + team.getAggregate(where, TeamRecord.AGGREGATE_DRAWN) * pointsForDraw;
        points += team.getPointsAdjustment(where);
        return points;
    }
}