package net.footballpredictions.footballstats.model;

import java.util.Comparator;


/**
 * {@link Comparator} for sorting a league table in order of fewest points dropped.
 * @author Daniel Dyer
 */
class DroppedPointsComparator implements Comparator<StandardRecord>
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


    public final int compare(StandardRecord team1, StandardRecord team2)
    {
        int compare = getPointsDropped(where, team1) - getPointsDropped(where, team2); // Swap teams for descending order.
        if (compare == 0)
        {
            compare = team2.getGoalDifference() - team1.getGoalDifference(); // Swap teams for descending order.
            if (compare == 0)
            {
                compare = team2.getScored() - team1.getScored(); // Swap teams for descending order.
                if (compare == 0)
                {
                    compare = team2.getWon() - team1.getWon(); // Swap teams for descending order.
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


    private int getPointsDropped(int where, StandardRecord team)
    {
        return team.getPlayed() * pointsForWin - getPoints(where, team);
    }


    private int getPoints(int where, StandardRecord team)
    {
        int points = team.getWon() * pointsForWin + team.getDrawn() * pointsForDraw;
        points += team.getTeam().getPointsAdjustment(where);
        return points;
    }
}
