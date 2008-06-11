package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} used to order teams by their mean number of points per game.
 * @author Daniel Dyer
 */
class PointsPerGameComparator implements Comparator<Team>
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


    public final int compare(Team team1, Team team2)
    {
        int compare = doMainComparison(team1, team2);
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


    public int doMainComparison(Team team1, Team team2)
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


    public double getAveragePoints(int where, Team team)
    {
        return (double) getPoints(where, team) / team.getAggregate(where, Team.AGGREGATE_PLAYED, false);
    }



    private int getPoints(int where, Team team)
    {
        int points = team.getAggregate(where, Team.AGGREGATE_WON, false) * pointsForWin
                     + team.getAggregate(where, Team.AGGREGATE_DRAWN, false) * pointsForDraw;
        points += team.getPointsAdjustment(where);
        return points;
    }
}