package net.footballpredictions.footballstats.swing;

import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * @author Daniel Dyer
 */
public interface StatsPanel
{
    /**
     * Sets the data to use for the stats.  Implementing classes should recalculate their
     * stats when the data changes.
     */
    void setLeagueData(LeagueSeason data);
}
