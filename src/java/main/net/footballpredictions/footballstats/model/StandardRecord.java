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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.LinkedHashMap;

/**
 * Models a single team's record for the season.  This may be their overall record,
 * or it may be their home record or their away record, or even some other subset of
 * results.
 * @author Daniel Dyer
 * @since 21/12/2003
 */
public final class StandardRecord extends AbstractTeamRecord
{
    private final List<Result> results = new ArrayList<Result>(46); // Most leagues have no more than 46 games per team.

    private final FormRecord form;

    private int won = 0;
    private int drawn = 0;
    private int lost = 0 ;
    private int scored = 0;
    private int conceded = 0;

    private final Map<SequenceType, List<Result>> currentSequences = new EnumMap<SequenceType, List<Result>>(SequenceType.class);
    private final Map<SequenceType, List<Result>> bestSequences = new EnumMap<SequenceType, List<Result>>(SequenceType.class);

    private Result biggestWin = null;
    private Result biggestDefeat = null;

    /**
     * Constructor, sets name.  All other data is added via the addResult method later.
     * @param team The team that this record corresponds to.
     * @param where Which matches are included in this record (home matches, away matches
     * or both?)
     * @param pointsForWin The number of points awarded for each win.
     * @param pointsForDraw The number of points awarded for each draw.
     */
    public StandardRecord(Team team,
                          VenueType where,
                          int pointsForWin,
                          int pointsForDraw)
    {
        super(team, pointsForWin, pointsForDraw);
        
        this.form = new FormRecord(team,
                                   pointsForWin,
                                   pointsForDraw,
                                   where == VenueType.BOTH ? 6 : 4);
        // Intialise sequences to zero.
        for (SequenceType sequence : SequenceType.values())
        {
            currentSequences.put(sequence, new LinkedList<Result>());
            bestSequences.put(sequence, new LinkedList<Result>());
        }
    }


    public List<Result> getResults()
    {
        return results;
    }


    /**
     * {@inheritDoc}
     */
    public void addResult(Result result)
    {
        results.add(result);
        form.addResult(result);
        updateAggregatesAndSequences(result);
    }


    /**
     * {@inheritDoc}
     */
    public int getPlayed()
    {
        return results.size();
    }


    /**
     * {@inheritDoc}
     */
    public int getWon()
    {
        return won;
    }


    /**
     * {@inheritDoc}
     */
    public int getDrawn()
    {
        return drawn;
    }


    /**
     * {@inheritDoc}
     */
    public int getLost()
    {
        return lost;
    }


    /**
     * {@inheritDoc}
     */
    public int getScored()
    {
        return scored;
    }


    /**
     * {@inheritDoc}
     */
    public int getConceded()
    {
        return conceded;
    }

    
    /**
     * This method answers questions such as "How many consecutive wins
     * does this team currently have?"
     * @param sequence The sequence to return the length of.
     * @return The specified current sequence.
     * @see #getBestSequence(SequenceType)
     */
    public List<Result> getCurrentSequence(SequenceType sequence)
    {
        return currentSequences.get(sequence);
    }


    /**
     * This method answers questions such as "What is the longest sequence
     * of consecutive wins this team has achieved this season?"
     * @param sequence The sequence to return the length of.
     * @return The longest sequence of the specified type achieved
     * this season..
     * @see #getCurrentSequence(SequenceType)
     */
    public List<Result> getBestSequence(SequenceType sequence)
    {
        return bestSequences.get(sequence);
    }


    /**
     * {@inheritDoc}
     */
    public String getForm()
    {
        return getFormRecord().getForm();
    }


    /**
     * @return A {@link TeamRecord} that contains only data relating to the team's current
     * form.
     */
    public FormRecord getFormRecord()
    {
        return form;
    }


    /**
     * @return The result of the most recent match played.
     */
    public Result getLatestResult()
    {
        return results.isEmpty() ? null : results.get(results.size() - 1);
    }


    /**
     * @return The {@link Result} that represents the team's biggest margin of victory achieved.
     */
    public Result getBiggestWin()
    {
        return biggestWin;
    }


