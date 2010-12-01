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
package net.footballpredictions.footballstats.swing;

import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * Interface implemented by objects that need to be informed when the selected
 * data file changes.
 * @author Daniel Dyer
 */
public interface DataListener
{
    /**
     * Sets the data to use for the stats.  Implementing classes should recalculate their
     * stats when the data changes.
     */
    void setLeagueData(LeagueSeason data);
}
