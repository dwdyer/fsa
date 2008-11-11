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
 * A {@link TeamRecord} implementation that is used to determine how teams are
 * split in a league such as the Scottish Premier League or Northern Ireland
 * Premiership.  In these competitions, the league is split in half for the
 * final games of the season.
 * @author Daniel Dyer
 */
class SplitRecord extends AbstractTeamRecord
{
    private final int split;

    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int scored;
    private int conceded;

    /**
     * Creates a {@link TeamRecord} that is used to order teams for splitting after
     * a predetermined number of games.
     * @param team The team that this record applies to.
     * @param pointsForWin The number of points awarded for a win.
     * @param pointsForDraw The number of points awarded for a draw.
     * @param split How many games are played before the league is split.  Results
     * after this point are ignored by this {@link TeamRecord} implementation.
     */
    public SplitRecord(Team team,
                       int pointsForWin,
                       int pointsForDraw,
                       int split)
    {
        super(team, pointsForWin, pointsForDraw);
        this.split = split;
    }


    /**
     * {@inheritDoc}
     */
    public void addResult(Result result)
    {
        if (played < split)
        {
            ++played;
            scored += result.getGoalsFor(getName());
            conceded += result.getGoalsAgainst(getName());
            if (result.isDraw())
            {
                ++drawn;
            }
            else if (result.isWin(getName()))
            {
                ++won;
            }
            else
            {
                ++lost;
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public int getPlayed()
    {
        return played;
    }


    /**
     * {@inheritDoc}
     */
    public int getWon()
    {
        return won;
    }


    /**
     * {@inheritDoc}
     */
    public int getDrawn()
    {
        return drawn;
    }


    /**
     * {@inheritDoc}
     */
    public int getLost()
    {
        return lost;
    }


    /**
     * {@inheritDoc}
     */
    public int getScored()
    {
        return scored;
    }


    /**
     * {@inheritDoc}
     */
    public int getConceded()
    {
        return conceded;
    }


    /**
     * @throws UnsupportedOperationException Not supported for this record type.
     */
    public String getForm()
    {
        throw new UnsupportedOperationException("Form is not calculated for split record.");
    }
}