    /**
     * @return The {@link Result} that represents the team's biggest margin of defeat suffered.
     */
    public Result getBiggestDefeat()
    {
        return biggestDefeat;
    }


    /**
     * Queries for any current sequences that are long enough to be interesting.
     * @return All current sequences that are deemed to be "interesting".
     */
    public Map<SequenceType, Integer> getInterestingSequences()
    {
        Map<SequenceType, Integer> sequences = new LinkedHashMap<SequenceType, Integer>();
        for (Map.Entry<SequenceType, List<Result>> entry : currentSequences.entrySet())
        {
            int sequenceLength = entry.getValue().size();
            if (sequenceLength >= entry.getKey().getInterestLevel())
            {
                sequences.put(entry.getKey(), sequenceLength);
            }
        }

        // No point in saying "5 consecutive wins" and "5 matches unbeaten".
        if (sequences.containsKey(SequenceType.UNBEATEN)
            && sequences.get(SequenceType.UNBEATEN).equals(sequences.get(SequenceType.WINS)))
        {
            sequences.remove(SequenceType.UNBEATEN);
        }
        if (sequences.containsKey(SequenceType.NO_WIN)
            && sequences.get(SequenceType.NO_WIN).equals(sequences.get(SequenceType.DEFEATS)))
        {
            sequences.remove(SequenceType.NO_WIN);
        }
        return sequences;
    }


    private void updateAggregatesAndSequences(Result result)
    {
        int goalsFor = result.getGoalsFor(getTeam().getName());
        int goalsAgainst = result.getGoalsAgainst(getTeam().getName());
        int marginOfVictory = result.getMarginOfVictory();

        // Update result aggregates/sequences.
        if (result.isDefeat(getTeam().getName()))
        {
            lost++;

            addToSequence(SequenceType.NO_WIN, result);
            addToSequence(SequenceType.DEFEATS, result);
            resetSequence(SequenceType.UNBEATEN);
            resetSequence(SequenceType.WINS);
            resetSequence(SequenceType.DRAWS);

            if (biggestDefeat == null || marginOfVictory > biggestDefeat.getMarginOfVictory())
            {
                biggestDefeat = result;
            }
        }
        else
        {
            addToSequence(SequenceType.UNBEATEN, result);
            resetSequence(SequenceType.DEFEATS);

            if (result.isDraw())
            {
                drawn++;

                addToSequence(SequenceType.DRAWS, result);
                addToSequence(SequenceType.NO_WIN, result);
                resetSequence(SequenceType.WINS);
            }
            else // Must be a win
            {
                won++;

                addToSequence(SequenceType.WINS, result);
                resetSequence(SequenceType.NO_WIN);
                resetSequence(SequenceType.DRAWS);

                if (biggestWin == null || marginOfVictory > biggestWin.getMarginOfVictory())
                {
                    biggestWin = result;
                }
            }
        }

        // Update score aggregates/sequences.
        scored += goalsFor;
        conceded += goalsAgainst;
        if (goalsFor == 0)
        {
            addToSequence(SequenceType.GAMES_NOT_SCORED_IN, result);
            resetSequence(SequenceType.GAMES_SCORED_IN);
        }
        else
        {
            resetSequence(SequenceType.GAMES_NOT_SCORED_IN);
            addToSequence(SequenceType.GAMES_SCORED_IN, result);
        }

        if (goalsAgainst == 0)
        {
            addToSequence(SequenceType.CLEANSHEETS, result);
        }
        else
        {
            resetSequence(SequenceType.CLEANSHEETS);
        }
    }


    private void addToSequence(SequenceType sequence, Result result)
    {
        List<Result> list = currentSequences.get(sequence);
        list.add(result);
        // If the current sequence is better than the best this season, then
        // update the best this season.
        if (list.size() > bestSequences.get(sequence).size())
        {
            bestSequences.put(sequence, new LinkedList<Result>(list));
        }
    }


    private void resetSequence(SequenceType sequence)
    {
        currentSequences.get(sequence).clear();
    }


    /**
     * Over-ride equals.  Teams are equal if the names are equal.
     * No need to also over-ride {@link #hashCode()} because this
     * method is consistent with the superclass hash code.
     */
    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof StandardRecord && super.equals(obj);
    }

}
