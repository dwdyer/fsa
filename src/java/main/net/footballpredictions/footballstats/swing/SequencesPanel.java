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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;
import java.util.SortedSet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.SequenceType;
import net.footballpredictions.footballstats.model.VenueType;
import net.footballpredictions.footballstats.model.StandardRecord;

/**
 * Displays tables of sequences (consecutive wins, games without defeat, etc.)
 * @author Daniel Dyer
 */
public class SequencesPanel extends JPanel implements StatsPanel
{
    private final ResourceBundle messageResources;

    private LeagueSeason data = null;

    private final JTable currentSequenceTable = new JTable();
    private final JTable bestSequenceTable = new JTable();
    private EnumComboBox<SequenceType> sequenceTypeCombo;
    private EnumComboBox<VenueType> venueCombo;

    /**
     * @param messageResources Internationalised messages for used by the GUI.
     */
    public SequencesPanel(ResourceBundle messageResources)
    {
        super(new BorderLayout());
        this.messageResources = messageResources;
        add(createControls(), BorderLayout.NORTH);
        add(createTables(), BorderLayout.CENTER);
    }


    private JComponent createControls()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        ItemListener itemListener = new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    changeTables();
                }
            }
        };
        sequenceTypeCombo = new EnumComboBox<SequenceType>(SequenceType.class, messageResources);
        sequenceTypeCombo.addItemListener(itemListener);
        panel.add(new JLabel(messageResources.getString("combo.SequenceType.label")));
        panel.add(sequenceTypeCombo);

        venueCombo = new EnumComboBox<VenueType>(VenueType.class, messageResources);
        venueCombo.addItemListener(itemListener);
        panel.add(venueCombo);

        return panel;
    }


    private JComponent createTables()
    {
        JPanel container = new JPanel(new GridLayout(1, 2));
        container.add(createTablePanel(currentSequenceTable, messageResources.getString("sequences.current")));
        container.add(createTablePanel(bestSequenceTable, messageResources.getString("sequences.season")));
        return container;
    }


    private JComponent createTablePanel(JTable table, String title)
    {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JLabel(title, JLabel.CENTER), BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(table));
        TableRenderer renderer = new TableRenderer();
        table.setDefaultRenderer(Object.class, renderer);
        table.setDefaultRenderer(String.class, renderer);
        table.setDefaultRenderer(Integer.class, renderer);
        return tablePanel;
    }


    /**
     * Provides the league data used to construct the tables.
     */
    public void setLeagueData(LeagueSeason data)
    {
        this.data = data;
        changeTables();
    }


    private void changeTables()
    {
        SequenceType type = (SequenceType) sequenceTypeCombo.getSelectedItem();
        VenueType venue = (VenueType) venueCombo.getSelectedItem();

        SortedSet<StandardRecord> modelData = data.getSequenceTable(type, venue, true);
        updateTable(currentSequenceTable, modelData, type, true);

        modelData = data.getSequenceTable(type, venue, false);
        updateTable(bestSequenceTable, modelData, type, false);
    }


    private void updateTable(JTable table,
                             SortedSet<StandardRecord> modelData,
                             SequenceType type,
                             boolean current)
    {
        table.setModel(new SequenceTableModel(modelData, type, current, messageResources));
        TableColumnModel columnModel = table.getColumnModel();

        for (int i = 0; i < columnModel.getColumnCount(); i++)
        {
            TableColumn column = columnModel.getColumn(i);
            // Team name column should be much wider than others (others should all be equal).
            column.setPreferredWidth(i == SequenceTableModel.TEAM_COLUMN ? 125 : 10);
        }

        TableColumn positionColumn = columnModel.getColumn(SequenceTableModel.POSITION_COLUMN);
        positionColumn.setCellRenderer(new PositionRenderer(data.getMetaData(), false));
    }
}
