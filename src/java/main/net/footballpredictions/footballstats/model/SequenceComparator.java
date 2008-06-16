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
 * {@link Comparator} for sorting a set of teams by a particular sequences statistic.
 * @author Daniel Dyer
 */
final class SequenceComparator implements Comparator<StandardRecord>
{
    private final boolean current;
    private final SequenceType sequence;
        
    public SequenceComparator(SequenceType sequence, boolean current)
    {
        this.sequence = sequence;
        this.current = current;
    }
        
    public int compare(StandardRecord team1, StandardRecord team2)
    {
        int compare;
        if (current)
        {
            compare = team2.getCurrentSequence(sequence) - team1.getCurrentSequence(sequence); // Swap teams for descending sort.
        }
        else
        {
            compare = team2.getBestSequence(sequence) - team1.getBestSequence(sequence); // Swap teams for descending sort.
        }
        if (compare == 0)
        {
            // If records are the same, sort on alphabetical order.
            compare = team1.getName().toLowerCase().compareTo(team2.getName().toLowerCase());
        }
        return compare;
    }
}