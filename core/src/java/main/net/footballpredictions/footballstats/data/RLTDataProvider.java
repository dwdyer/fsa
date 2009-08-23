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
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import net.footballpredictions.footballstats.model.LeagueMetaData;
import net.footballpredictions.footballstats.model.Result;

/**
 * {@link LeagueDataProvider} implementation that reads the RLT file format used by
 * FSA version 2.
 * @author Daniel Dyer
 */
public class RLTDataProvider implements LeagueDataProvider
{
    /**
     * Replaced by {@link #RULES_TAG}.
     */
    @Deprecated
    private static final String POINTS_TAG = "POINTS";

    private static final String RULES_TAG = "RULES";
    private static final String PRIZE_TAG = "PRIZE";
    private static final String RELEGATION_TAG = "RELEGATION";
    private static final String AWARDED_TAG = "AWARDED";
    private static final String DEDUCTED_TAG = "DEDUCTED";
    private static final String MINILEAGUE_TAG = "MINILEAGUE";

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    private final LeagueMetaData metaData;

    private final SortedSet<String> teams = new TreeSet<String>();
    private final List<Result> results = new LinkedList<Result>();
    private final Map<String, Integer> pointsAdjustments = new HashMap<String, Integer>();
    private final List<LeagueMetaData.LeagueZone> prizeZones = new LinkedList<LeagueMetaData.LeagueZone>();
    private final List<LeagueMetaData.LeagueZone> relegationZones = new LinkedList<LeagueMetaData.LeagueZone>();


    public RLTDataProvider(InputStream data) throws IOException
    {
        BufferedReader resultsReader = new BufferedReader(new InputStreamReader(data, "UTF-8"));
        try
        {
            int pointsForWin = 3;
            int pointsForDraw = 1;
            int split = 0;

            String nextLine = resultsReader.readLine();
            while (nextLine != null)
            {
                nextLine = nextLine.trim();
                // Lines beginning with a hash are comments and are ignored.  Blank lines are also ignored.
                if (nextLine.length() > 0 && nextLine.charAt(0) != '#')
                {
                    StringTokenizer tokens = new StringTokenizer(nextLine, "|");

                    String tag = tokens.nextToken();
                    if (tag.equals(RULES_TAG) || tag.equals(POINTS_TAG)) // Data-file over-rides default league rules.
                    {
                        pointsForWin = Integer.parseInt(tokens.nextToken());
                        pointsForDraw = Integer.parseInt(tokens.nextToken());
                        if (tokens.hasMoreTokens())
                        {
                            split = Integer.parseInt(tokens.nextToken());
                        }
                    }
                    else if (tag.equals(PRIZE_TAG))
                    {
                        prizeZones.add(new LeagueMetaData.LeagueZone(Integer.parseInt(tokens.nextToken()),
                                                                     Integer.parseInt(tokens.nextToken()),
                                                                     tokens.nextToken()));
                    }
                    else if (tag.equals(RELEGATION_TAG))
                    {
                        relegationZones.add(new LeagueMetaData.LeagueZone(Integer.parseInt(tokens.nextToken()),
                                                                          Integer.parseInt(tokens.nextToken()),
                                                                          tokens.nextToken()));
                    }
                    else if (tag.equals(DEDUCTED_TAG)) // Points adjustment.
                    {
                        processPointsAdjustment(tokens.nextToken(),
                                                -Integer.parseInt(tokens.nextToken()));
                    }
                    else if (tag.equals(AWARDED_TAG))
                    {
                        processPointsAdjustment(tokens.nextToken(),
                                                Integer.parseInt(tokens.nextToken()));
                    }
                    else if (!tag.equals(MINILEAGUE_TAG)) // Mini-leagues currently implemented by Anorak but not FSA.
                    {
                        // Process as a result (first char should be a number).
                        Date date = DATE_FORMAT.parse(tag);
                        String homeTeamName = tokens.nextToken().trim().intern();
                        int homeScore = Integer.parseInt(tokens.nextToken().trim());
                        String awayTeamName = tokens.nextToken().trim().intern();
                        int awayScore = Integer.parseInt(tokens.nextToken().trim());

                        int attendance = tokens.hasMoreTokens() ? Integer.parseInt(tokens.nextToken().trim()) : -1;

                        Result result = new Result(homeTeamName, awayTeamName, homeScore, awayScore, attendance, date);
                        teams.add(homeTeamName);
                        teams.add(awayTeamName);
                        results.add(result);
                    }
                }
                nextLine = resultsReader.readLine();
            }
            System.out.println("Read " + results.size() + " results.");

            this.metaData = new LeagueMetaData(pointsForWin,
                                               pointsForDraw,
                                               split,
                                               teams.size(),
                                               prizeZones,
                                               relegationZones);
        }
        catch(ParseException ex)
        {
            ex.printStackTrace();
            throw new IOException("Invalid date format in results file.");
        }
        finally
        {
            resultsReader.close();
        }
    }


    private void processPointsAdjustment(String team, int amount)
    {
        Integer value = pointsAdjustments.get(team);
        pointsAdjustments.put(team, value == null ? amount : value + amount);
    }


    /**
     * {@inheritDoc}
     */
    public SortedSet<String> getTeams()
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
    public LeagueMetaData getLeagueMetaData()
    {
        return metaData;
    }
}
