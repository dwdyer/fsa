package net.footballpredictions.footballstats.model;

/**
 * This interface is similar to the one defined by java.util.Comparator since J2SE version
 * 1.2, but since the model classes must work with Java version 1.1, we cannot use that
 * interface.  This definition is specialised for comparing Team objects since that is all we
 * are concerned with.
 * @author Daniel Dyer
 */
interface TeamComparator
{
    public int compareTeams(Team team1, Team team2);
}