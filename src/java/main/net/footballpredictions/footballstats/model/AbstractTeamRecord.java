package net.footballpredictions.footballstats.model;

/**
 * Base class for {@link TeamRecord} implementations.
 * @author Daniel Dyer
 */
public abstract class AbstractTeamRecord implements TeamRecord
{
    // Team data.
    private final Team team;

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
        return getAggregate(AGGREGATE_SCORED) - getAggregate(AGGREGATE_CONCEDED);
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
