// $Header: $
package net.footballpredictions.footballstats.model;

import java.util.Date;

/**
 * Immutable class to model the result of a single match.
 * @author Daniel Dyer
 * @since 21/12/2003
 * @version $Revision: $
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
        return team.equals(homeTeam) ? (homeGoals > awayGoals) : (awayGoals > homeGoals);
    }
    
    
    /**
     * Checks whether the specified team lost this game or not.  Assumes the specified
     * team is one of the two teams that contested the match.
     * @return true if this is a defeat for the specified team, false otherwise.
     */
    public boolean isDefeat(Team team)
    {
        return team.equals(homeTeam) ? (homeGoals < awayGoals) : (awayGoals < homeGoals);
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