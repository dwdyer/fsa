package net.footballpredictions.footballstats.model;

import java.util.Collections;
import java.util.SortedSet;
import net.footballpredictions.footballstats.util.FixedSizeSortedSet;

/**
 * @author Daniel Dyer
 */
public class FormRecord extends AbstractTeamRecord
{
    private final int length;
    private final SortedSet<Result> formResults;

    public FormRecord(Team team, int length)
    {
        super(team);
        this.length = length;
        this.formResults = new FixedSizeSortedSet<Result>(length, Collections.reverseOrder(new ResultDateComparator()));
    }


    public void addResult(Result result)
    {
        formResults.add(result);
    }


    public String getForm()
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
            else if (result.isWin(getTeam()))
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


    public int getPlayed()
    {
        return formResults.size();
    }


    public int getWon()
    {
        int won = 0;
        for (Result result : formResults)
        {
            if (result.isWin(getTeam()))
            {
                won++;
            }
        }
        return won;
    }


    public int getDrawn()
    {
        int drawn = 0;
        for (Result result : formResults)
        {
            if (result.isDraw())
            {
                drawn++;
            }
        }
        return drawn;
    }


    public int getLost()
    {
        int lost = 0;
        for (Result result : formResults)
        {
            if (result.isDefeat(getTeam()))
            {
                lost++;
            }
        }
        return lost;
    }

    
    public int getScored()
    {
        int scored = 0;
        for (Result result : formResults)
        {
            scored += result.getGoalsFor(getTeam());
        }
        return scored;
    }


    public int getConceded()
    {
        int conceded = 0;
        for (Result result : formResults)
        {
            conceded += result.getGoalsAgainst(getTeam());
        }
        return conceded;
    }
}
