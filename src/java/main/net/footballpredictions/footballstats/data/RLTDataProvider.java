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
package net.footballpredictions.footballstats.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.Result;

/**
 * {@link LeagueDataProvider} implementation that reads the RLT file format used by
 * FSA version 2.
 * @author Daniel Dyer
 */
public class RLTDataProvider implements LeagueDataProvider
{
    private static final String POINTS_TAG = "POINTS";
    private static final String PRIZE_TAG = "PRIZE";
    private static final String RELEGATION_TAG = "RELEGATION";
    private static final String AWARDED_TAG = "AWARDED";
    private static final String DEDUCTED_TAG = "DEDUCTED";

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    private int pointsForWin = 3;
    private int pointsForDraw = 1;

    private final Set<String> teams = new TreeSet<String>();
    private final List<Result> results = new LinkedList<Result>();
    private final Map<String, Integer> pointsAdjustments = new HashMap<String, Integer>();
    private final List<LeagueSeason.LeagueZone> prizeZones = new LinkedList<LeagueSeason.LeagueZone>();
    private final List<LeagueSeason.LeagueZone> relegationZones = new LinkedList<LeagueSeason.LeagueZone>();


    public RLTDataProvider(InputStream data) throws IOException
    {
        BufferedReader resultsReader = new BufferedReader(new InputStreamReader(data, "UTF-8"));
        try
        {
            String nextLine = resultsReader.readLine();
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


                        Result result = new Result(homeTeamName, awayTeamName, homeScore, awayScore, attendance, date);
                        teams.add(homeTeamName);
                        teams.add(awayTeamName);
                        results.add(result);

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
                        prizeZones.add(new LeagueSeason.LeagueZone(Integer.parseInt(nextResult.nextToken()),
                                                                   Integer.parseInt(nextResult.nextToken()),
                                                                   nextResult.nextToken()));
                    }
                    else if (dateString.equals(RELEGATION_TAG))
                    {
                        relegationZones.add(new LeagueSeason.LeagueZone(Integer.parseInt(nextResult.nextToken()),
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

                        Integer value = pointsAdjustments.get(teamName);
                        if (value == null)
                        {
                            value = 0;
                        }

                        pointsAdjustments.put(teamName, value + amount);
                    }
                }
                nextLine = resultsReader.readLine();
            }
            System.out.println("Read " + results.size() + " results.");
        }
        catch(ParseException ex)
        {
            System.out.println("ERROR: Invalid date format in results file.");
        }
        catch(NoSuchElementException ex)
        {
            System.out.println("ERROR: Results file badly formatted.");
        }
        finally
        {
            resultsReader.close();
        }
    }


    /**
     * {@inheritDoc}
     */
    public Set<String> getTeams()
    {
        return teams;
    }


    /**
     * {@inheritDoc}
     */
    public List<Result> getResults()
    {
        return results;
    }


    /**
     * {@inheritDoc}
     */
    public Map<String, Integer> getPointsAdjustments()
    {
        return pointsAdjustments;
    }


    /**
     * {@inheritDoc}
     */
    public List<LeagueSeason.LeagueZone> getPrizeZones()
    {
        return prizeZones;
    }


    /**
     * {@inheritDoc}
     */
    public List<LeagueSeason.LeagueZone> getRelegationZones()
    {
        return relegationZones;
    }


    /**
     * {@inheritDoc}
     */
    public int getPointsForWin()
    {
        return pointsForWin;
    }


    /**
     * {@inheritDoc}
     */
    public int getPointsForDraw()
    {
        return pointsForDraw;
    }
}
