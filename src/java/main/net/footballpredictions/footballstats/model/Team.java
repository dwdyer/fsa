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
public final class Team
{
    // Constants for home/away/both.
    public static final int HOME = 0;
    public static final int AWAY = 1;
    public static final int BOTH = 2;
    
    // Constants for standards stats.
    public static final int AGGREGATE_PLAYED = 0;
    public static final int AGGREGATE_WON = 1;
    public static final int AGGREGATE_DRAWN = 2;
    public static final int AGGREGATE_LOST = 3;
    public static final int AGGREGATE_SCORED = 4;
    public static final int AGGREGATE_CONCEDED = 5;

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

    // Team data.
    private final String name;    
    private final List<Result> results = new ArrayList<Result>(46); // Most leagues have no more than 46 games per team.
    private final List<LeaguePosition> leaguePositions = new ArrayList<LeaguePosition>(46);
           
    /**
     * Two dimensional array for storing aggregates (i.e. league table data).
     * Only store home and away totals because overall totals can be calculated from these.
     */
    private final int[][] aggregates = new int[2][6];

    /**
     * Three-dimensional array for storing sequence data.  First dimension is
     * current/season, second is venue (home/away/both), third is sequence type.
     */
    private final int[][][] sequences = new int[2][3][8];
    
    private final Result[][] form = new Result[][]{new Result[4], new Result[4], new Result[6]};
    private final int[] formPointers = new int[3]; // Indices of most recent results in form array.
    
    private final Result[][] keyResults = new Result[3][3];
    
    private int lowestCrowd;
    private int highestCrowd;
    private int aggregateCrowd;
    
    private int pointsAdjustment = 0;
    
