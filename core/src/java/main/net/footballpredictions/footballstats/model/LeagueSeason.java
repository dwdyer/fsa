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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import net.footballpredictions.footballstats.data.LeagueDataProvider;
import net.footballpredictions.footballstats.util.FixedSizeSortedSet;

/**
 * Models a single season in a particular football league.
 * @author Daniel Dyer
 * @since 24/12/2003
 */
public final class LeagueSeason
{
    private final LeagueMetaData metaData;
    private final Map<String, Team> teamMappings = new TreeMap<String, Team>();
    private final SortedSet<String> teamNames;
    
    // Store a list of results for each date on which matches were played.  The map is sorted
    // with the earliest date first.
    private final SortedMap<Date, List<Result>> resultsByDate = new TreeMap<Date, List<Result>>();

    private final Comparator<Result> resultAttendanceComparator = new ResultAttendanceComparator();
    private final SortedSet<Result> topAttendances = new FixedSizeSortedSet<Result>(20, resultAttendanceComparator);
    private final SortedSet<Result> bottomAttendances = new FixedSizeSortedSet<Result>(20, Collections.reverseOrder(resultAttendanceComparator));

    private final SortedSet<Result> biggestHomeWins = new FixedSizeSortedSet<Result>(5, new ResultMarginComparator());
    private final SortedSet<Result> biggestAwayWins = new FixedSizeSortedSet<Result>(5, new ResultMarginComparator());
    private final SortedSet<Result> highestMatchAggregates = new FixedSizeSortedSet<Result>(5, new ResultAggregateComparator());
    
    private int matchCount = 0;
    private int aggregateHomeWins = 0;
    private int aggregateAwayWins = 0;
    private int aggregateScoreDraws = 0;
    private int aggregateNoScoreDraws = 0;
    private int aggregateHomeGoals = 0;
    private int aggregateAwayGoals = 0;
    private int aggregateCleansheets = 0;
    private int aggregateAttendance = 0;
    

    public LeagueSeason(LeagueDataProvider dataProvider)
    {
        this(dataProvider.getTeams(),
             dataProvider.getResults(),
             dataProvider.getPointsAdjustments(),
             dataProvider.getLeagueMetaData());
    }


    public LeagueSeason(SortedSet<String> teamNames,
                        List<Result> results,
                        Map<String, Integer> pointsAdjustments,
                        LeagueMetaData metaData)
    {
        this.metaData = metaData;
        this.teamNames = teamNames;
        for (String teamName : teamNames)
        {
            teamMappings.put(teamName, new Team(teamName,
                                                metaData.getPointsForWin(),
                                                metaData.getPointsForDraw(),
                                                metaData.getSplit()));
        }

        Collections.sort(results, new ResultDateComparator());

        for (Result result : results)
        {
            // Update global records.
            updateGlobalTotals(result);

            // Add result to list for that day.
            List<Result> resultsForDate = resultsByDate.get(result.getDate());
            if (resultsForDate == null)
            {
                resultsForDate = new LinkedList<Result>();
                resultsByDate.put(result.getDate(), resultsForDate);
            }
            resultsForDate.add(result);

            matchCount++;
        }

        for (Map.Entry<String, Integer> adjustment : pointsAdjustments.entrySet())
        {
            teamMappings.get(adjustment.getKey()).adjustPoints(adjustment.getValue());
        }

        processTeamRecords();
    }


