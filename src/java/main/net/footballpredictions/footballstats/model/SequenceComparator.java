package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * Comparator for sorting a set of teams by a particular sequences statistic.
 * @author Daniel Dyer
 */
final class SequenceComparator implements Comparator<StandardRecord>
{
    private final boolean current;
    private final SequenceType sequence;
        
    public SequenceComparator(SequenceType sequence, boolean current)
    {
        this.sequence = sequence;
        this.current = current;
    }
        
    public int compare(StandardRecord team1, StandardRecord team2)
    {
        int compare;
        if (current)
        {
            compare = team2.getCurrentSequence(sequence) - team1.getCurrentSequence(sequence); // Swap teams for descending sort.
        }
        else
        {
            compare = team2.getBestSequence(sequence) - team1.getBestSequence(sequence); // Swap teams for descending sort.
        }
        if (compare == 0)
        {
            // If records are the same, sort on alphabetical order.
            compare = team1.getName().toLowerCase().compareTo(team2.getName().toLowerCase());
        }
        return compare;
    }
}