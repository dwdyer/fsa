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
 * {@link Comparator} for ordering results by total number of goals.
 * @author Daniel Dyer
 */
class ResultAggregateComparator implements Comparator<Result>
{
    private final Comparator<Result> dateComparator = new ResultDateComparator();

    public int compare(Result result1, Result result2)
    {
        int compare = result2.getMatchAggregate() - result1.getMatchAggregate();  // Descending order.
        if (compare == 0) // If the aggregate is the same, earlier matches take precedence.
        {
            compare = dateComparator.compare(result1, result2);
        }
        return compare;        
    }
}