    private void processTeamRecords()
    {
        // Add result to the record of each team.
        for (Date date : resultsByDate.keySet())
        {
            List<Result> results = resultsByDate.get(date);
            // Add current date's results to individual team records.
            for (Result result : results)
            {
                teamMappings.get(result.getHomeTeam()).addResult(result);
                teamMappings.get(result.getAwayTeam()).addResult(result);
            }
            // Calculate table for current date.
            Set<StandardRecord> table = getStandardLeagueTable(VenueType.BOTH);
            int index = 1;
            for (StandardRecord team : table)
            {
                team.getTeam().addLeaguePosition(date, index);
                ++index;
            }
        
            System.out.println("Processed results for " + date.toString());
        }
    }
    
    
    private void updateGlobalTotals(Result result)
    {
        if (result.isDraw())
        {
            if (result.getHomeGoals() == 0)
            {
                aggregateNoScoreDraws++;
                aggregateCleansheets += 2; // 0-0 draw means two cleansheets.
            }
            else
            {
                aggregateScoreDraws++;
            }
        }
        else
        {
            if (result.isWin(result.getHomeTeam()))
            {
                aggregateHomeWins++;
            }
            else
            {
                aggregateAwayWins++;
            }
            
            if (result.getHomeGoals() == 0 || result.getAwayGoals() == 0) // Can't both be zero otherwise it would be a draw.
            {
                aggregateCleansheets++;
            }
        }
        aggregateHomeGoals += result.getHomeGoals();
        aggregateAwayGoals += result.getAwayGoals();

        updateKeyResults(result);
        updateAttendances(result);
    }
    
    
    private void updateKeyResults(Result result)
    {
        if (result.isWin(result.getHomeTeam())) // Home Win
        {
            biggestHomeWins.add(result);
        }
        else if (result.isWin(result.getAwayTeam())) // Away Win
        {
            biggestAwayWins.add(result);
        }
        highestMatchAggregates.add(result);
    }
    
    
    private void updateAttendances(Result result)
    {
        if (result.getAttendance() >= 0)
        {
            aggregateAttendance += result.getAttendance();
            topAttendances.add(result);
            bottomAttendances.add(result);
        }
    }


    /**
     * Get a list of the names of all the teams in the division, sorted in alphabetical order.
     * @return An ordered collection of team names.
     */
    public SortedSet<String> getTeamNames()
    {
        return teamNames;
    }
    
    
    /**
     * @param teamName The name of the team to look-up.
     * @return The Team object for a particular team name.
     */
    public Team getTeam(String teamName)
    {
        return teamMappings.get(teamName);
    }
    
    
    /**
     * @return An ordered collection of dates on which matches took place, most recent first.
     */
    public SortedSet<Date> getDates()
    {
        SortedSet<Date> dates = new TreeSet<Date>(Collections.reverseOrder());
        dates.addAll(resultsByDate.keySet());
        return dates;
    }
    
    
    public Date getMostRecentDate()
    {
        return resultsByDate.lastKey();
    }
    
    
    /**
     * @param date The date of the matches to return.
     * @return An array of results for a particular date.
     */
    public List<Result> getResults(Date date)
    {
        return resultsByDate.get(date);
    }


    /**
     * Divide the league into two sections if an SPL-style split has been configured.
     * Otherwise all teams are in one section as in any sane league.
     * @param where The type of table that is being generated.
     * @return A list of league sections (will be either one or two).
     */
    private List<Collection<Team>> splitTeams(VenueType where)
    {
        List<Collection<Team>> result = new ArrayList<Collection<Team>>(2);
        // Only split the teams if a split has been configured.  Don't split home/away tables.
        if (metaData.getSplit() > 0 && where == VenueType.BOTH)
        {
            SortedSet<SplitRecord> splitTable = new TreeSet<SplitRecord>(new LeagueTableComparator());
            for (Team team : teamMappings.values())
            {
                splitTable.add(team.getSplitRecord());
            }
            List<Team> teams = new ArrayList<Team>(teamNames.size());
            for (SplitRecord record : splitTable)
            {
                teams.add(record.getTeam());
            }
            result.add(teams.subList(0, teams.size() / 2));
            result.add(teams.subList(teams.size() / 2, teams.size()));
        }
        else // All teams are in one league section.
        {
            result.add(teamMappings.values());
        }
        return result;
    }
    
    
    /**
     * Sorts the teams into standard league table order (in order of points won).
     * @param where Whether the table is for home games, away games or both.
     * @return A collection of team records ordered by league position.
     */
    public Set<StandardRecord> getStandardLeagueTable(VenueType where)
    {
        List<Collection<Team>> splits = splitTeams(where);

        Set<StandardRecord> leagueTable = new LinkedHashSet<StandardRecord>(teamNames.size());
        for (Collection<Team> split : splits)
        {
            SortedSet<StandardRecord> subTable = new TreeSet<StandardRecord>(new LeagueTableComparator());
            for (Team team : split)
            {
                subTable.add(team.getRecord(where));
            }
            leagueTable.addAll(subTable);
        }
        return leagueTable;
    }
    
    
    /**
     * Sorts the teams in order of average points won per game.
     * @param where Whether the table is for home games, away games or both.
     * @return A collection of team records ordered by points per game (highest first).
     */
    public Set<StandardRecord> getAverageLeagueTable(VenueType where)
    {
        SortedSet<StandardRecord> leagueTable = new TreeSet<StandardRecord>(new PointsPerGameComparator());
        for (Team team : teamMappings.values())
        {
            leagueTable.add(team.getRecord(where));
        }
        return leagueTable;
    }
    
    
    /**
     * Sorts the teams in order of fewest points lost.
     * @param where Whether the table is for home games, away games or both.
     * @return A collection of team records ordered by points dropped (fewest first).
     */
    public Set<StandardRecord> getInvertedLeagueTable(VenueType where)
    {
        SortedSet<StandardRecord> leagueTable = new TreeSet<StandardRecord>(new DroppedPointsComparator());
        for (Team team : teamMappings.values())
        {
            leagueTable.add(team.getRecord(where));
        }
        return leagueTable;
    }
    
    
    public Set<FormRecord> getFormTable(VenueType where)
    {
        SortedSet<FormRecord> formTeams = new TreeSet<FormRecord>(new LeagueTableComparator());
        for (Team team : teamMappings.values())
        {
            formTeams.add(team.getRecord(where).getFormRecord());
        }
        return formTeams;
    }


