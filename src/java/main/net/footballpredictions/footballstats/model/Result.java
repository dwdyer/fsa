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

/**
 * Immutable class to model the result of a single match.
 * @author Daniel Dyer
 * @since 21/12/2003
 */
public final class Result
{
    private final Team homeTeam;
    private final Team awayTeam;
    private final int homeGoals;
    private final int awayGoals;
    private final int attendance;
    private final Date date;
    
    
    /**
     * Constructor, sets all of the immutable fields.  Attendance should be set to -1 if the
     * data is not available.
     */
    public Result(Team homeTeam,
                  Team awayTeam,
                  int homeGoals,
                  int awayGoals,
                  int attendance,
                  Date date)
    {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        this.attendance = attendance;
        this.date = date;
    }
    
    
    /**
     * Checks whether the specified team won this game or not.  Assumes the specified
     * team is one of the two teams that contested the match.
     * @return true if this is a win for the specified team, false otherwise.
     */
    public boolean isWin(Team team)
    {
        return team.getName().equals(homeTeam.getName())
               ? (homeGoals > awayGoals)
               : (awayGoals > homeGoals);
    }
    
    
    /**
     * Checks whether the specified team lost this game or not.  Assumes the specified
     * team is one of the two teams that contested the match.
     * @return true if this is a defeat for the specified team, false otherwise.
     */
    public boolean isDefeat(Team team)
    {
        return team.getName().equals(homeTeam.getName())
               ? (homeGoals < awayGoals)
               : (awayGoals < homeGoals);
    }
    
    
    /**
     * Checks whether the game was a draw or not.
     * @return true if the scores are equal.
     */
    public boolean isDraw()
    {
        return homeGoals == awayGoals;
    }
    
    
    public int getGoalsFor(Team team)
    {
        return team.equals(homeTeam) ? homeGoals : awayGoals;
    }
    
    
    public int getGoalsAgainst(Team team)
    {
        return team.equals(homeTeam) ? awayGoals : homeGoals;
    }
    
    
    /**
     * @return The difference, in goals, between the two teams in this match, zero for a draw.
     */
    public int getMarginOfVictory()
    {
        return Math.abs(homeGoals - awayGoals);
    }


    /**
     * @return The total number of goals scored by both teams.
     */
    public int getMatchAggregate()
    {
        return homeGoals + awayGoals;
    }


    public Team getHomeTeam()
    {
        return homeTeam;
    }


    public Team getAwayTeam()
    {
        return awayTeam;
    }


    public int getHomeGoals()
    {
        return homeGoals;
    }


    public int getAwayGoals()
    {
        return awayGoals;
    }


    public int getAttendance()
    {
        return attendance;
    }

    
    public Date getDate()
    {
        return date;
    }
}