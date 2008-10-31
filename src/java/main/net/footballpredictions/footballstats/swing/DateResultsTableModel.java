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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;
import net.footballpredictions.footballstats.model.Result;

/**
 * {@link javax.swing.table.TableModel} implementation for displaying sets of match
 * results in a {@link javax.swing.JTable} component.
 * @author Daniel Dyer
 */
class DateResultsTableModel extends AbstractTableModel
{
    static final int HOME_TEAM_COLUMN = 0;
    static final int SCORE_COLUMN = 1;
    static final int AWAY_TEAM_COLUMN = 2;

    private final List<Result> results;
    private final ResourceBundle messageResources;

    private static final String[] COLUMN_NAMES = new String[]
    {
        "results.homeTeam",
        "results.score",
        "results.awayTeam"
    };

    public DateResultsTableModel(Collection<Result> results,
                                 ResourceBundle messageResources)
    {
        this.results = new ArrayList<Result>(results);
        this.messageResources = messageResources;
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
            case SCORE_COLUMN: return Result.class;
            default : return String.class;
        }
    }


    @Override
    public String getColumnName(int i)
    {
        return messageResources.getString(COLUMN_NAMES[i]);
    }


    public Object getValueAt(int row, int column)
    {
        Result result = results.get(row);
        switch (column)
        {
            case HOME_TEAM_COLUMN: return result.getHomeTeam();
            case SCORE_COLUMN: return result;
            case AWAY_TEAM_COLUMN: return result.getAwayTeam();
            default: throw new IllegalArgumentException("Invalid column index: " + column);
        }
    }

}
