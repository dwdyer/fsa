// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   � Copyright 2000-2008 Daniel W. Dyer
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

/**
 * @author Daniel Dyer
 */
public interface TeamRecord
{
    /**
     * @return The name of the team.
     */
    String getName();

    /**
     * @return The team that this record relates to.
     */
    Team getTeam();

    /**
     * Adds a result to the list of results that make up this record.
     * @param result The result to add to the record.
     */
    void addResult(Result result);

    /**
     * Returns the total number of matches stored in this record.
     * @return The number of games played.
     */
    int getPlayed();

    /**
     * Returns the number of matches in this record that were won by the
     * team that this record relates to.
     * @return The number of wins.
     */
    int getWon();

    /**
     * Returns the number of matches in this record that were drawn.
     * @return The number of draws.
     */
    int getDrawn();

    /**
     * Returns the number of matches in this record that were lost by the
     * team that this record relates to.
     * @return The number of defeats.
     */
    int getLost();

    /**
     * For all the results in this record, returns the total number of goals
     * scored by the team that this record relates to.
     * @return The number of goals scored.
     */
    int getScored();

    /**
     * For all the results in this record, returns the total number of goals
     * conceded by the team that this record relates to.
     * @return The number of goals conceded.
     */
    int getConceded();

    /**
     * @return The difference between the number of goals scored by this team and the number
     * conceded.  A positive value indicates more goals scored than conceded, a negative value
     * indicates more conceded than scored.
     */
    int getGoalDifference();

    /**
     * @return The total number of points earned for these results.
     */
    int getPoints();

    /**
     * @return The total number of points that have been dropped by the team
     * in these matches (this is the total number of available points minus the
     * number of points actually achieved).
     */
    int getDroppedPoints();

    /**
     * @return The average number of points earned per game.
     */
    double getAveragePoints();

    /**
     * @return A String representation of this team's current form as determined
     * by the results that make up this record.
     */
    String getForm();

    /**
     * Used to apply points adjustments (positive or negative) made by the league
     * administrators for rules infringements.
     * @param amount A positive number means points have been awarded, a negative value
     * means points have been deducted.
     */
    void adjustPoints(int amount);
        
}