    /**
     * Constructor, sets name.  All other data is added via the addResult method later.
     */
    public Team(String name)
    {
        this.name = name;
    }
    
    
    public String getName()
    {
        return name;
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
            resultsArray = new Result[getAggregate(where, AGGREGATE_PLAYED)];
            int count = 0;
            for (int i = 0; i < results.size() && count < resultsArray.length; i++)
            {
                Result result = results.get(i);
                if ((where == HOME && result.homeTeam.equals(this))
                    || (where == AWAY && result.awayTeam.equals(this)))
                {
                    resultsArray[count] = result;
                    count++;
                }
            }
        }
        return resultsArray;
    }
    
    
    public LeaguePosition[] getLeaguePositions()
    {
        LeaguePosition[] positions = new LeaguePosition[leaguePositions.size()];
        leaguePositions.toArray(positions);
        return positions;
    }
    
    
    public int getLastLeaguePosition()
    {
        return (leaguePositions.get(leaguePositions.size() - 1)).position;
        
    }
    
    
    public int[][] getPointsData(int pointsForWin, int pointsForDraw)
    {
        int[][] data = new int[results.size() + 1][2];
        int total = 0;
        data[0][0] = 0;
        data[0][1] = total;
        for (int i = 0; i < results.size(); i++)
        {
            Result result = results.get(i);
            if (result.isDraw())
            {
                total += pointsForDraw;
            }
            else if (result.isWin(this))
            {
                total += pointsForWin;
            }
            int x = i + 1;
            data[x][0] = x;
            data[x][1] = total;
        }
        // TO DO: What about points adjustments?
        return data;
    }
    
    
    public void addResult(Result result)
    {
        results.add(result);
        updateAggregatesAndSequences(result);
        updateForm(result);
        updateAttendanceFigures(result);
    }
    
    
    public void addLeaguePosition(Date date, int position)
    {
        leaguePositions.add(new LeaguePosition(date, position));
    }
    
    
    public void adjustPoints(int amount)
    {
        pointsAdjustment += amount;
    }
    
    
    public int getAggregate(int where, int aggregate, boolean form)
    {
        return form ? getFormAggregate(where, aggregate) : getAggregate(where, aggregate);
    }
    
    
    /**
     * Helper method for three parameter version of getAggregate.
     */
    private int getAggregate(int where, int aggregate)
    {
        if (where == BOTH)
        {
            return aggregates[HOME][aggregate] + aggregates[AWAY][aggregate];
        }
        else
        {
            return aggregates[where][aggregate];
        }
    }
    
    
    /**
     * @return The difference between the number of goals scored by this team and the number
     * conceded.  A positive value indicates more goals scored than conceded, a negative value
     * indicates more conceded than scored.
     */
    public int getGoalDifference(int where, boolean form)
    {
        return getAggregate(where, AGGREGATE_SCORED, form) - getAggregate(where, AGGREGATE_CONCEDED, form);
    }
    
    
    public int getSequence(int when, int where, int sequence)
    {
        return sequences[when][where][sequence];
    }
    

    /**
     * @return A String representation of this teams current form.  Either home form,
     * away form or combined form depending on the method argument.
     */
    public String getForm(int where)
    {
        StringBuffer formString = new StringBuffer();
        for (int i = 0; i < form[where].length; i++)
        {
            int index = (formPointers[where] + i) % form[where].length;
            if (form[where][index] == null)
            {
                formString.append('-');
            }
            else if (form[where][index].isDraw())
            {
                formString.append('D');
            }
            else if (form[where][index].isWin(this))
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
    private int getFormAggregate(int where, int aggregate)
    {
        int value = 0;
        switch (aggregate)
        {
            case AGGREGATE_PLAYED:
            {
                return Math.min(getAggregate(where, AGGREGATE_PLAYED), form[where].length);
            }
            case AGGREGATE_WON:
            {
                for (int i = 0; i < form[where].length; i++)
                {
                    if (form[where][i] != null && form[where][i].isWin(this))
                    {
                        value++;
                    }
                }
                break;
            }
            case AGGREGATE_DRAWN:
            {
                for (int i = 0; i < form[where].length; i++)
                {
                    if (form[where][i] != null && form[where][i].isDraw())
                    {
                        value++;
                    }
                }
                break;
            }
            case AGGREGATE_LOST:
            {
                for (int i = 0; i < form[where].length; i++)
                {
                    if (form[where][i] != null && form[where][i].isDefeat(this))
                    {
                        value++;
                    }
                }
                break;
            }
            case AGGREGATE_SCORED:
            {
                for (int i = 0; i < form[where].length; i++)
                {
                    if (form[where][i] != null)
                    {
                        value += form[where][i].getGoalsFor(this);
                    }
                }
                break;
            }
            case AGGREGATE_CONCEDED:
            {
                for (int i = 0; i < form[where].length; i++)
                {
                    if (form[where][i] != null)
                    {
                        value += form[where][i].getGoalsAgainst(this);
                    }
                }
            }
        }
        return value;
    }
    
    
    public Result getKeyResult(int where, int key)
    {
        return keyResults[where][key];
    }
    
    
    public int getAttendance(int type)
    {
        switch (type)
        {
            case ATTENDANCE_AVERAGE: return (int) ((double) aggregateCrowd / getAggregate(HOME, AGGREGATE_PLAYED) + 0.5);
            case ATTENDANCE_HIGHEST: return highestCrowd;
            case ATTENDANCE_LOWEST: return lowestCrowd;
            case ATTENDANCE_AGGREGATE: return aggregateCrowd;
        }
        return -1;
    }
    
    
    /**
     * Returns the number of points awarded to or deducted from this team during
     * the season.  This is not points for results but points adjustments made by
     * the league administrators for rules infringements.
     * @return The adjustment if where is set to BOTH, zero otherwise.
     */
    public int getPointsAdjustment(int where)
    {
        return where == BOTH ? pointsAdjustment: 0;
    }
    
    
    /**
     * Returns interesting facts about the team's form.
     */
    public String[] getNotes(int where)
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
        if (getSequence(CURRENT, where, SEQUENCE_UNBEATEN) >= 3)
        {
            notes.add("Unbeaten in last " + getSequence(CURRENT, where, SEQUENCE_UNBEATEN) + end);
        }
        if (getSequence(CURRENT, where, SEQUENCE_NO_WIN) >= 3)
        {
            notes.add("Haven't won in last " + getSequence(CURRENT, where, SEQUENCE_NO_WIN) + end);
        }        
        
        // Check win/loss sequences.
        if (getSequence(CURRENT, where, SEQUENCE_WIN) >= 3)
        {
            notes.add("Won last " + getSequence(CURRENT, where, SEQUENCE_WIN) + end);
        }
        else if (getSequence(CURRENT, where, SEQUENCE_DRAW) >= 3)
        {
            notes.add("Drawn last " + getSequence(CURRENT, where, SEQUENCE_DRAW) + end);
        }
        else if (getSequence(CURRENT, where, SEQUENCE_DEFEAT) >= 3)
        {
            notes.add("Lost last " + getSequence(CURRENT, where, SEQUENCE_DEFEAT) + end);
        }
        
        // Check cleansheet/scoring sequences.
        if (getSequence(CURRENT, where, SEQUENCE_NO_GOAL) >= 3)
        {
            notes.add("Haven't scored in last " + getSequence(CURRENT, where, SEQUENCE_NO_GOAL) + end);
        }
        if (getSequence(CURRENT, where, SEQUENCE_CLEANSHEET) >= 3)
        {
            notes.add("Haven't conceded in last " + getSequence(CURRENT, where, SEQUENCE_CLEANSHEET) + end);
        }
        if (getSequence(CURRENT, where, SEQUENCE_SCORED) >= 10)
        {
            notes.add("Scored in last " + getSequence(CURRENT, where, SEQUENCE_SCORED) + end);
        }
        
        String[] noteStrings = new String[notes.size()];
        notes.toArray(noteStrings);
        return noteStrings;
    }
    
    
    private void updateAggregatesAndSequences(Result result)
    {
        int where = result.homeTeam.equals(this) ? HOME : AWAY; // No error checking, assumes result is for this team.
        
        int goalsFor = result.getGoalsFor(this);
        int goalsAgainst = result.getGoalsAgainst(this);
        int marginOfVictory = result.getMarginOfVictory();
        
        // Update last result.
        keyResults[where][LAST_RESULT] = result;
        keyResults[BOTH][LAST_RESULT] = result;
        
        aggregates[where][AGGREGATE_PLAYED]++;
        
        // Update result aggregates/sequences.
        if (result.isDefeat(this))
        {
            aggregates[where][AGGREGATE_LOST]++;
            
            sequences[CURRENT][where][SEQUENCE_NO_WIN]++;
            sequences[CURRENT][where][SEQUENCE_DEFEAT]++;
            sequences[CURRENT][where][SEQUENCE_UNBEATEN] = 0;
            sequences[CURRENT][where][SEQUENCE_WIN] = 0;
            sequences[CURRENT][where][SEQUENCE_DRAW] = 0;
            sequences[CURRENT][BOTH][SEQUENCE_NO_WIN]++;
            sequences[CURRENT][BOTH][SEQUENCE_DEFEAT]++;
            sequences[CURRENT][BOTH][SEQUENCE_UNBEATEN] = 0;
            sequences[CURRENT][BOTH][SEQUENCE_WIN] = 0;
            sequences[CURRENT][BOTH][SEQUENCE_DRAW] = 0;
            
            if (keyResults[where][BIGGEST_DEFEAT] == null || marginOfVictory > keyResults[where][BIGGEST_DEFEAT].getMarginOfVictory())
            {
                keyResults[where][BIGGEST_DEFEAT] = result;
            }
            if (keyResults[BOTH][BIGGEST_DEFEAT] == null || marginOfVictory > keyResults[BOTH][BIGGEST_DEFEAT].getMarginOfVictory())
            {
                keyResults[BOTH][BIGGEST_DEFEAT] = result;
            }
        }
        else
        {
            sequences[CURRENT][where][SEQUENCE_UNBEATEN]++;
            sequences[CURRENT][where][SEQUENCE_DEFEAT] = 0;
            sequences[CURRENT][BOTH][SEQUENCE_UNBEATEN]++;
            sequences[CURRENT][BOTH][SEQUENCE_DEFEAT] = 0;
            
            if (result.isDraw())
            {
                aggregates[where][AGGREGATE_DRAWN]++;
                
                sequences[CURRENT][where][SEQUENCE_DRAW]++;
                sequences[CURRENT][where][SEQUENCE_NO_WIN]++;
                sequences[CURRENT][where][SEQUENCE_WIN] = 0;
                sequences[CURRENT][BOTH][SEQUENCE_DRAW]++;
                sequences[CURRENT][BOTH][SEQUENCE_NO_WIN]++;
                sequences[CURRENT][BOTH][SEQUENCE_WIN] = 0;
            }
            else // Must be a win
            {
                aggregates[where][AGGREGATE_WON]++;
                
                sequences[CURRENT][where][SEQUENCE_WIN]++;
                sequences[CURRENT][where][SEQUENCE_NO_WIN] = 0;
                sequences[CURRENT][where][SEQUENCE_DRAW] = 0;
                sequences[CURRENT][BOTH][SEQUENCE_WIN]++;
                sequences[CURRENT][BOTH][SEQUENCE_NO_WIN] = 0;
                sequences[CURRENT][BOTH][SEQUENCE_DRAW] = 0;
                
                if (keyResults[where][BIGGEST_WIN] == null || marginOfVictory > keyResults[where][BIGGEST_WIN].getMarginOfVictory())
                {
                    keyResults[where][BIGGEST_WIN] = result;
                }
                if (keyResults[BOTH][BIGGEST_WIN] == null || marginOfVictory > keyResults[BOTH][BIGGEST_WIN].getMarginOfVictory())
                {
                    keyResults[BOTH][BIGGEST_WIN] = result;
                }
            }
        }
        
        // Update score aggregates/sequences.
        aggregates[where][AGGREGATE_SCORED] += goalsFor;
        aggregates[where][AGGREGATE_CONCEDED] += goalsAgainst;
        if (goalsFor == 0)
        {
            sequences[CURRENT][where][SEQUENCE_NO_GOAL]++;
            sequences[CURRENT][BOTH][SEQUENCE_NO_GOAL]++;
            sequences[CURRENT][where][SEQUENCE_SCORED] = 0;
            sequences[CURRENT][BOTH][SEQUENCE_SCORED] = 0;
        }
        else
        {
            sequences[CURRENT][where][SEQUENCE_NO_GOAL] = 0;
            sequences[CURRENT][BOTH][SEQUENCE_NO_GOAL] = 0;
            sequences[CURRENT][where][SEQUENCE_SCORED]++;
            sequences[CURRENT][BOTH][SEQUENCE_SCORED]++;
        }
        
        if (goalsAgainst == 0)
        {
            sequences[CURRENT][where][SEQUENCE_CLEANSHEET]++;
            sequences[CURRENT][BOTH][SEQUENCE_CLEANSHEET]++;
        }
        else
        {
            sequences[CURRENT][where][SEQUENCE_CLEANSHEET] = 0;
            sequences[CURRENT][BOTH][SEQUENCE_CLEANSHEET] = 0;
        }
        
        // Update season's best sequences, if necessary.
        for (int i = 0; i < 8; i++) // 8 is number of sequences.
        {
            sequences[SEASON][where][i] = Math.max(sequences[SEASON][where][i], sequences[CURRENT][where][i]);
            sequences[SEASON][BOTH][i] = Math.max(sequences[SEASON][BOTH][i], sequences[CURRENT][BOTH][i]);
        }
    }
    
    
    private void updateForm(Result result)
    {
        int where = result.homeTeam.equals(this) ? HOME : AWAY; // No error checking, assumes result is for this team.
        form[where][formPointers[where]] = result;
        form[BOTH][formPointers[BOTH]] = result;
        formPointers[where] = formPointers[where] < form[where].length - 1 ? formPointers[where] + 1 : 0;
        formPointers[BOTH] = formPointers[BOTH] < form[BOTH].length - 1 ? formPointers[BOTH] + 1 : 0;
    }
    
    
    /**
     * Update the aggregate attendance and, if necessary, the
     * highest or lowest attendance.
     */
    private void updateAttendanceFigures(Result result)
    {
        int where = result.homeTeam.equals(this) ? HOME : AWAY; // No error checking, assumes result is for this team.
        
        if (where == HOME && result.attendance >= 0) // Attendances away from home do not concern us.
        {
            aggregateCrowd += result.attendance;
            if (result.attendance > highestCrowd)
            {
                highestCrowd = result.attendance;
            }
            if (result.attendance < lowestCrowd || lowestCrowd == 0)
            {
                lowestCrowd = result.attendance;
            }
        }
    }
    
    
    /**
     * Over-ride equals.  Teams are equal if the names are equal.
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Team)
        {
            Team other = (Team) obj;
            return name.equals(other.name);
        }
        return false;
    }
    
    
    /**
     * Over-ride hashCode because equals has also been over-ridden, to satisfy general contract
     * of equals.
     * Algorithm from Effective Java by Joshua Bloch.
     */
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + name.hashCode();
        return result;
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
