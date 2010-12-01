// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2010 Daniel W. Dyer
//
//   This program is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published by
//   the Free Software Foundation, either version 3 of the License, or
//   (at your option) any later version.
//
//   This program is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//   GNU General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with this program.  If not, see <http://www.gnu.org/licenses/>.
// ============================================================================
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