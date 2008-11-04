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
 * Base class for {@link TeamRecord} implementations.
 * @author Daniel Dyer
 */
public abstract class AbstractTeamRecord implements TeamRecord
{
    private final int pointsForWin;
    private final int pointsForDraw;

    private final Team team;
    private int pointsAdjustment = 0;


    protected AbstractTeamRecord(Team team, int pointsForWin, int pointsForDraw)
    {
        this.team = team;
        this.pointsForWin = pointsForWin;
        this.pointsForDraw = pointsForDraw;
    }


    /**
     * {@inheritDoc}
     */
    public Team getTeam()
    {
        return team;
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return team.getName();
    }


    /**
     * {@inheritDoc}
     */
    public int getGoalDifference()
    {
        return getScored() - getConceded();
    }


    /**
     * {@inheritDoc}
     */
    public int getPoints()
    {
        return (getWon() * pointsForWin) + (getDrawn() * pointsForDraw) + pointsAdjustment;
    }


    /**
     * {@inheritDoc}
     */
    public int getDroppedPoints()
    {
        return getPlayed() * pointsForWin - getPoints();
    }


    /**
     * {@inheritDoc}
     */
    public double getAveragePoints()
    {
        return getPlayed() == 0 ? 0 : (double) getPoints() / (double) getPlayed();
    }
    

    /**
     * {@inheritDoc}
     */
    public void adjustPoints(int amount)
    {
        pointsAdjustment += amount;
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

    
    /**
     * Over-ride equals.  Records are equal if the teams are the same.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof AbstractTeamRecord)
        {
            AbstractTeamRecord other = (AbstractTeamRecord) obj;
            return team.equals(other.getTeam());
        }
        return false;
    }


    /**
     * Over-ride hashCode because equals has also been over-ridden, to satisfy general contract
     * of equals.
     * Algorithm from Effective Java by Joshua Bloch.
     */
    @Override
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + team.hashCode();
        return result;
    }
}
