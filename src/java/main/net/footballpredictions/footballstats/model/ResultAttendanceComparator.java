package net.footballpredictions.footballstats.model;

import java.util.Comparator;

/**
 * {@link Comparator} for ordering results by attendance.
 * @author Daniel Dyer
*/
class ResultAttendanceComparator implements Comparator<Result>
{
    private final Comparator<Result> dateComparator = new ResultDateComparator();

    public int compare(Result result1, Result result2)
    {
        int compare = result2.getAttendance() - result1.getAttendance(); // Descending order.
        if (compare == 0) // If the aggregate is the same, earlier matches take precedence.
        {
            compare = dateComparator.compare(result1, result2);
        }
        return compare;
    }
}
