package net.footballpredictions.footballstats.model;

import java.util.Collections;
import java.util.SortedSet;
import net.footballpredictions.footballstats.util.FixedSizeSortedSet;

/**
 * @author Daniel Dyer
 */
public class FormRecord extends AbstractTeamRecord
{
    private final int where;
    private final int length;
    private final SortedSet<Result> formResults;

    public FormRecord(String name, int where, int length)
    {
        super(name);
        this.where = where;
        this.length = length;
        this.formResults = new FixedSizeSortedSet<Result>(length, Collections.reverseOrder(new ResultDateComparator()));
    }


    public void addResult(Result result)
    {
        if (where == BOTH
            || (where == HOME && result.getHomeTeam().getName().equals(getName()))
            || (where == AWAY && result.getAwayTeam().getName().equals(getName())))
        {
            formResults.add(result);
        }
    }


    public String getForm(int where)
    {
        StringBuilder formString = new StringBuilder();

        // If we don't have a full set of results (because not enough games have been played
        // yet), uses dashes in place of the missing results.
        for (int i = formResults.size(); i < length; i++)
        {
            formString.append('-');
        }

        for (Result result : formResults)
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
        switch (aggregate)
        {
            case AGGREGATE_PLAYED:
            {
                return formResults.size();
            }
            case AGGREGATE_WON:
            {
                for (Result result : formResults)
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
                for (Result result : formResults)
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
                for (Result result : formResults)
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
                for (Result result : formResults)
                {
                    value += result.getGoalsFor(this);
                }
                break;
            }
            case AGGREGATE_CONCEDED:
            {
                for (Result result : formResults)
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
