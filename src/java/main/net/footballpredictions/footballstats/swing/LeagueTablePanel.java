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

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.VenueType;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * By default displays a standard league table.  Can also display tables based on total points dropped
 * or average points per game.  Can be filtered to include only home games or only away games (default is
 * to include all matches).
 * @author Daniel Dyer
 */
class LeagueTablePanel extends JPanel implements StatsPanel
{
    private final boolean form;

    private LeagueSeason data = null;

    private final JTable leagueTable = new JTable();
    private final JComboBox tableTypeCombo = new JComboBox();
    {
        tableTypeCombo.addItem(TableType.POINTS_WON);
        tableTypeCombo.addItem(TableType.POINTS_PER_GAME);
        tableTypeCombo.addItem(TableType.POINTS_DROPPED);
    }
    private final VenueComboBox venueCombo = new VenueComboBox();

    public LeagueTablePanel(boolean form)
    {
        super(new BorderLayout());
        this.form = form;
        add(createTable(), BorderLayout.CENTER);
        add(createControls(), BorderLayout.EAST);
    }

    private JComponent createTable()
    {
        leagueTable.setIntercellSpacing(new Dimension(0, 1));
        return new JScrollPane(leagueTable);
    }


    private JComponent createControls()
    {
        JPanel panel = new JPanel(new BorderLayout());

        ItemListener itemListener = new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    changeTable();
                }
            }
        };

        JPanel innerPanel = new JPanel(new GridLayout(0, 1));

        if (!form) // Only show table type drop-down if it's not a form table.
        {
            tableTypeCombo.addItemListener(itemListener);
            innerPanel.add(new JLabel("Table Type:"));
            innerPanel.add(tableTypeCombo);
        }

        venueCombo.addItemListener(itemListener);
        innerPanel.add(new JLabel("Matches:"));
        innerPanel.add(venueCombo);
        panel.add(innerPanel, BorderLayout.NORTH);

        return panel;
    }


    /**
     * Provides the league data used to construct the tables.
     */
    public void setLeagueData(LeagueSeason data)
    {
        this.data = data;
        changeTable();
    }


    private void changeTable()
    {
        TableType type = (TableType) tableTypeCombo.getSelectedItem(); // Will be null for form tables.
        
        leagueTable.setModel(getTableModel(type, (VenueType) venueCombo.getSelectedItem()));
        TableColumnModel columnModel = leagueTable.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++)
        {
            TableColumn column = columnModel.getColumn(i);
            // Team name column should be much wider than others (others should all be equal).
            column.setPreferredWidth(i == LeagueTableModel.TEAM_COLUMN ? 125 : 10);
        }

        TableColumn averageColumn = columnModel.getColumn(LeagueTableModel.AVERAGE_POINTS_COLUMN);
        averageColumn.setPreferredWidth(30);
        TableColumn droppedColumn = columnModel.getColumn(LeagueTableModel.POINTS_DROPPED_COLUMN);
        droppedColumn.setPreferredWidth(30);
        TableColumn formColumn = columnModel.getColumn(LeagueTableModel.FORM_COLUMN);
        formColumn.setPreferredWidth(40);

        // Hide columns that aren't relevant for the selected table type.
        if (!form)
        {
            columnModel.removeColumn(formColumn);
        }
        if (type != TableType.POINTS_PER_GAME)
        {
            columnModel.removeColumn(averageColumn);
        }
        if (type != TableType.POINTS_DROPPED)
        {
            columnModel.removeColumn(droppedColumn);
        }

        LeagueTableRenderer renderer = new LeagueTableRenderer(data, !form);
        leagueTable.setDefaultRenderer(Object.class, renderer);
        leagueTable.setDefaultRenderer(Number.class, renderer);
        leagueTable.setDefaultRenderer(Double.class, renderer);
        TableColumn positionColumn = columnModel.getColumn(LeagueTableModel.POSITION_COLUMN);
        positionColumn.setCellRenderer(new PositionRenderer(data, !form));
        TableColumn goalDifferenceColumn = columnModel.getColumn(LeagueTableModel.GOAL_DIFFERENCE_COLUMN);
        goalDifferenceColumn.setCellRenderer(new GoalDifferenceRenderer(data, !form));
        formColumn.setCellRenderer(new FormRenderer(data, !form));
    }


    /**
     * Constructs a league table of the specified type.
     * @param type The type of table to create (standard, average points, or inverted).
     * @param where Whether to include just home matches, just away matches, or both.
     * @return A {@link LeagueTableModel} containing an ordered set of team records.
     */
    private LeagueTableModel getTableModel(TableType type, VenueType where)
    {
        if (form)
        {
            return new LeagueTableModel(data.getFormTable(where));
        }
        else
        {
            switch (type)
            {
                case POINTS_WON: return new LeagueTableModel(data.getStandardLeagueTable(where));
                case POINTS_PER_GAME: return new LeagueTableModel(data.getAverageLeagueTable(where));
                case POINTS_DROPPED: return new LeagueTableModel(data.getInvertedLeagueTable(where));
                default: throw new IllegalStateException("Unexpected venue type: " + where);
            }
        }
    }


    private static enum TableType
    {
        POINTS_WON("Points Won"),
        POINTS_PER_GAME("Average Points"),
        POINTS_DROPPED("Points Dropped");

        private final String description;

        private TableType(String description)
        {
            this.description = description;
        }

        @Override
        public String toString()
        {
            return description;
        }
    }
}
