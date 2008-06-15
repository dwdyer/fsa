// $Header: $
package net.footballpredictions.footballstats.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Models a single team's record for the season.  This may be their overall record
 * or it may be their home record or their away record.
 * @author Daniel Dyer
 * @since 21/12/2003
 * @version $Revision: $
 */
public final class StandardRecord extends AbstractTeamRecord
{
    // Constants for sequence types.
    public static final int SEQUENCE_WIN = 0;
    public static final int SEQUENCE_DRAW = 1;
    public static final int SEQUENCE_DEFEAT = 2;
    public static final int SEQUENCE_UNBEATEN = 3;
    public static final int SEQUENCE_NO_WIN = 4;
    public static final int SEQUENCE_CLEANSHEET = 5;
    public static final int SEQUENCE_SCORED = 6;
    public static final int SEQUENCE_NO_GOAL = 7;

    // Constants for current/season sequence.
    public static final int CURRENT = 0;
    public static final int SEASON = 1;

    // Constants for attendance stats.
    public static final int ATTENDANCE_AVERAGE = 0;
    public static final int ATTENDANCE_HIGHEST = 1;
    public static final int ATTENDANCE_LOWEST = 2;
    public static final int ATTENDANCE_AGGREGATE = 3;

    private final VenueType where;

    private final List<Result> results = new ArrayList<Result>(46); // Most leagues have no more than 46 games per team.

    private final FormRecord form;

    private int won = 0;
    private int drawn = 0;
    private int lost = 0 ;
    private int scored = 0;
    private int conceded = 0;

    /**
     * Two-dimensional array for storing sequence data.  First dimension is
     * current/season, second is sequence type.
     */
    private final int[][] sequences = new int[2][8];

    private Result biggestWin = null;
    private Result biggestDefeat = null;

    /**
     * Constructor, sets name.  All other data is added via the addResult method later.
     */
    public StandardRecord(Team team, VenueType where)
    {
        super(team);
        this.where = where;
        this.form = new FormRecord(team, where == VenueType.BOTH ? 6 : 4);
    }


    public List<Result> getResults()
    {
        return results;
    }


    public void addResult(Result result)
    {
        results.add(result);
        form.addResult(result);
        updateAggregatesAndSequences(result);
    }


    public int getPlayed()
    {
        return results.size();
    }


    public int getWon()
    {
        return won;
    }


    public int getDrawn()
    {
        return drawn;
    }


    public int getLost()
    {
        return lost;
    }


    public int getScored()
    {
        return scored;
    }


    public int getConceded()
    {
        return conceded;
    }


    public int getSequence(int when, int sequence)
    {
        return sequences[when][sequence];
    }


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


    public Result getLatestResult()
    {
        return results.isEmpty() ? null : results.get(results.size() - 1);
    }


    public Result getBiggestWin()
    {
        return biggestWin;
    }


    public Result getBiggestDefeat()
    {
        return biggestDefeat;
    }


    /**
     * Returns interesting facts about the team's form.
     */
    public List<String> getNotes()
    {
        List<String> notes = new LinkedList<String>();
        String end = " " + where.getDescription().toLowerCase() + "matches.";

        // Check unbeatean/without win sequences.
        if (getSequence(CURRENT, SEQUENCE_UNBEATEN) >= 3)
        {
            notes.add("Unbeaten in last " + getSequence(CURRENT, SEQUENCE_UNBEATEN) + end);
        }
        if (getSequence(CURRENT, SEQUENCE_NO_WIN) >= 3)
        {
            notes.add("Haven't won in last " + getSequence(CURRENT, SEQUENCE_NO_WIN) + end);
        }

        // Check win/loss sequences.
        if (getSequence(CURRENT, SEQUENCE_WIN) >= 3)
        {
            notes.add("Won last " + getSequence(CURRENT, SEQUENCE_WIN) + end);
        }
        else if (getSequence(CURRENT, SEQUENCE_DRAW) >= 3)
        {
            notes.add("Drawn last " + getSequence(CURRENT, SEQUENCE_DRAW) + end);
        }
        else if (getSequence(CURRENT, SEQUENCE_DEFEAT) >= 3)
        {
            notes.add("Lost last " + getSequence(CURRENT, SEQUENCE_DEFEAT) + end);
        }

        // Check cleansheet/scoring sequences.
        if (getSequence(CURRENT, SEQUENCE_NO_GOAL) >= 3)
        {
            notes.add("Haven't scored in last " + getSequence(CURRENT, SEQUENCE_NO_GOAL) + end);
        }
        if (getSequence(CURRENT, SEQUENCE_CLEANSHEET) >= 3)
        {
            notes.add("Haven't conceded in last " + getSequence(CURRENT, SEQUENCE_CLEANSHEET) + end);
        }
        if (getSequence(CURRENT, SEQUENCE_SCORED) >= 10)
        {
            notes.add("Scored in last " + getSequence(CURRENT, SEQUENCE_SCORED) + end);
        }

        return notes;
    }


    private void updateAggregatesAndSequences(Result result)
    {
        int goalsFor = result.getGoalsFor(getTeam());
        int goalsAgainst = result.getGoalsAgainst(getTeam());
        int marginOfVictory = result.getMarginOfVictory();

        // Update result aggregates/sequences.
        if (result.isDefeat(getTeam()))
        {
            lost++;

            sequences[CURRENT][SEQUENCE_NO_WIN]++;
            sequences[CURRENT][SEQUENCE_DEFEAT]++;
            sequences[CURRENT][SEQUENCE_UNBEATEN] = 0;
            sequences[CURRENT][SEQUENCE_WIN] = 0;
            sequences[CURRENT][SEQUENCE_DRAW] = 0;

            if (biggestDefeat == null || marginOfVictory > biggestDefeat.getMarginOfVictory())
            {
                biggestDefeat = result;
            }
        }
        else
        {
            sequences[CURRENT][SEQUENCE_UNBEATEN]++;
            sequences[CURRENT][SEQUENCE_DEFEAT] = 0;

            if (result.isDraw())
            {
                drawn++;

                sequences[CURRENT][SEQUENCE_DRAW]++;
                sequences[CURRENT][SEQUENCE_NO_WIN]++;
                sequences[CURRENT][SEQUENCE_WIN] = 0;
            }
            else // Must be a win
            {
                won++;

                sequences[CURRENT][SEQUENCE_WIN]++;
                sequences[CURRENT][SEQUENCE_NO_WIN] = 0;
                sequences[CURRENT][SEQUENCE_DRAW] = 0;

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
            sequences[CURRENT][SEQUENCE_NO_GOAL]++;
            sequences[CURRENT][SEQUENCE_SCORED] = 0;
        }
        else
        {
            sequences[CURRENT][SEQUENCE_NO_GOAL] = 0;
            sequences[CURRENT][SEQUENCE_SCORED]++;
        }

        if (goalsAgainst == 0)
        {
            sequences[CURRENT][SEQUENCE_CLEANSHEET]++;
        }
        else
        {
            sequences[CURRENT][SEQUENCE_CLEANSHEET] = 0;
        }

        // Update season's best sequences, if necessary.
        for (int i = 0; i < 8; i++) // 8 is number of sequences.
        {
            sequences[SEASON][i] = Math.max(sequences[SEASON][i], sequences[CURRENT][i]);
        }
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
