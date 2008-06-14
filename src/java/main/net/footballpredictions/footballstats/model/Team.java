// $Header: $
package net.footballpredictions.footballstats.model;

import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;

/**
 * Models a single team's record for the season.
 * @author Daniel Dyer
 * @since 21/12/2003
 * @version $Revision: $
 */
public final class Team
{
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

    private final String name;

    private final SortedMap<Date, Integer> leaguePositions = new TreeMap<Date, Integer>();

    private final StandardRecord homeRecord;
    private final StandardRecord awayRecord;
    private final StandardRecord overallRecord;

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
        this.homeRecord = new StandardRecord(this, TeamRecord.HOME);
        this.awayRecord = new StandardRecord(this, TeamRecord.AWAY);
        this.overallRecord = new StandardRecord(this, TeamRecord.BOTH);
    }


    public String getName()
    {
        return name;
    }

    
    public StandardRecord getRecord(int where)
    {
        switch (where)
        {
            case TeamRecord.HOME: return homeRecord;
            case TeamRecord.AWAY: return awayRecord;
            case TeamRecord.BOTH: return overallRecord;
            default: throw new IllegalArgumentException("Invalid venue type: " + where);
        }
    }


    public List<Result> getResults(int where)
    {
        return getRecord(where).getResults();
    }
    
    
    public SortedMap<Date, Integer> getLeaguePositions()
    {
        return leaguePositions;
    }
    
    
    public int getLastLeaguePosition()
    {
        return leaguePositions.get(leaguePositions.lastKey());
        
    }
    
    
    public int[][] getPointsData(int pointsForWin, int pointsForDraw)
    {
        List<Result> results = overallRecord.getResults();
        int[][] data = new int[results.size() + 1][2];
        int total = 0;
        data[0][0] = 0;
        data[0][1] = total;
        int index = 1;
        for (Result result : results)
        {
            if (result.isDraw())
            {
                total += pointsForDraw;
            }
            else if (result.isWin(this))
            {
                total += pointsForWin;
            }
            data[index][0] = index;
            data[index][1] = total;
            ++index;
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
        leaguePositions.put(date, position);
    }
    
    
    public void adjustPoints(int amount)
    {
        pointsAdjustment += amount;
    }


    public int getSequence(int when, int where, int sequence)
    {
        return getRecord(where).getSequence(when, sequence);
    }


    public String getForm(int where)
    {
        return getFormRecord(where).getForm();
    }


    /**
     * @return A {@link TeamRecord} that contains only data relating to the team's current
     * form.
     */
    public FormRecord getFormRecord(int where)
    {
        return getRecord(where).getFormRecord();
    }


    public int getAttendance(int type)
    {
        switch (type)
        {
            case ATTENDANCE_AVERAGE: return (int) ((double) aggregateCrowd / homeRecord.getPlayed() + 0.5);
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
        return where == TeamRecord.BOTH ? pointsAdjustment: 0;
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
        int where = result.getHomeTeam().equals(this) ? TeamRecord.HOME : TeamRecord.AWAY; // No error checking, assumes result is for this team.
        
        if (where == TeamRecord.HOME && result.getAttendance() >= 0) // Attendances away from home do not concern us.
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
        return obj instanceof Team && super.equals(obj);
    }
}
