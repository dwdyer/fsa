package net.footballpredictions.footballstats.model;

import java.util.Comparator;


/**
 * {@link Comparator} for sorting a league table in order of fewest points dropped.
 * @author Daniel Dyer
 */
class DroppedPointsComparator implements Comparator<StandardRecord>
{
    public final int compare(StandardRecord team1, StandardRecord team2)
    {
        int compare = team1.getDroppedPoints() - team2.getDroppedPoints(); // Swap teams for descending order.
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
}