    /**
     * @param type Which sequence to use.
     * @param where Whether the sequence is for home games, away games or both.
     * @param current Whether to use the current value of the sequence or the season's best sequence.
     * @return A set of teams, sorted in descending order of the sequence specified by the above parameters.
     */
    public Set<StandardRecord> getSequenceTable(SequenceType type,
                                                VenueType where,
                                                boolean current)
    {
        SortedSet<StandardRecord> sequenceTable = new TreeSet<StandardRecord>(new SequenceComparator(type, current));
        for (Team team : teamMappings.values())
        {
            StandardRecord record = team.getRecord(where);
            List<Result> sequence = current ? record.getCurrentSequence(type) : record.getBestSequence(type);
            if (!sequence.isEmpty()) // Don't include teams with zero-length sequences.
            {
                sequenceTable.add(record);
            }
        }
        return sequenceTable;
    }
    
    
    public Set<Team> getAttendanceTable(int type)
    {
        SortedSet<Team> sortedTeams = new TreeSet<Team>(new TeamAttendanceComparator(type));
        sortedTeams.addAll(teamMappings.values());
        return sortedTeams;
    }
    
    
    public SortedSet<Result> getBiggestHomeWins()
    {
        return biggestHomeWins;
    }


    public SortedSet<Result> getBiggestAwayWins()
    {
        return biggestAwayWins;
    }
    
    
    public SortedSet<Result> getHighestMatchAggregates()
    {
        return highestMatchAggregates;
    }

    
    public SortedSet<Result> getHighestAttendances()
    {
        return topAttendances;
    }
    
    
    public SortedSet<Result> getLowestAttendances()
    {
        return bottomAttendances;
    }
    
    
    public int getMatchCount()
    {
        return matchCount;
    }
    
    
    public int getHomeWins()
    {
        return aggregateHomeWins;
    }
    
    
    public int getAwayWins()
    {
        return aggregateAwayWins;
    }
    
    
    public int getScoreDraws()
    {
        return aggregateScoreDraws;
    }
    
    
    public int getNoScoreDraws()
    {
        return aggregateNoScoreDraws;
    }
    
    
    public int getHomeGoals()
    {
        return aggregateHomeGoals;
    }
    
    
    public int getAwayGoals()
    {
        return aggregateAwayGoals;
    }
    
    
    public int getCleansheets()
    {
        return aggregateCleansheets;
    }
    
    
    public int getAggregateAttendance()
    {
        return aggregateAttendance;
    }
    
    
    public int getAverageAttendance()
    {
        return (int) ((float) aggregateAttendance / matchCount + 0.5);
    }


    public LeagueMetaData getMetaData()
    {
        return metaData;
    }
}
