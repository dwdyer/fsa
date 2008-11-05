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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedSet;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.BorderFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.Result;
import net.footballpredictions.footballstats.model.SequenceType;
import net.footballpredictions.footballstats.model.StandardRecord;
import net.footballpredictions.footballstats.model.VenueType;

/**
 * Displays tables of sequences (consecutive wins, games without defeat, etc.)
 * @author Daniel Dyer
 */
public class SequencesPanel extends JPanel implements DataListener
{
    private final ResourceBundle messageResources;

    private LeagueSeason data = null;
    
    private JTable teamsTable;
    private JTable matchesTable;
    private EnumComboBox<SequenceType> sequenceTypeCombo;
    private EnumComboBox<VenueType> venueCombo;
    private JRadioButton currentOption;
    private JRadioButton longestOption;

    /**
     * @param messageResources Internationalised messages for used by the GUI.
     */
    public SequencesPanel(ResourceBundle messageResources)
    {
        super(new BorderLayout());
        this.messageResources = messageResources;
        add(createControls(), BorderLayout.NORTH);
        JPanel main = new JPanel(new RatioLayout(0.45));
        main.add(createTeamsPanel());
        main.add(createMatchesPanel());
        add(main, BorderLayout.CENTER);
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
                    updateTeamsTable();
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

        currentOption = new JRadioButton(messageResources.getString("sequences.current"), true);
        currentOption.addItemListener(itemListener);
        longestOption = new JRadioButton(messageResources.getString("sequences.longest"), false);
        longestOption.addItemListener(itemListener);
        ButtonGroup group = new ButtonGroup();
        group.add(currentOption);
        group.add(longestOption);
        panel.add(currentOption);
        panel.add(longestOption);

        return panel;
    }


    private JComponent createTeamsPanel()
    {
        teamsTable = new StatisticsTable(messageResources);
        teamsTable.setRowSelectionAllowed(true);
        teamsTable.setColumnSelectionAllowed(false);
        teamsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent listSelectionEvent)
            {
                if (teamsTable.getSelectedRow() >= 0)
                {
                    updateMatchesTable();
                }
                else // Don't let there be no row selected.
                {
                    int index = listSelectionEvent.getFirstIndex();
                    if (index >= teamsTable.getRowCount())
                    {
                        index = 0;
                    }
                    teamsTable.setRowSelectionInterval(index, index);
                }
            }
        });
        return new JScrollPane(teamsTable);
    }


    private JComponent createMatchesPanel()
    {
        matchesTable = new StatisticsTable(messageResources);
        JScrollPane scroller = new JScrollPane(matchesTable);
        scroller.setBackground(null);
        return scroller;
    }


    /**
     * Provides the league data used to construct the tables.
     */
    public void setLeagueData(LeagueSeason data)
    {
        this.data = data;
        updateTeamsTable();
    }


    private void updateTeamsTable()
    {
        SequenceType type = (SequenceType) sequenceTypeCombo.getSelectedItem();
        VenueType venue = (VenueType) venueCombo.getSelectedItem();
        boolean current = currentOption.isSelected();

        // Update teams table.
        SortedSet<StandardRecord> teamData = data.getSequenceTable(type, venue, current);
        teamsTable.setModel(new SequenceTableModel(teamData, type, current, messageResources));
        TableColumnModel columnModel = teamsTable.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++)
        {
            TableColumn column = columnModel.getColumn(i);
            // Team name column should be much wider than others (others should all be equal).
            column.setPreferredWidth(i == SequenceTableModel.TEAM_COLUMN ? 125 : 10);
        }
        TableColumn positionColumn = columnModel.getColumn(SequenceTableModel.POSITION_COLUMN);
        positionColumn.setCellRenderer(new PositionRenderer(data.getMetaData(), false));
        TableColumn sequenceColumn = columnModel.getColumn(SequenceTableModel.SEQUENCE_COLUMN);
        sequenceColumn.setCellRenderer(new TableRenderer(null, false, true));
        teamsTable.setRowSelectionInterval(0, 0); // Select first row by default.
    }


    private void updateMatchesTable()
    {
        int row = teamsTable.getSelectedRow();
        String team = (String) teamsTable.getModel().getValueAt(row, SequenceTableModel.TEAM_COLUMN);
        SequenceType type = (SequenceType) sequenceTypeCombo.getSelectedItem();
        VenueType venue = (VenueType) venueCombo.getSelectedItem();
        boolean current = currentOption.isSelected();

        StandardRecord record = data.getTeam(team).getRecord(venue);
        List<Result> sequence = current ? record.getCurrentSequence(type) : record.getBestSequence(type);
        matchesTable.setModel(new TeamResultsTableModel(sequence, team, messageResources));
        TableColumnModel columnModel = matchesTable.getColumnModel();
        TableColumn scoreColumn = columnModel.getColumn(TeamResultsTableModel.SCORE_COLUMN);
        scoreColumn.setPreferredWidth(50);
        scoreColumn.setCellRenderer(new ScoreRenderer(team));
        columnModel.getColumn(TeamResultsTableModel.DATE_COLUMN).setPreferredWidth(110);
        columnModel.getColumn(TeamResultsTableModel.OPPOSITION_COLUMN).setPreferredWidth(190);
    }
}
