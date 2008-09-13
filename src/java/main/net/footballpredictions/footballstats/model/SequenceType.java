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
    WINS("seq_type.wins"),
    DRAWS("seq_type.draws"),
    DEFEATS("seq_type.defeats"),
    UNBEATEN("seq_type.gamesWithoutDefeat"),
    NO_WIN("seq_type.gamesWithoutWin"),
    CLEANSHEETS("seq_type.clean_sheet"),
    GAMES_SCORED_IN("seq_type.gamesScoredIn"),
    GAMES_NOT_SCORED_IN("seq_type.gamesWithoutScoring");
    
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
