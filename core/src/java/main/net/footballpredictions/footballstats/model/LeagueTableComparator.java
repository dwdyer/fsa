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
import java.io.Serializable;

/**
 * {@link Comparator} used to order teams in standard league table order.
 * @author Daniel Dyer
 */
class LeagueTableComparator implements Comparator<TeamRecord>, Serializable
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