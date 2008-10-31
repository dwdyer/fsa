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
import net.footballpredictions.footballstats.model.SequenceType;
import net.footballpredictions.footballstats.model.StandardRecord;

/**
 * {@link javax.swing.table.TableModel} implementation for displaying sequence charts
 * in a {@link javax.swing.JTable} component.
 * @author Daniel Dyer
 */
public class SequenceTableModel extends AbstractTableModel
{
    static final int POSITION_COLUMN = 0;
    static final int TEAM_COLUMN = 1;
    static final int SEQUENCE_COLUMN = 2;

    private final List<StandardRecord> teams;
    private final SequenceType sequence;
    private final boolean current;
    private final ResourceBundle messageResources;

    private static final String[] COLUMN_NAMES = new String[]
    {
        "sequences.position",
        "sequences.team",
        "sequences.total"
    };

    public SequenceTableModel(Collection<StandardRecord> teams,
                              SequenceType sequence,
                              boolean current,
                              ResourceBundle messageResources)
    {
        this.teams = new ArrayList<StandardRecord>(teams.size());
        this.teams.addAll(teams);
        this.sequence = sequence;
        this.current = current;
        this.messageResources = messageResources;
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
            default : return Integer.class;
        }
    }


    @Override
    public String getColumnName(int i)
    {
        return messageResources.getString(COLUMN_NAMES[i]);
    }


    public Object getValueAt(int row, int column)
    {
        StandardRecord team = teams.get(row);
        switch (column)
        {
            case POSITION_COLUMN: return row + 1;
            case TEAM_COLUMN: return team.getName();
            case SEQUENCE_COLUMN: return current ? team.getCurrentSequence(sequence) : team.getBestSequence(sequence);
            default: throw new IllegalArgumentException("Invalid column index: " + column);
        }
    }
}
