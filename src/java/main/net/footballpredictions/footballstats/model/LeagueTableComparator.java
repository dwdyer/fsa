package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * Convenience base class that provides most of the comparison logic for league tables.
 * Sub-classes just need to fill in the main comparison (i.e. the one that takes precedence
 * over all of the others).
 * @author Daniel Dyer
 */
abstract class LeagueTableComparator implements Comparator<Team>
{
    protected int where = Team.BOTH;
    protected boolean form = false; // Are we calculating a form table.
        
    public final void setWhere(int where)
    {
        this.where = where;
    }
        
        
    public final void setForm(boolean form)
    {
        this.form = form;
    }
        
        
    public final int compare(Team team1, Team team2)
    {
        int compare = doMainComparison(team1, team2);
        if (compare == 0)
        {
            compare = team2.getGoalDifference(where, form) - team1.getGoalDifference(where, form); // Swap teams for descending sort.
            if (compare == 0)
            {
                compare = team2.getAggregate(where, Team.AGGREGATE_SCORED, form) - team1.getAggregate(where, Team.AGGREGATE_SCORED, form); // Swap teams for descending sort.
                if (compare == 0)
                {
                    compare = team2.getAggregate(where, Team.AGGREGATE_WON, form) - team1.getAggregate(where, Team.AGGREGATE_WON, form); // Swap teams for descending sort.
                    if (compare == 0)
                    {
                        // If records are the same, sort on alphabetical order.
                        compare = team1.getName().toLowerCase().compareTo(team2.getName().toLowerCase());
                    }
                }
            }
        }
        return compare;
    }
        
    protected abstract int doMainComparison(Team team1, Team team2);
}