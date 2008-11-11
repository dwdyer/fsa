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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;
import net.footballpredictions.footballstats.data.LeagueDataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test for the {@link LeagueSeason} class.
 * @author Daniel Dyer
 */
public class LeagueSeasonTest
{
    private static final int ONE_DAY = 86400000;

    private List<Result> results;

    @BeforeClass
    public void createLeagueData()
    {
        Date today = new Date();
        // After these 4 results, A should be top on 6 points, D bottom with zero and
        // B and C have three points each.
        // In a normal league, B will be second because of superior goal difference.
        // In a split league (split after 1 match) C will be second even though B has
        // the better goal difference.
        results = Arrays.asList(new Result("A", "B", 1, 0, -1, today),
                                new Result("C", "D", 1, 0, -1, today),
                                new Result("A", "C", 1, 0, -1, new Date(today.getTime() + ONE_DAY)),
                                new Result("B", "D", 2, 0, -1, new Date(today.getTime() + ONE_DAY)));
    }


    /**
     * First test the sensible way of doing things (no split).
     */
    @Test
    public void testUnsplitLeague()
    {
        LeagueMetaData metaData = new LeagueMetaData(3, // Points for a win.
                                                     1, // Points for a draw.
                                                     0, // No split.
                                                     4, // 4 teams in the league.
                                                     Collections.<LeagueMetaData.LeagueZone>emptyList(),
                                                     Collections.<LeagueMetaData.LeagueZone>emptyList());
        LeagueSeason season = new LeagueSeason(new TestDataProvider(results,
                                                                    metaData));

        Set<StandardRecord> table = season.getStandardLeagueTable(VenueType.BOTH);
        Iterator<StandardRecord> iterator = table.iterator();

        assert iterator.next().getName().equals("A") : "Top team should be A.";
        assert iterator.next().getName().equals("B") : "Second team should be B.";
        assert iterator.next().getName().equals("C") : "Third team should be C.";
        assert iterator.next().getName().equals("D") : "Bottom team should be D.";
    }


    /**
     * Then test the Scottish way of doing things.  The ordering should be different with
     * a split after 1 game.
     */
    @Test(dependsOnMethods = "testUnsplitLeague")
    public void testSplitLeague()
    {
        LeagueMetaData metaData = new LeagueMetaData(3, // Points for a win.
                                                     1, // Points for a draw.
                                                     1, // Split after 1 game.
                                                     4, // 4 teams in the league.
                                                     Collections.<LeagueMetaData.LeagueZone>emptyList(),
                                                     Collections.<LeagueMetaData.LeagueZone>emptyList());
        LeagueSeason season = new LeagueSeason(new TestDataProvider(results,
                                                                    metaData));

        Set<StandardRecord> table = season.getStandardLeagueTable(VenueType.BOTH);
        Iterator<StandardRecord> iterator = table.iterator();

        assert iterator.next().getName().equals("A") : "Top team should be A.";
        assert iterator.next().getName().equals("C") : "Second team should be C.";
        assert iterator.next().getName().equals("B") : "Third team should be B.";
        assert iterator.next().getName().equals("D") : "Bottom team should be D.";
    }


    private static class TestDataProvider implements LeagueDataProvider
    {
        private final List<Result> results;
        private final LeagueMetaData metaData;

        public TestDataProvider(List<Result> results, LeagueMetaData metaData)
        {
            this.results = results;
            this.metaData = metaData;
        }


        public SortedSet<String> getTeams()
        {
            SortedSet<String> teams = new TreeSet<String>();
            for (Result result : results)
            {
                teams.add(result.getHomeTeam());
                teams.add(result.getAwayTeam());
            }
            return teams;
        }


        public List<Result> getResults()
        {
            return results;
        }


        public Map<String, Integer> getPointsAdjustments()
        {
            return Collections.emptyMap();
        }


        public LeagueMetaData getLeagueMetaData()
        {
            return metaData;
        }
    }
}
