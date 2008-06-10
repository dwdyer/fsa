// $Header: $
package net.footballpredictions.footballstats.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Models a single season in a particular football league.
 * @author Daniel Dyer
 * @since 24/12/2003
 * @version $Revision: $
 */
public final class LeagueSeason
{
    public int pointsForWin = 3;
    public int pointsForDraw = 1;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    private static final String POINTS_TAG = "POINTS";
    private static final String PRIZE_TAG = "PRIZE";
    private static final String RELEGATION_TAG = "RELEGATION";
    private static final String AWARDED_TAG = "AWARDED";
    private static final String DEDUCTED_TAG = "DEDUCTED";
    
    private final Map<String, Team> teamMappings = new HashMap<String, Team>();
    private Team[] teams;
    private String[] teamNames;
    private final SortedSet<Date> dates = new TreeSet<Date>(Collections.reverseOrder()); // Most recent first.
    private final Map<Date, List<Result>> resultsByDate = new HashMap<Date, List<Result>>();
    private final Vector topAttendances = new Vector(21);
    private final Vector bottomAttendances = new Vector(21);
    private final Vector biggestHomeWins = new Vector(5);
    private final Vector biggestAwayWins = new Vector(5);
    private final Vector highestMatchAggregates = new Vector(5);
    
    private int matchCount = 0;
    private int aggregateHomeWins = 0;
    private int aggregateAwayWins = 0;
    private int aggregateScoreDraws = 0;
    private int aggregateNoScoreDraws = 0;
    private int aggregateHomeGoals = 0;
    private int aggregateAwayGoals = 0;
    private int aggregateCleansheets = 0;
    private int aggregateAttendance = 0;
    
    private int[] zones;
    private String[] prizeZoneNames;
    private String[] relegationZoneNames;
    
    private int highestPointsTotal = 0;
 
    /**
     * Comparator for sorting a standard league/form table.
     */
    private final LeagueTableComparator standardComparator = new LeagueTableComparator()
    {
        public int doMainComparison(Team team1, Team team2)
        {
            return getPoints(where, team2, form) - getPoints(where, team1, form); // Swap teams for descending sort.
        }
    };
    
    
    /**
     * Comparator for sorting a league table by average points per game.
     */
    private final LeagueTableComparator averagesComparator = new LeagueTableComparator()
    {
        public int doMainComparison(Team team1, Team team2)
        {
            double difference = getAveragePoints(where, team2) - getAveragePoints(where, team1); // Swap teams for descending sort.
            // Convert to int (sign is more important than value).
            if (difference == 0)
            {
                return 0;
            }
            else
            {
                return difference > 0 ? 1 : -1;
            }
        }
    };
    
    
    /**
     * Comparator for sorting a league table in order of fewest points dropped.
     */
    private final LeagueTableComparator invertedComparator = new LeagueTableComparator()
    {
        public int doMainComparison(Team team1, Team team2)
        {
            return getPointsDropped(where, team1) - getPointsDropped(where, team2);
        }
    };
        
