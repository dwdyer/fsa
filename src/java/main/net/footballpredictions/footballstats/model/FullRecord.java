// $Header: $
package net.footballpredictions.footballstats.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Models a single team's record for the season.
 * @author Daniel Dyer
 * @since 21/12/2003
 * @version $Revision: $
 */
public final class FullRecord extends AbstractTeamRecord
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

    private final List<LeaguePosition> leaguePositions = new ArrayList<LeaguePosition>(46);

    private final PartialRecord homeRecord;
    private final PartialRecord awayRecord;
    private final PartialRecord overallRecord;

    private int lowestCrowd;
    private int highestCrowd;
    private int aggregateCrowd;
    
    private int pointsAdjustment = 0;
    
    /**
     * Constructor, sets name.  All other data is added via the addResult method later.
     */
    public FullRecord(String name)
    {
        super(name);
        this.homeRecord = new PartialRecord(name, HOME);
        this.awayRecord = new PartialRecord(name, AWAY);
        this.overallRecord = new PartialRecord(name, BOTH);
    }


    private PartialRecord getRecord(int where)
    {
        switch (where)
        {
            case HOME: return homeRecord;
            case AWAY: return awayRecord;
            case BOTH: return overallRecord;
            default: throw new IllegalArgumentException("Invalid venue type: " + where);
        }
    }


    public Result[] getResults(int where)
    {
        return getRecord(where).getResults(where);
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
        Result[] results = overallRecord.getResults(BOTH);
        int[][] data = new int[results.length + 1][2];
        int total = 0;
        data[0][0] = 0;
        data[0][1] = total;
        for (int i = 0; i < results.length; i++)
        {
            Result result = results[i];
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
        homeRecord.addResult(result);
        awayRecord.addResult(result);
        overallRecord.addResult(result);
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


    /**
     * Helper method for three parameter version of getAggregate.
     */
    public int getAggregate(int where, int aggregate)
    {
        return getRecord(where).getAggregate(where, aggregate);
    }


    public int getSequence(int when, int where, int sequence)
    {
        return getRecord(where).getSequence(when, sequence);
    }


    public String getForm(int where)
    {
        return getFormRecord(where).getForm(where);
    }


    /**
     * @return A {@link TeamRecord} that contains only data relating to the team's current
     * form.
     */
    public FormRecord getFormRecord(int where)
    {
        return getRecord(where).getFormRecord();
    }


    public Result getKeyResult(int where, int key)
    {
        return getRecord(where).getKeyResult(key);
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
        return getRecord(where).getNotes();
    }

    
    /**
     * Update the aggregate attendance and, if necessary, the
     * highest or lowest attendance.
     */
    private void updateAttendanceFigures(Result result)
    {
        int where = result.getHomeTeam().equals(this) ? HOME : AWAY; // No error checking, assumes result is for this team.
        
        if (where == HOME && result.getAttendance() >= 0) // Attendances away from home do not concern us.
        {
            aggregateCrowd += result.getAttendance();
            if (result.getAttendance() > highestCrowd)
            {
                highestCrowd = result.getAttendance();
            }
            if (result.getAttendance() < lowestCrowd || lowestCrowd == 0)
            {
                lowestCrowd = result.getAttendance();
            }
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
        return obj instanceof FullRecord && super.equals(obj);
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
