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

import java.util.List;

/**
 * Information about a particular league.  Includes details of any deviations from
 * standard rules and any prizes/relegation.
 * @author Daniel Dyer
 */
public class LeagueMetaData
{
    private final int pointsForWin;
    private final int pointsForDraw;
    private final int split;

    private final int[] zones;
    private final String[] prizeZoneNames;
    private final String[] relegationZoneNames;


    public LeagueMetaData(int pointsForWin,
                          int pointsForDraw,
                          int split,
                          int numberOfTeams,
                          List<LeagueZone> prizeZones,
                          List<LeagueZone> relegationZones)
    {
        this.pointsForWin = pointsForWin;
        this.pointsForDraw = pointsForDraw;
        this.split = split;

        // Workout positions info.
        zones = new int[numberOfTeams];
        prizeZoneNames = new String[prizeZones.size()];
        int index = 0;
        for (LeagueMetaData.LeagueZone zone : prizeZones)
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
        for (LeagueMetaData.LeagueZone zone : relegationZones)
        {
            relegationZoneNames[index] = zone.name;
            for (int j = zone.startPos; j <= zone.endPos; j++)
            {
                zones[j - 1] = -(index + 1); // Decrement to convert to zero-based index.
            }
            ++index;
        }
    }


    public int getPointsForWin()
    {
        return pointsForWin;
    }


    public int getPointsForDraw()
    {
        return pointsForDraw;
    }


    /**
     * Certain leagues (e.g. the Scottish Premier League and the IFA Premiership in
     * Northern Ireland) have an unconvential format that involves the league splitting
     * into two sections after a certain number of matches.  Further matches are played
     * after the split but bottom half teams cannot finish above top half teams even if
     * they eventually accumulate more points.
     *
     * For example, in the SPL, after 33 games (each of the 12 teams plays each of the
     * others 3 times), the league splits into a top half and bottom half of 6 teams
     * each.  Within these sub-leagues, each team plays each other once more (5 games
     * in total per club).  If a team makes the cut for the top section, they are
     * guaranteed to finish no lower than sixth regardless of the results of the final
     * 5 matches.  Likewise, if a team is in the bottom 6 after 33 matches they cannot
     * finish higher than seventh, regardless of how many points they accumulate.
     *
     * This method returns the number of matches that are played before the league splits.
     * Or if, as in the case of most leagues, there is no split, the method returns zero.  
     *
     * @return The number of matches that are played before the league splits, or zero
     * if this league does not split.
     */
    public int getSplit()
    {
        return split;
    }

    
    /**
     * @param position A league position, where 1 is the highest position.
     * @return A zone ID for a particular league position.  Positive zone IDs represent
     * prize zones (promotion, play-offs etc.) and negative IDs represent relegation zones.
     */
    public int getZoneForPosition(int position)
    {
        return zones[position - 1]; // Decrement to convert to zero-based index.
    }


    public String[] getPrizeZoneNames()
    {
        return prizeZoneNames;
    }


    public String[] getRelegationZoneNames()
    {
        return relegationZoneNames;
    }
       


    // Class for temporarily storing league position data.
    public static final class LeagueZone
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
}
