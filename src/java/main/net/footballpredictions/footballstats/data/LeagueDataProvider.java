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
import net.footballpredictions.footballstats.model.LeagueSeason;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface implemented by objects that provide access to league data.
 * @author Daniel Dyer
 */
public interface LeagueDataProvider
{
    /**
     * @return A set of the names of all of the teams in the league.
     */
    Set<String> getTeams();

    /**
     * @return A list of the league results for the season.
     */
    List<Result> getResults();

    /**
     * @return Any points adjustments (awarded or deducted) for any teams in the league.
     */
    Map<String, Integer> getPointsAdjustments();

    /**
     * @return Details of the prizes awarded to the top teams.
     */
    List<LeagueSeason.LeagueZone> getPrizeZones();

    /**
     * @return Details of which teams will be relegated at the end of the season. 
     */
    List<LeagueSeason.LeagueZone> getRelegationZones();

    /**
     * @return The number of points earned for winning a match (usually 3, but can be 2).
     */
    int getPointsForWin();

    /**
     * @return The number of points earned for drawing a match (almost always 1).
     */
    int getPointsForDraw();
}
