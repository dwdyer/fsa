package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} for ordering results by date.
 * @author Daniel Dyer
 */
class ResultDateComparator implements Comparator<Result>
{
    public int compare(Result result1, Result result2)
    {
        int compare = result1.getDate().compareTo(result2.getDate());
        if (compare == 0) // If the date is the same, order alphabetically by home team.
        {
            compare = result1.getHomeTeam().getName().toLowerCase().compareTo(result2.getHomeTeam().getName().toLowerCase());
        }
        return compare;
    }
}
