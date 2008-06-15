package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} used to order teams in standard league table order.
 * @author Daniel Dyer
 */
class LeagueTableComparator implements Comparator<TeamRecord>
{
    public final int compare(TeamRecord team1, TeamRecord team2)
    {
        int compare = team2.getPoints() - team1.getPoints();// Swap teams for descending order.
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