    private final SequenceComparator sequenceComparator = new SequenceComparator();
    private final AttendanceComparator attendanceComparator = new AttendanceComparator();
    
    
    public LeagueSeason(URL resultsURL)
    {
        try
        {
            // Local class for temporarily storing league position data.
            final class LeagueZone
            {
                public final int startPos;
                public final int endPos;
                public final String name;
        
                public LeagueZone(int startPos, int endPos, String name)
                {
                    this.startPos = startPos;
                    this.endPos = endPos;
                    this.name = name;
                }
            }
            List<LeagueZone> prizeZones = new LinkedList<LeagueZone>();
            List<LeagueZone> relegationZones = new LinkedList<LeagueZone>();
            
            BufferedReader resultsFile = new BufferedReader(new InputStreamReader(resultsURL.openStream()));
            String nextLine = resultsFile.readLine();
            while (nextLine != null)
            {
                nextLine = nextLine.trim();
                if (nextLine.length() > 0)
                {
                    StringTokenizer nextResult = new StringTokenizer(nextLine, "|");

                    String dateString = nextResult.nextToken();
                    if (Character.isDigit(dateString.charAt(0))) // Process as a result if first char is a number.
                    {
                        Date date = DATE_FORMAT.parse(dateString);
                        String homeTeamName = nextResult.nextToken().trim();
                        int homeScore = Integer.parseInt(nextResult.nextToken().trim());
                        String awayTeamName = nextResult.nextToken().trim();
                        int awayScore = Integer.parseInt(nextResult.nextToken().trim());
                
                        int attendance = -1;
                        if (nextResult.hasMoreTokens())
                        {
                            attendance = Integer.parseInt(nextResult.nextToken().trim());
                        }
                       
                        Team homeTeam = teamMappings.get(homeTeamName);
                        if (homeTeam == null)
                        {
                            homeTeam = new Team(homeTeamName);
                            teamMappings.put(homeTeamName, homeTeam);
                        }
                        Team awayTeam = teamMappings.get(awayTeamName);
                        if (awayTeam == null)
                        {
                            awayTeam = new Team(awayTeamName);
                            teamMappings.put(awayTeamName, awayTeam);
                        }

                        if (!dates.isEmpty() && date.before(dates.last()))
                        {
                            System.out.println("ERROR: Results must be listed in chronological order.");
                            break;
                        }
                        dates.add(date);

                        Result result = new Result(homeTeam, awayTeam, homeScore, awayScore, attendance, date);
                    
                        // Update global records.
                        updateGlobalTotals(result);                    
                
                        // Add result to list for that day.
                        List<Result> results = resultsByDate.get(date);
                        if (results == null)
                        {
                            results = new LinkedList<Result>();
                            resultsByDate.put(date, results);
                        }
                        results.add(result);
                
                        matchCount++;
                    }
                    else if (dateString.charAt(0) == '#') 
                    {
                        // Lines beginning with a hash are comments and are ignored.
                    }
                    else if (dateString.equals(POINTS_TAG))
                    {
                        pointsForWin = Integer.parseInt(nextResult.nextToken());
                        pointsForDraw = Integer.parseInt(nextResult.nextToken());
                    }
                    else if (dateString.equals(PRIZE_TAG))
                    {
                        prizeZones.add(new LeagueZone(Integer.parseInt(nextResult.nextToken()),
                                                      Integer.parseInt(nextResult.nextToken()),
                                                      nextResult.nextToken()));
                    }
                    else if (dateString.equals(RELEGATION_TAG))
                    {
                        relegationZones.add(new LeagueZone(Integer.parseInt(nextResult.nextToken()),
                                                           Integer.parseInt(nextResult.nextToken()),
                                                           nextResult.nextToken()));
                    }
                    else // Points adjustment.
                    {
                        String teamName = nextResult.nextToken();
                        int amount = Integer.parseInt(nextResult.nextToken());
                        if (dateString.equals(DEDUCTED_TAG))
                        {
                            amount = -amount;
                        }
                        teamMappings.get(teamName).adjustPoints(amount);
                    }
                }
                nextLine = resultsFile.readLine();
            }
            System.out.println("Read " + matchCount + " results from " + resultsURL.toString());
            teams = generateTeamsArray();
            processTeamRecords();
            
            // Workout positions info.
            zones = new int[teams.length];
            prizeZoneNames = new String[prizeZones.size()];
            int index = 0;
            for (LeagueZone zone : prizeZones)
            {
                prizeZoneNames[index] = zone.name;
                for (int j = zone.startPos; j <= zone.endPos; j++)
                {
                    zones[j - 1] = index + 1; // Decrement to convert to zero-based index.
                }
                ++index;
            }
            relegationZoneNames = new String[relegationZones.size()];
            index = 0;
            for (LeagueZone zone : relegationZones)
            {
                relegationZoneNames[index] = zone.name;
                for (int j = zone.startPos; j <= zone.endPos; j++)
                {
                    zones[j - 1] = -(index + 1); // Decrement to convert to zero-based index.
                }
                ++index;
            }
        }
        catch(ParseException ex)
        {
            System.out.println("ERROR: Invalid date format in results file.");
        }
        catch(NoSuchElementException ex)
        {
            System.out.println("ERROR: Results file badly formatted.");
        }
        catch(IOException ex)
        {
            System.out.println("ERROR: Results file not found.");
        }
        finally
        {
            if (teams == null)
            {
                teams = new Team[0];
            }
        }
    }
    
    
    private Team[] generateTeamsArray()
    {
        Team[] teams = new Team[teamMappings.size()];
        int index = -1;
        for (Team team : teamMappings.values())
        {
            teams[++index] = team;
        }
        return teams;
    }
    
    
    private void processTeamRecords()
    {
        // Add result to the record of each team.
        Team[] table = null;
        for (Date date : dates)
        {
            List<Result> results = resultsByDate.get(date);
            // Add current date's results to individual team records.
            for (Result result : results)
            {
                result.homeTeam.addResult(result);
                result.awayTeam.addResult(result);
            }
            // Calculate table for current date.
            table = getStandardLeagueTable(Team.BOTH);
            for (int i = 0; i < table.length; i++)
            {
                table[i].addLeaguePosition(date, i + 1);
            }
            if (highestPointsTotal == 0) // Only set this for the most recent (first) date.
            {
                highestPointsTotal = getPoints(Team.BOTH, table[0], false);
            }
            System.out.println("Processed results for " + date.toString());
        }
    }
    
    
    private void updateGlobalTotals(Result result)
    {
        if (result.isDraw())
        {
            if (result.homeGoals == 0)
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
            if (result.isWin(result.homeTeam))
            {
                aggregateHomeWins++;
            }
            else
            {
                aggregateAwayWins++;
            }
            
            if (result.homeGoals == 0 || result.awayGoals == 0) // Can't both be zero otherwise it would be a draw.
            {
                aggregateCleansheets++;
            }
        }
        aggregateHomeGoals += result.homeGoals;
        aggregateAwayGoals += result.awayGoals;

        updateKeyResults(result);
        updateAttendances(result);
    }
    
    
    private void updateKeyResults(Result result)
    {
        Vector winsVector = null;
        if (result.isWin(result.homeTeam)) // Home Win
        {
            winsVector = biggestHomeWins;
        }
        else if (result.isWin(result.awayTeam)) // Away Win
        {
            winsVector = biggestAwayWins;
        }
        if (winsVector != null)
        {
            int index = winsVector.size();
            while (index > 0 && result.getMarginOfVictory() > ((Result) winsVector.elementAt(index - 1)).getMarginOfVictory())
            {
                index--;
            }
            if (index < 5)
            {
                winsVector.insertElementAt(result, index);
                winsVector.setSize(Math.min(winsVector.size(), 4));
            }
        }
        
        int index = highestMatchAggregates.size();
        while (index > 0 && result.getMatchAggregate() > ((Result) highestMatchAggregates.elementAt(index - 1)).getMatchAggregate())
        {
            index--;
        }
        if (index < 5)
        {
            highestMatchAggregates.insertElementAt(result, index);
            highestMatchAggregates.setSize(Math.min(highestMatchAggregates.size(), 4));
        }
    }
    
    
    private void updateAttendances(Result result)
    {
        if (result.attendance >= 0)
        {
            aggregateAttendance += result.attendance;
            int index = topAttendances.size();
            while (index > 0 && result.attendance > ((Result) topAttendances.elementAt(index - 1)).attendance)
            {
                index--;
            }
            if (index < 20)
            {
                topAttendances.insertElementAt(result, index);
                topAttendances.setSize(Math.min(topAttendances.size(), 20));
            }
            index = bottomAttendances.size();
            while (index > 0 && result.attendance < ((Result) bottomAttendances.elementAt(index - 1)).attendance)
            {
                index--;
            }
            if (index < 20)
            {
                bottomAttendances.insertElementAt(result, index);
                bottomAttendances.setSize(Math.min(bottomAttendances.size(), 20));
            }
        }
    }
    
    
    /**
     * Get a list of the names of all the teams in the division, sorted in alphabetical order.
     */
    public String[] getTeamNames()
    {
        if (teamNames == null)
        {
            Team[] alphabeticalTeams = sortTeams(teams, new TeamComparator()
            {
                public int compareTeams(Team team1, Team team2)
                {
                    return team1.getName().toLowerCase().compareTo(team2.getName().toLowerCase());
                }
            });
            teamNames = new String[alphabeticalTeams.length];
            for (int i = 0; i < alphabeticalTeams.length; i++)
            {
                teamNames[i] = alphabeticalTeams[i].getName();
            }
        }
        return teamNames;
    }
    
    
    /**
     * @return The Team object for a particular team name.
     */
    public Team getTeam(String teamName)
    {
        return teamMappings.get(teamName);
    }
    
    
    /**
     * @return An array of dates on which matches took place, most recent first.
     */
    public Date[] getDates()
    {
        Date[] datesArray = new Date[dates.size()];
        return dates.toArray(datesArray);
    }
    
    
    public Date getMostRecentDate()
    {
        return dates.last();
    }
    
    
    public String[] getPrizeZoneNames()
    {
        return prizeZoneNames;
    }
    
    
    public String[] getRelegationZoneNames()
    {
        return relegationZoneNames;
    }
    
    
    /**
     * @return An array of results for a particular date.
     */
    public Result[] getResults(Date date)
    {
        List<Result> results = resultsByDate.get(date);
        Result[] resultsArray = new Result[results.size()];
        return results.toArray(resultsArray);
    }
    
    
    /**
     * Sorts the teams into standard league table order (in order of points won).
     */
    public Team[] getStandardLeagueTable(int where)
    {
        standardComparator.setForm(false);
        standardComparator.setWhere(where);
        return sortTeams(teams, standardComparator);
    }
    
    
    /**
     * Sorts the teams in order of average points won per game.
     */
    public Team[] getAverageLeagueTable(int where)
    {
        averagesComparator.setWhere(where);
        return sortTeams(teams, averagesComparator);
    }
    
    
    /**
     * Sorts the teams in order of fewest points lost.
     */
    public Team[] getInvertedLeagueTable(int where)
    {
        invertedComparator.setWhere(where);
        return sortTeams(teams, invertedComparator);
    }
    
    
    public Team[] getFormTable(int where)
    {
        standardComparator.setForm(true);
        standardComparator.setWhere(where);
        return sortTeams(teams, standardComparator);
    }
    
    
    public Team[] getSequenceTable(int when, int where, int sequence)
    {
        sequenceComparator.configure(when, where, sequence);
        return sortTeams(teams, sequenceComparator);
    }
    
    
    public Team[] getAttendanceTable(int type)
    {
        attendanceComparator.setType(type);
        return sortTeams(teams, attendanceComparator);
    }
    
    
    public Result[] getBiggestHomeWins()
    {
        return vectorToResultsArray(biggestHomeWins);
    }


