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

import net.footballpredictions.footballstats.model.Result;
import net.footballpredictions.footballstats.model.LeagueMetaData;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Interface implemented by objects that provide access to league data.
 * @author Daniel Dyer
 */
public interface LeagueDataProvider
{
    /**
     * @return A set of the names of all of the teams in the league.
     */
    SortedSet<String> getTeams();

    /**
     * @return A list of the league results for the season.
     */
    List<Result> getResults();

    /**
     * @return Any points adjustments (awarded or deducted) for any teams in the league.
     */
    Map<String, Integer> getPointsAdjustments();

    /**
     * @return League metadata (how points for a win, how many teams in the league,
     * promotion/relegation details, etc.)
     */
    LeagueMetaData getLeagueMetaData();
}
