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
package net.footballpredictions.footballstats.swing;

import javax.swing.table.AbstractTableModel;
import net.footballpredictions.footballstats.model.TeamRecord;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

/**
 * {@link javax.swing.table.TableModel} implementation for displaying a league table
 * in a {@link javax.swing.JTable} component.
 * @author Daniel Dyer
 */
class LeagueTableModel extends AbstractTableModel
{
    static final int POSITION_COLUMN = 0;
    static final int TEAM_COLUMN = 1;
    static final int PLAYED_COLUMN = 2;
    static final int WINS_COLUMN = 3;
    static final int DRAWS_COLUMN = 4;
    static final int DEFEATS_COLUMN = 5;
    static final int SCORED_COLUMN = 6;
    static final int CONCEDED_COLUMN = 7;
    static final int GOAL_DIFFERENCE_COLUMN = 8;
    static final int POINTS_COLUMN = 9;
    static final int AVERAGE_POINTS_COLUMN = 10;
    static final int POINTS_DROPPED_COLUMN = 11;
    static final int FORM_COLUMN = 12;

    private static final String[] COLUMN_NAMES = new String[]
    {
        "Pos.", "Team", "P", "W", "D", "L", "F", "A", "GD", "Pts.", "Average", "Dropped", "Form"
    };

    private final List<TeamRecord> teams;

    public LeagueTableModel(Collection<? extends TeamRecord> teams)
    {
        this.teams = new ArrayList<TeamRecord>(teams.size());
        this.teams.addAll(teams);
    }


    public int getRowCount()
    {
        return teams.size();
    }


    public int getColumnCount()
    {
        return COLUMN_NAMES.length;
    }


    @Override
    public Class<?> getColumnClass(int i)
    {
        switch (i)
        {
            case TEAM_COLUMN: return String.class;
            case AVERAGE_POINTS_COLUMN: return Double.class;
            case FORM_COLUMN: return String.class;
            default : return Integer.class;
        }
    }


    @Override
    public String getColumnName(int i)
    {
        return COLUMN_NAMES[i];
    }

    public Object getValueAt(int row, int column)
    {
        TeamRecord team = teams.get(row);
        switch (column)
        {
            case POSITION_COLUMN: return row + 1;
            case TEAM_COLUMN: return team.getName();
            case PLAYED_COLUMN: return team.getPlayed();
            case WINS_COLUMN: return team.getWon();
            case DRAWS_COLUMN: return team.getDrawn();
            case DEFEATS_COLUMN: return team.getLost();
            case SCORED_COLUMN: return team.getScored();
            case CONCEDED_COLUMN: return team.getConceded();
            case GOAL_DIFFERENCE_COLUMN: return team.getGoalDifference();
            case POINTS_COLUMN: return team.getPoints();
            case AVERAGE_POINTS_COLUMN: return team.getAveragePoints();
            case POINTS_DROPPED_COLUMN: return team.getDroppedPoints();
            case FORM_COLUMN: return team.getForm(); 
            default: throw new IllegalArgumentException("Invalid column index: " + column);
        }
    }
}
