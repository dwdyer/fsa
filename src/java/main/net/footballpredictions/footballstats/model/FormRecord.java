// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2008 Daniel W. Dyer
//
//   This program is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published by
//   the Free Software Foundation, either version 3 of the License, or
//   (at your option) any later version.
//
//   This program is distributed in the hope that it will be useful,
//   but WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//   GNU General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with this program.  If not, see <http://www.gnu.org/licenses/>.
// ============================================================================
package net.footballpredictions.footballstats.model;

import java.util.Collections;
import java.util.SortedSet;
import net.footballpredictions.footballstats.util.FixedSizeSortedSet;

/**
 * {@link TeamRecord} that contains only data about the most recent matches.
 * @author Daniel Dyer
 */
public class FormRecord extends AbstractTeamRecord
{
    private final int length;
    private final SortedSet<Result> formResults;

    /**
     * @param team The team that the form data relates to.                                       
     * @param length The number of matches that make up the form record (typically six for
     * an overall form record and 4 for home/away form).
     */
    public FormRecord(Team team, int length)
    {
        super(team);
        this.length = length;
        this.formResults = new FixedSizeSortedSet<Result>(length, Collections.reverseOrder(new ResultDateComparator()));
    }


    /**
     * {@inheritDoc}
     */
    public void addResult(Result result)
    {
        formResults.add(result);
    }


    /**
     * {@inheritDoc}
     */
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
        return formString.reverse().toString();
    }


    /**
     * {@inheritDoc}
     */
    public int getPlayed()
    {
        return formResults.size();
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
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

    
    /**
     * {@inheritDoc}
     */
    public int getScored()
    {
        int scored = 0;
        for (Result result : formResults)
        {
            scored += result.getGoalsFor(getTeam());
        }
        return scored;
    }


    /**
     * {@inheritDoc}
     */
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
