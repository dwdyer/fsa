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
 * @author Daniel Dyer
 */
public class LeagueMetaData
{
    private final int pointsForWin;
    private final int pointsForDraw;

    private final int[] zones;
    private final String[] prizeZoneNames;
    private final String[] relegationZoneNames;


    public LeagueMetaData(int pointsForWin,
                          int pointsForDraw,
                          int numberOfTeams,
                          List<LeagueZone> prizeZones,
                          List<LeagueZone> relegationZones)
    {
        this.pointsForWin = pointsForWin;
        this.pointsForDraw = pointsForDraw;

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
