package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} for ordering results by margin of victory.
 * @author Daniel Dyer
 */
class ResultMarginComparator implements Comparator<Result>
{
    private final Comparator<Result> aggregateComparator = new ResultAggregateComparator();

    public int compare(Result result1, Result result2)
    {
        int compare = result2.getMarginOfVictory() - result1.getMarginOfVictory();  // Descending order.
        if (compare == 0) // If margin is the same, give priority to higher scoring game.
        {
            compare = aggregateComparator.compare(result1, result2);
        }
        return compare;
    }
}
