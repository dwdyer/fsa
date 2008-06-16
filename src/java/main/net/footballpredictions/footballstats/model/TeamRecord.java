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

/**
 * @author Daniel Dyer
 */
public interface TeamRecord
{
    /**
     * @return The name of the team.
     */
    String getName();

    void addResult(Result result);

    int getPlayed();

    int getWon();

    int getDrawn();

    int getLost();

    int getScored();

    int getConceded();

    /**
     * @return The difference between the number of goals scored by this team and the number
     * conceded.  A positive value indicates more goals scored than conceded, a negative value
     * indicates more conceded than scored.
     */
    int getGoalDifference();

    int getPoints();

    /**
     * @return The total number of points that have been dropped by the team
     * (this is the total number of available points minus the number of points
     * actually achieved).
     */
    int getDroppedPoints();

    /**
     * @return The average number of points earned per game.
     */
    double getAveragePoints();

    /**
     * @return A String representation of this teams current form.  Either home form,
     * away form or combined form depending on the method argument.
     */
    String getForm();

    Team getTeam();

    /**
     * Used to apply points adjustments (positive or negative) made by the league
     * administrators for rules infringements.
     * @param amount A positive number means points have been awarded, a negative value
     * means points have been deducted.
     */
    void adjustPoints(int amount);
}
