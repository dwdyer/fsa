// $Header: $
package net.footballpredictions.footballstats.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.HashSet;
import net.footballpredictions.footballstats.util.FixedSizeSortedSet;

/**
 * Models a single season in a particular football league.
 * @author Daniel Dyer
 * @since 24/12/2003
 * @version $Revision: $
 */
public final class LeagueSeason
{
    private int pointsForWin = 3;
    private int pointsForDraw = 1;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    private static final String POINTS_TAG = "POINTS";
    private static final String PRIZE_TAG = "PRIZE";
    private static final String RELEGATION_TAG = "RELEGATION";
    private static final String AWARDED_TAG = "AWARDED";
    private static final String DEDUCTED_TAG = "DEDUCTED";
    
    private final Map<String, Team> teamMappings = new HashMap<String, Team>();
    private Set<Team> teams;
    private SortedSet<String> teamNames;
    private final SortedSet<Date> dates = new TreeSet<Date>(Collections.reverseOrder()); // Most recent first.
    private final Map<Date, List<Result>> resultsByDate = new HashMap<Date, List<Result>>();

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
    
    private int[] zones;
    private String[] prizeZoneNames;
    private String[] relegationZoneNames;
    
    private int highestPointsTotal = 0;
 
    private final SequenceComparator sequenceComparator = new SequenceComparator();
    private final TeamAttendanceComparator attendanceComparator = new TeamAttendanceComparator();
    
    
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
            teams = new HashSet<Team>(teamMappings.values());
            processTeamRecords();
            
            // Workout positions info.
            zones = new int[teams.size()];
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
                teams = Collections.emptySet();
            }
        }
    }


    private void processTeamRecords()
    {
        // Add result to the record of each team.
        for (Date date : dates)
        {
            List<Result> results = resultsByDate.get(date);
            // Add current date's results to individual team records.
            for (Result result : results)
            {
                result.getHomeTeam().addResult(result);
                result.getAwayTeam().addResult(result);
            }
            // Calculate table for current date.
            SortedSet<Team> table = getStandardLeagueTable(Team.BOTH);
            int index = 1;
            for (Team team : table)
            {
                team.addLeaguePosition(date, index);
                ++index;
            }
            if (highestPointsTotal == 0) // Only set this for the most recent (first) date.
            {
                highestPointsTotal = getPoints(Team.BOTH, table.first(), false);
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
     */
    public SortedSet<String> getTeamNames()
    {
        if (teamNames == null)
        {
            teamNames = new TreeSet<String>();
            for (Team team : teams)
            {
                teamNames.add(team.getName());
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
     * @return An ordered collection of dates on which matches took place, most recent first.
     */
    public SortedSet<Date> getDates()
    {
        return dates;
    }
    
    
    public Date getMostRecentDate()
    {
        return dates.first();
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
    public List<Result> getResults(Date date)
    {
        return resultsByDate.get(date);
    }
    
    
    /**
     * Sorts the teams into standard league table order (in order of points won).
     */
    public SortedSet<Team> getStandardLeagueTable(int where)
    {
        return sortTeams(teams, new LeagueTableComparator(where, pointsForWin, pointsForDraw));
    }
    
    
    /**
     * Sorts the teams in order of average points won per game.
     */
    public SortedSet<Team> getAverageLeagueTable(int where)
    {
        return sortTeams(teams, new PointsPerGameComparator(where, pointsForWin, pointsForDraw));
    }
    
    
    /**
     * Sorts the teams in order of fewest points lost.
     */
    public SortedSet<Team> getInvertedLeagueTable(int where)
    {
        return sortTeams(teams, new DroppedPointsComparator(where, pointsForWin, pointsForDraw));
    }
    
    
    public SortedSet<Team> getFormTable(int where)
    {
        return sortTeams(teams, new FormTableComparator(where, pointsForWin, pointsForDraw));
    }
    
    
    public SortedSet<Team> getSequenceTable(int when, int where, int sequence)
    {
        sequenceComparator.configure(when, where, sequence);
        return sortTeams(teams, sequenceComparator);
    }
    
    
    public SortedSet<Team> getAttendanceTable(int type)
    {
        attendanceComparator.setType(type);
        return sortTeams(teams, attendanceComparator);
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
    private SortedSet<Team> sortTeams(Set<Team> teams, Comparator<Team> comparator)
    {
        SortedSet<Team> sortedTeams = new TreeSet<Team>(comparator);
        sortedTeams.addAll(teams);
        return sortedTeams;
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