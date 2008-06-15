package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} used to order teams by their mean number of points per game.
 * @author Daniel Dyer
 */
class PointsPerGameComparator implements Comparator<StandardRecord>
{
    public final int compare(StandardRecord team1, StandardRecord team2)
    {
        int compare = doMainComparison(team1, team2);
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


    public int doMainComparison(StandardRecord team1, StandardRecord team2)
    {
        double difference = team2.getAveragePoints() - team1.getAveragePoints(); // Swap teams for descending sort.
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
}