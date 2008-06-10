package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * Comparator for sorting a set of teams by a particular sequences statistic.
 * @author Daniel Dyer
 */
final class TeamSequenceComparator implements Comparator<Team>
{
    private int when, where, sequence;
        
    public void configure(int when, int where, int sequence)
    {
        this.when = when;
        this.where = where;
        this.sequence = sequence;
    }
        
    public int compare(Team team1, Team team2)
    {
        int compare = team2.getSequence(when, where, sequence) - team1.getSequence(when, where, sequence); // Swap teams for descending sort.
        if (compare == 0)
        {
            // If records are the same, sort on alphabetical order.
            compare = team1.getName().toLowerCase().compareTo(team2.getName().toLowerCase());
        }
        return compare;
    }
}