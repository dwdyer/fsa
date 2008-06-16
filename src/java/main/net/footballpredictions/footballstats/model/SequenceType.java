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

/**
 * Enumeration of all of the sequence types supported by the software.
 * @author Daniel Dyer
 */
public enum SequenceType
{
    WINS("Wins"),
    DRAWS("Draws"),
    DEFEATS("Defeats"),
    UNBEATEN("Games Without Defeat"),
    NO_WIN("Games Without Winning"),
    CLEANSHEETS("Cleansheets"),
    GAMES_SCORED_IN("Games Scored In"),
    GAMES_NOT_SCORED_IN("Games Without Scoring");
    
    private final String description;

    SequenceType(String description)
    {
        this.description = description;
    }


    @Override
    public String toString()
    {
        return description;
    }
}
