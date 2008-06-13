package net.footballpredictions.footballstats.model;

/**
 * Base class for {@link TeamRecord} implementations.
 * @author Daniel Dyer
 */
public abstract class AbstractTeamRecord implements TeamRecord
{
    // Team data.
    private final String name;

    protected AbstractTeamRecord(String name)
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
    }


    public int getGoalDifference(int where)
    {
        return getAggregate(where, AGGREGATE_SCORED) - getAggregate(where, AGGREGATE_CONCEDED);
    }


    /**
     * Over-ride equals.  Teams are equal if the names are equal.
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof AbstractTeamRecord)
        {
            AbstractTeamRecord other = (AbstractTeamRecord) obj;
            return name.equals(other.getName());
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
        result = 37 * result + name.hashCode();
        return result;
    }
}
