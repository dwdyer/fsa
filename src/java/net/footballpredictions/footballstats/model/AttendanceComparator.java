package net.footballpredictions.footballstats.model;

/**
 * Comparator for sorting a set of teams by a particular attendance statistic.
 * @author Daniel Dyer.
 */
final class AttendanceComparator implements TeamComparator
{
    private int type;
        
    public void setType(int type)
    {
        this.type = type;
    }

        
    public int compareTeams(Team team1, Team team2)
    {
        int compare = team2.getAttendance(type) - team1.getAttendance(type); // Swap teams for descending sort.
        if (compare == 0)
        {
            // If records are the same, sort on alphabetical order.
            compare = team1.getName().toLowerCase().compareTo(team2.getName().toLowerCase());
        }
        return compare;
    }
}