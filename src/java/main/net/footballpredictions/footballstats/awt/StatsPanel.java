// $Header: $
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