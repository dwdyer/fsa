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
package net.footballpredictions.footballstats.awt;

import java.awt.Component;
import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * Defines methods to be implemented by classes that provide stats panels for the GUI.
 * @author Daniel Dyer
 * @since 27/12/2003
 * @version $Revision: $
 */
public interface StatsPanel
{
    /**
     * Sets the data to use for the stats.  Implementing classes should recalculate their
     * stats when the data changes.
     * @param highlightedTeam Specifies a team to highlight when displaying this panel's stats.
     */
    void setLeagueData(LeagueSeason data, String highlightedTeam);
    
    
    /**
     * Sets the theme (colours and fonts) to use in the panel's GUI.
     * Guaranteed to be called before either of the methods below.
     */
    void setTheme(Theme theme);
    
    
    /**
     * @return A component encapsulating the controls for manipulating the stats.
     */
    Component getControls();
    
    
    /**
     * @return A component containing a view of the statistics.
     */
    Component getView();
}