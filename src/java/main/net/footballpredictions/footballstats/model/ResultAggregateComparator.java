package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} for ordering results by total number of goals.
 * @author Daniel Dyer
 */
class ResultAggregateComparator implements Comparator<Result>
{
    private final Comparator<Result> dateComparator = new ResultDateComparator();

    public int compare(Result result1, Result result2)
    {
        int compare = result2.getMatchAggregate() - result1.getMatchAggregate();  // Descending order.
        if (compare == 0) // If the aggregate is the same, earlier matches take precedence.
        {
            compare = dateComparator.compare(result1, result2);
        }
        return compare;        
    }
}
