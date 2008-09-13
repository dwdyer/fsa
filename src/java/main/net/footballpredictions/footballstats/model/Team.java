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

import java.util.Date;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;

/**
 * Provides access to all aspects of a team's record for the season.
 * @author Daniel Dyer
 * @since 21/12/2003
 */
public final class Team
{
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
    
    /**
     * Constructor, sets name.  All other data is added via the addResult method later.
     * @param name The name of the team.
     */
    public Team(String name, ResourceBundle res)
    {
        this.name = name;
        this.homeRecord = new StandardRecord(this, VenueType.HOME, res);
        this.awayRecord = new StandardRecord(this, VenueType.AWAY, res);
        this.overallRecord = new StandardRecord(this, VenueType.BOTH, res);
    }


    public String getName()
    {
        return name;
    }

    
    public StandardRecord getRecord(VenueType where)
    {
        switch (where)
        {
            case HOME: return homeRecord;
            case AWAY: return awayRecord;
            case BOTH: return overallRecord;
            default: throw new IllegalArgumentException("Invalid venue type: " + where);
        }
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
        data[0][0] = 0;
        data[0][1] = 0;
        int total = 0;
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
        overallRecord.addResult(result);
        if (result.getHomeTeam().getName().equals(getName()))
        {
            homeRecord.addResult(result);
            // Attendances away from home do not concern us.
            updateAttendanceFigures(result);
        }
        else if (result.getAwayTeam().getName().equals(getName()))
        {
            awayRecord.addResult(result);
        }
    }
    
    
    public void addLeaguePosition(Date date, int position)
    {
        leaguePositions.put(date, position);
    }
    
    
    public void adjustPoints(int amount)
    {
        // Only apply the points adjustment to the overall record.
        overallRecord.adjustPoints(amount);
    }


    public int getAttendanceRecord(int type)
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
     * Update the aggregate attendance and, if necessary, the
     * highest or lowest attendance.  This method assumes that only
     * home results will be passed in.
     * @param result The attendance from this result will be recorded.
     */
    private void updateAttendanceFigures(Result result)
    {
        assert result.getHomeTeam().equals(this) : "Not a home game for this team.";
        if (result.getAttendance() >= 0) // Negative value means attendance data is not available.
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
