// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2008 Daniel W. Dyer
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
 * {@link Comparator} for ordering results by date.
 * @author Daniel Dyer
 */
class ResultDateComparator implements Comparator<Result>
{
    public int compare(Result result1, Result result2)
    {
        int compare = result1.getDate().compareTo(result2.getDate());
        if (compare == 0) // If the date is the same, order alphabetically by home team.
        {
            compare = result1.getHomeTeam().toLowerCase().compareTo(result2.getHomeTeam().toLowerCase());
        }
        return compare;
    }
}
