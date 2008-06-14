package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * Comparator for sorting a set of teams by a particular sequences statistic.
 * @author Daniel Dyer
 */
final class SequenceComparator implements Comparator<StandardRecord>
{
    private final int when;
    private final int sequence;
        
    public SequenceComparator(int when, int sequence)
    {
        this.when = when;
        this.sequence = sequence;
    }
        
    public int compare(StandardRecord team1, StandardRecord team2)
    {
        int compare = team2.getSequence(when, sequence) - team1.getSequence(when, sequence); // Swap teams for descending sort.
        if (compare == 0)
        {
            // If records are the same, sort on alphabetical order.
            compare = team1.getName().toLowerCase().compareTo(team2.getName().toLowerCase());
        }
        return compare;
    }
}