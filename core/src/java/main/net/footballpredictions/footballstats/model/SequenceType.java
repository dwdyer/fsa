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

/**
 * Enumeration of all of the sequence types supported by the software.
 * @author Daniel Dyer
 */
public enum SequenceType
{
    WINS(3),
    DRAWS(3),
    DEFEATS(3),
    UNBEATEN(3),
    NO_WIN(3),
    CLEANSHEETS(3),
    GAMES_SCORED_IN(10),
    GAMES_NOT_SCORED_IN(3),
    GAMES_CONCEDED_IN(10);

    private final int interestLevel;

    /**
     * @param interestLevel The threshold at which the sequence becomes "interesting".
     * Used to determine which notes to display about a team's current sequences.
     */
    private SequenceType(int interestLevel)
    {
        this.interestLevel = interestLevel;
    }


    public int getInterestLevel()
    {
        return interestLevel;
    }
}
