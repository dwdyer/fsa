package net.footballpredictions.footballstats.model;

/**
 * Base class for {@link TeamRecord} implementations.
 * @author Daniel Dyer
 */
public abstract class AbstractTeamRecord implements TeamRecord
{
    private static int pointsForWin = 3;
    private static int pointsForDraw = 1;

    private final Team team;
    private int pointsAdjustment = 0;

    protected AbstractTeamRecord(Team team)
    {
        this.team = team;
    }


    public Team getTeam()
    {
        return team;
    }

    
    public String getName()
    {
        return team.getName();
    }


    public int getGoalDifference()
    {
        return getScored() - getConceded();
    }


    public int getPoints()
    {
        return (getWon() * pointsForWin) + (getDrawn() * pointsForDraw) + pointsAdjustment;
    }


    public int getDroppedPoints()
    {
        return getPlayed() * pointsForWin - getPoints();
    }


    public double getAveragePoints()
    {
        return getPlayed() == 0 ? 0 : getPoints() / getPlayed();
    }


    public void adjustPoints(int amount)
    {
        pointsAdjustment += amount;
    }


    /**
     * Over-ride equals.  Records are equal if the teams are the same.
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof AbstractTeamRecord)
        {
            AbstractTeamRecord other = (AbstractTeamRecord) obj;
            return team.equals(other.getTeam());
        }
        return false;
    }


    /**
     * Over-ride hashCode because equals has also been over-ridden, to satisfy general contract
     * of equals.
     * Algorithm from Effective Java by Joshua Bloch.
     */
    @Override
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + team.hashCode();
        return result;
    }
}
