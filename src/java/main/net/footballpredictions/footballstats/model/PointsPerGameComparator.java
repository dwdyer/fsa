package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} used to order teams by their mean number of points per game.
 * @author Daniel Dyer
 */
class PointsPerGameComparator implements Comparator<StandardRecord>
{
    private final int pointsForWin;
    private final int pointsForDraw;
    private final int where;


    public PointsPerGameComparator(int where,
                                   int pointsForWin,
                                   int pointsForDraw)
    {
        this.where = where;
        this.pointsForWin = pointsForWin;
        this.pointsForDraw = pointsForDraw;
    }


    public final int compare(StandardRecord team1, StandardRecord team2)
    {
        int compare = doMainComparison(team1, team2);
        if (compare == 0)
        {
            compare = team2.getGoalDifference() - team1.getGoalDifference(); // Swap teams for descending order.
            if (compare == 0)
            {
                compare = team2.getAggregate(TeamRecord.AGGREGATE_SCORED)
                          - team1.getAggregate(TeamRecord.AGGREGATE_SCORED); // Swap teams for descending order.
                if (compare == 0)
                {
                    compare = team2.getAggregate(TeamRecord.AGGREGATE_WON)
                              - team1.getAggregate(TeamRecord.AGGREGATE_WON); // Swap teams for descending order.
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


    public int doMainComparison(StandardRecord team1, StandardRecord team2)
    {
        double difference = getAveragePoints(where, team2) - getAveragePoints(where, team1); // Swap teams for descending sort.
        // Convert to int (sign is more important than value).
        if (difference == 0)
        {
            return 0;
        }
        else
        {
            return difference > 0 ? 1 : -1;
        }
    }


    public double getAveragePoints(int where, StandardRecord team)
    {
        return (double) getPoints(where, team) / team.getAggregate(TeamRecord.AGGREGATE_PLAYED);
    }



    private int getPoints(int where, StandardRecord team)
    {
        int points = team.getAggregate(TeamRecord.AGGREGATE_WON) * pointsForWin
                     + team.getAggregate(TeamRecord.AGGREGATE_DRAWN) * pointsForDraw;
        points += team.getTeam().getPointsAdjustment(where);
        return points;
    }
}