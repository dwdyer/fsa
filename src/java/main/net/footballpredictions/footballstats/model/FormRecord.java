package net.footballpredictions.footballstats.model;

import java.util.SortedSet;
import java.util.Collections;
import net.footballpredictions.footballstats.util.FixedSizeSortedSet;

/**
 * @author Daniel Dyer
 */
public class FormRecord extends AbstractTeamRecord
{
    private final FixedSizeSortedSet<Result> homeForm
        = new FixedSizeSortedSet<Result>(4, Collections.reverseOrder(new ResultDateComparator()));
    private final FixedSizeSortedSet<Result> awayForm
        = new FixedSizeSortedSet<Result>(4, Collections.reverseOrder(new ResultDateComparator()));
    private final FixedSizeSortedSet<Result> overallForm
        = new FixedSizeSortedSet<Result>(6, Collections.reverseOrder(new ResultDateComparator()));

    public FormRecord(String name)
    {
        super(name);
    }


    private FixedSizeSortedSet<Result> getFormResults(int where)
    {
        switch (where)
        {
            case HOME: return homeForm;
            case AWAY: return awayForm;
            case BOTH: return overallForm;
            default: throw new IllegalArgumentException("where = " + where);
        }
    }


    public void addResult(Result result)
    {
        overallForm.add(result);
        if (result.getHomeTeam().getName().equals(getName()))
        {
            homeForm.add(result);
        }
        else if (result.getAwayTeam().getName().equals(getName()))
        {
            awayForm.add(result);
        }
    }


    public String getForm(int where)
    {
        FixedSizeSortedSet<Result> results = getFormResults(where);
        StringBuilder formString = new StringBuilder();

        // If we don't have a full set of results (because not enough games have been played
        // yet), uses dashes in place of the missing results.
        int dashCount = results.getMaxSize() - results.size();
        for (int i = 0; i < dashCount; i++)
        {
            formString.append('-');
        }

        for (Result result : results)
        {
            if (result.isDraw())
            {
                formString.append('D');
            }
            else if (result.isWin(this))
            {
                formString.append('W');
            }
            else
            {
                formString.append('L');
            }
        }
        return formString.toString();
    }


    /**
     * Calculates the value for one of the columns in a form table.
     */
    public int getAggregate(int where, int aggregate)
    {
        int value = 0;
        SortedSet<Result> results = getFormResults(where);
        switch (aggregate)
        {
            case AGGREGATE_PLAYED:
            {
                return results.size();
            }
            case AGGREGATE_WON:
            {
                for (Result result : results)
                {
                    if (result.isWin(this))
                    {
                        value++;
                    }
                }
                break;
            }
            case AGGREGATE_DRAWN:
            {
                for (Result result : results)
                {
                    if (result.isDraw())
                    {
                        value++;
                    }
                }
                break;
            }
            case AGGREGATE_LOST:
            {
                for (Result result : results)
                {
                    if (result.isDefeat(this))
                    {
                        value++;
                    }
                }
                break;
            }
            case AGGREGATE_SCORED:
            {
                for (Result result : results)
                {
                    value += result.getGoalsFor(this);
                }
                break;
            }
            case AGGREGATE_CONCEDED:
            {
                for (Result result : results)
                {
                    value += result.getGoalsAgainst(this);
                }
            }
        }
        return value;
    }


    public int getPointsAdjustment(int where)
    {
        return 0; // Points adjustments are not applied to form tables.
    }
}
