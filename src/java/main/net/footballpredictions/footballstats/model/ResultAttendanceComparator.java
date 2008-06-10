package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} for ordering results by attendance.
 * @author Daniel Dyer
*/
class ResultAttendanceComparator implements Comparator<Result>
{
    public int compare(Result result1, Result result2)
    {
        int compare = result2.attendance - result1.attendance; // Descending order.
        if (compare == 0) // If the aggregate is the same, earlier matches take precedence.
        {
            compare = result1.date.compareTo(result2.date);
            if (compare == 0) // And if that's the same, order alphabetically by home team.
            {
                compare = result1.homeTeam.getName().toLowerCase().compareTo(result2.homeTeam.getName().toLowerCase());
            }
        }
        return compare;
    }
}
