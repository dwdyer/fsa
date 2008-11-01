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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.Result;
import net.footballpredictions.footballstats.model.Team;
import net.footballpredictions.footballstats.model.VenueType;

/**
 * Displays lists of results (grouped either by team or by date).
 * @author Daniel Dyer
 */
public class ResultsPanel extends JPanel implements DataListener
{
    private final DateFormat dateFormat;

    private final ResourceBundle messageResources;

    private LeagueSeason data = null;

    private final JTable resultsTable = new JTable();
    private EnumComboBox<VenueType> venueCombo;
    private JRadioButton dateOption;
    private JRadioButton teamOption;
    private JComboBox datesCombo = new JComboBox();
    private JComboBox teamsCombo = new JComboBox();

    public ResultsPanel(ResourceBundle messageResources)
    {
        super(new BorderLayout());
        this.messageResources = messageResources;
        dateFormat = new SimpleDateFormat("EEE d MMM yyyy", messageResources.getLocale());
        add(createControls(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
    }

    private JComponent createControls()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        dateOption = new JRadioButton(messageResources.getString("results.byDate"), true);
        teamOption = new JRadioButton(messageResources.getString("results.byTeam"), false);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(dateOption);
        buttonGroup.add(teamOption);

        datesCombo.setRenderer(new DefaultListCellRenderer()
        {

            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object item,
                                                          int row,
                                                          boolean isSelected,
                                                          boolean hasFocus)
            {
                return super.getListCellRendererComponent(list,
                                                          dateFormat.format(item),
                                                          row,
                                                          isSelected,
                                                          hasFocus);
            }
        });

        venueCombo = new EnumComboBox<VenueType>(VenueType.class, messageResources);
        teamsCombo.setEnabled(false);
        venueCombo.setEnabled(false);

        ItemListener itemListener = new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    if (itemEvent.getSource() == dateOption)
                    {
                        datesCombo.setEnabled(true);
                        venueCombo.setEnabled(false);
                        teamsCombo.setEnabled(false);
                    }
                    else if (itemEvent.getSource() == teamOption)
                    {
                        datesCombo.setEnabled(false);
                        venueCombo.setEnabled(true);
                        teamsCombo.setEnabled(true);
                    }
                    changeTables();
                }
            }
        };

        dateOption.addItemListener(itemListener);
        teamOption.addItemListener(itemListener);
        datesCombo.addItemListener(itemListener);
        teamsCombo.addItemListener(itemListener);
        venueCombo.addItemListener(itemListener);

        panel.add(dateOption);
        panel.add(datesCombo);
        panel.add(teamOption);
        panel.add(teamsCombo);
        panel.add(venueCombo);

        return panel;

    }


    private JComponent createTable()
    {
        resultsTable.setDefaultRenderer(String.class, new TableRenderer());
        resultsTable.setDefaultRenderer(Date.class, new DateRenderer(dateFormat));
        resultsTable.setDefaultRenderer(Result.class, new ScoreRenderer());
        return new JScrollPane(resultsTable);
    }


    public void setLeagueData(LeagueSeason data)
    {
        this.data = data;
        datesCombo.removeAllItems();
        for (Date date : data.getDates())
        {
            datesCombo.addItem(date);
        }
        teamsCombo.removeAllItems();
        for (String teamName : data.getTeamNames())
        {
            teamsCombo.addItem(teamName);
        }
        changeTables();
    }


    private void changeTables()
    {
        if (dateOption.isSelected())
        {
            List<Result> results = data.getResults((Date) datesCombo.getSelectedItem());
            resultsTable.setModel(new DateResultsTableModel(results, messageResources));

            TableColumnModel columnModel = resultsTable.getColumnModel();
            
            // Right-justify the home team column so that both home team and away team
            // are lined-up against the score column.
            TableColumn homeTeamColumn = columnModel.getColumn(DateResultsTableModel.HOME_TEAM_COLUMN);
            homeTeamColumn.setCellRenderer(new TableRenderer()
            {

                @Override
                public Component getTableCellRendererComponent(JTable table,
                                                               Object value,
                                                               boolean isSelected,
                                                               boolean hasFocus,
                                                               int row,
                                                               int column)
                {
                    JLabel component = (JLabel) super.getTableCellRendererComponent(table,
                                                                                    value,
                                                                                    isSelected,
                                                                                    hasFocus,
                                                                                    row,
                                                                                    column);
                    component.setHorizontalAlignment(JLabel.RIGHT);
                    return component;
                }
            });

            homeTeamColumn.setPreferredWidth(160);
            columnModel.getColumn(DateResultsTableModel.AWAY_TEAM_COLUMN).setPreferredWidth(160);
            columnModel.getColumn(DateResultsTableModel.SCORE_COLUMN).setPreferredWidth(10);
        }
        else
        {
            Team team = data.getTeam((String) teamsCombo.getSelectedItem());
            List<Result> results = team.getRecord((VenueType) venueCombo.getSelectedItem()).getResults();
            resultsTable.setModel(new TeamResultsTableModel(results, team.getName(), messageResources));

            TableColumnModel columnModel = resultsTable.getColumnModel();
            columnModel.getColumn(TeamResultsTableModel.DATE_COLUMN).setPreferredWidth(50);
            columnModel.getColumn(TeamResultsTableModel.OPPOSITION_COLUMN).setPreferredWidth(200);
            columnModel.getColumn(TeamResultsTableModel.SCORE_COLUMN).setPreferredWidth(10);
        }
    }
}
