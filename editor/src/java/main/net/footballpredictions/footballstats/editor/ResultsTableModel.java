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
package net.footballpredictions.footballstats.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;
import javax.swing.table.AbstractTableModel;
import net.footballpredictions.footballstats.model.Result;

/**
 * @author Daniel Dyer
 */
class ResultsTableModel extends AbstractTableModel
{
    static final int DATE_COLUMN = 0;
    static final int HOME_TEAM_COLUMN = 1;
    static final int HOME_GOALS_COLUMN = 2;
    static final int AWAY_TEAM_COLUMN = 3;
    static final int AWAY_GOALS_COLUMN = 4;
    static final int ATTENDANCE_COLUMN = 5;

    private final List<Result> results;

    private static final String[] COLUMN_NAMES = new String[]{"Date",
                                                              "Home Team",
                                                              "Goals",
                                                              "Away Team",
                                                              "Goals",
                                                              "Attendance"};

    public ResultsTableModel(Collection<Result> results)
    {
        this.results = new ArrayList<Result>(results);
    }


    public int getRowCount()
    {
        return results.size();
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
            case DATE_COLUMN: return Date.class;
            case HOME_TEAM_COLUMN: return String.class;
            case AWAY_TEAM_COLUMN: return String.class;
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
        Result result = results.get(row);
        switch (column)
        {
            case DATE_COLUMN: return result.getDate();
            case HOME_TEAM_COLUMN: return result.getHomeTeam();
            case HOME_GOALS_COLUMN: return result.getHomeGoals();
            case AWAY_TEAM_COLUMN: return result.getAwayTeam();
            case AWAY_GOALS_COLUMN: return result.getAwayGoals();
            case ATTENDANCE_COLUMN: return result.getAttendance();
            default: throw new IllegalArgumentException("Invalid column index: " + column);
        }
    }
}
