// $Header: $
package net.footballpredictions.footballstats.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Models a single team's record for the season.
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

    // Constants for key results types.
    public static final int LAST_RESULT = 0;
    public static final int BIGGEST_WIN = 1;
    public static final int BIGGEST_DEFEAT = 2;

    // Constants for current/season sequence.
    public static final int CURRENT = 0;
    public static final int SEASON = 1;

    // Constants for attendance stats.
    public static final int ATTENDANCE_AVERAGE = 0;
    public static final int ATTENDANCE_HIGHEST = 1;
    public static final int ATTENDANCE_LOWEST = 2;
    public static final int ATTENDANCE_AGGREGATE = 3;

    private final int where;

    private final List<Result> results = new ArrayList<Result>(23); // Most leagues have no more than 46 games per team.

    private final FormRecord form;

    private final int[] aggregates = new int[6];

    /**
     * Two-dimensional array for storing sequence data.  First dimension is
     * current/season, second is sequence type.
     */
    private final int[][] sequences = new int[2][8];

    private final Result[] keyResults = new Result[3];

    /**
     * Constructor, sets name.  All other data is added via the addResult method later.
     */
    public StandardRecord(Team team, int where)
    {
        super(team);
        this.where = where;
        this.form = new FormRecord(team, where, where == BOTH ? 6 : 4);
    }


    public Result[] getResults(int where)
    {
        Result[] resultsArray;
        if (where == BOTH)
        {
            resultsArray = new Result[results.size()];
            results.toArray(resultsArray);
        }
        else
        {
            resultsArray = new Result[getAggregate(AGGREGATE_PLAYED)];
            int count = 0;
            for (int i = 0; i < results.size() && count < resultsArray.length; i++)
            {
                Result result = results.get(i);
                if ((where == HOME && result.getHomeTeam().equals(this))
                    || (where == AWAY && result.getAwayTeam().equals(this)))
                {
                    resultsArray[count] = result;
                    count++;
                }
            }
        }
        return resultsArray;
    }


    public void addResult(Result result)
    {
        if (where == BOTH
            || (where == HOME && result.getHomeTeam().getName().equals(getName()))
            || (where == AWAY && result.getAwayTeam().getName().equals(getName())))
        {
            results.add(result);
            form.addResult(result);
            updateAggregatesAndSequences(result);
        }
    }


    /**
     * Helper method for three parameter version of getAggregate.
     */
    public int getAggregate(int aggregate)
    {
        return aggregates[aggregate];
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


    public Result getKeyResult(int key)
    {
        return keyResults[key];
    }


    /**
     * Returns interesting facts about the team's form.
     */
    public String[] getNotes()
    {
        List<String> notes = new LinkedList<String>();
        String end = " matches.";
        if (where == HOME)
        {
            end = " home matches.";
        }
        else if (where == AWAY)
        {
            end = " away matches.";
        }

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

        String[] noteStrings = new String[notes.size()];
        notes.toArray(noteStrings);
        return noteStrings;
    }


    private void updateAggregatesAndSequences(Result result)
    {
        int goalsFor = result.getGoalsFor(getTeam());
        int goalsAgainst = result.getGoalsAgainst(getTeam());
        int marginOfVictory = result.getMarginOfVictory();

        // Update last result.
        keyResults[LAST_RESULT] = result;

        aggregates[AGGREGATE_PLAYED]++;

        // Update result aggregates/sequences.
        if (result.isDefeat(getTeam()))
        {
            aggregates[AGGREGATE_LOST]++;

            sequences[CURRENT][SEQUENCE_NO_WIN]++;
            sequences[CURRENT][SEQUENCE_DEFEAT]++;
            sequences[CURRENT][SEQUENCE_UNBEATEN] = 0;
            sequences[CURRENT][SEQUENCE_WIN] = 0;
            sequences[CURRENT][SEQUENCE_DRAW] = 0;

            if (keyResults[BIGGEST_DEFEAT] == null || marginOfVictory > keyResults[BIGGEST_DEFEAT].getMarginOfVictory())
            {
                keyResults[BIGGEST_DEFEAT] = result;
            }
        }
        else
        {
            sequences[CURRENT][SEQUENCE_UNBEATEN]++;
            sequences[CURRENT][SEQUENCE_DEFEAT] = 0;

            if (result.isDraw())
            {
                aggregates[AGGREGATE_DRAWN]++;

                sequences[CURRENT][SEQUENCE_DRAW]++;
                sequences[CURRENT][SEQUENCE_NO_WIN]++;
                sequences[CURRENT][SEQUENCE_WIN] = 0;
            }
            else // Must be a win
            {
                aggregates[AGGREGATE_WON]++;

                sequences[CURRENT][SEQUENCE_WIN]++;
                sequences[CURRENT][SEQUENCE_NO_WIN] = 0;
                sequences[CURRENT][SEQUENCE_DRAW] = 0;

                if (keyResults[BIGGEST_WIN] == null || marginOfVictory > keyResults[BIGGEST_WIN].getMarginOfVictory())
                {
                    keyResults[BIGGEST_WIN] = result;
                }
            }
        }

        // Update score aggregates/sequences.
        aggregates[AGGREGATE_SCORED] += goalsFor;
        aggregates[AGGREGATE_CONCEDED] += goalsAgainst;
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
        return obj instanceof Team && super.equals(obj);
    }


    public static final class LeaguePosition
    {
        public final Date date;
        public final int position;

        public LeaguePosition(Date date, int position)
        {
            this.date = date;
            this.position = position;
        }
    }
}