    public Result[] getBiggestAwayWins()
    {
        return vectorToResultsArray(biggestAwayWins);
    }
    
    
    public Result[] getHighestMatchAggregates()
    {
        return vectorToResultsArray(highestMatchAggregates);
    }

    
    public Result[] getHighestAttendances()
    {
        return vectorToResultsArray(topAttendances);
    }
    
    
    public Result[] getLowestAttendances()
    {
        return vectorToResultsArray(bottomAttendances);
    }
    
    
    /**
     * Helper method to convert a vector of results into an array.
     */
    private Result[] vectorToResultsArray(Vector vector)
    {
        Result[] results = new Result[vector.size()];
        vector.copyInto(results);
        return results;
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
    
    
    public int getPoints(int where, Team team, boolean form)
    {
        int points = team.getAggregate(where, Team.AGGREGATE_WON, form) * pointsForWin
                + team.getAggregate(where, Team.AGGREGATE_DRAWN, form) * pointsForDraw;
        if (!form)
        {
            points += team.getPointsAdjustment(where);
        }
        return points;
    }
    
    
    public double getAveragePoints(int where, Team team)
    {
        return (double) getPoints(where, team, false) / team.getAggregate(where, Team.AGGREGATE_PLAYED, false);
    }

    
    public int getPointsDropped(int where, Team team)
    {
        return team.getAggregate(where, Team.AGGREGATE_PLAYED, false) * pointsForWin - getPoints(where, team, false);
    }
    
    
    /**
     * @return A zone ID for a particular league position.  Positive zone IDs represent
     * prize zones (promotion, play-offs etc.) and negative IDs represent relegation zones.
     */
    public int getZoneForPosition(int position)
    {
        return zones[--position]; // Decrement to convert to zero-based index.
    }
    
    
    public int getHighestPointsTotal()
    {
        return highestPointsTotal;
    }

    
    /**
     * Perform a sort on an array of teams using the specified comparator.
     */
    private Team[] sortTeams(Team[] teams, TeamComparator comparator)
    {
        Team[] copy = (Team[]) teams.clone();
        mergeSort(teams, copy, 0, teams.length, comparator);
        return copy;
    }


    /**
     * Merge sort based on the implementation in java.util.Arrays in J2SE version 1.2 and later.
     * One thing that is not made clear in the documentation for that implementation is that it
     * makes the assumption that the contents of both src and dest are the same to start with.
     */
    private void mergeSort(Team[] src,
                           Team[] dest,
                           int low,
                           int high,
                           TeamComparator comparator)
    {
	int length = high - low;

	// Use insertion sort for small sorts, it's quicker.
	if (length < 7)
        {
	    for (int i = low; i < high; i++)
            {
		for (int j = i; j > low && comparator.compareTeams(dest[j - 1], dest[j]) > 0; j--)
                {
		    swap(dest, j, j - 1);
                }
            }
	    return;
	}

        // Recursively sort halves of dest into src.
        int mid = (low + high) >> 1;
        mergeSort(dest, src, low, mid, comparator);
        mergeSort(dest, src, mid, high, comparator);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimisation that results in faster sorts for nearly ordered lists.
        if (comparator.compareTeams(src[mid - 1], src[mid]) <= 0)
        {
           System.arraycopy(src, low, dest, low, length);
           return;
        }

        // Merge sorted halves (now in src) into dest
        for(int i = low, p = low, q = mid; i < high; i++)
        {
            if (q >= high || p < mid && comparator.compareTeams(src[p], src[q]) <= 0)
            {
                dest[i] = src[p++];
            }
            else
            {
                dest[i] = src[q++];
            }
        }
    }
    
    
    /**
     * Swaps teams[a] with teams[b].
     */
    private void swap(Team[] teams, int a, int b)
    {
        Team team = teams[a];
        teams[a] = teams[b];
        teams[b] = team;
    }


    public int getPointsForWin()
    {
        return pointsForWin;
    }


    public int getPointsForDraw()
    {
        return pointsForDraw;
    }
}