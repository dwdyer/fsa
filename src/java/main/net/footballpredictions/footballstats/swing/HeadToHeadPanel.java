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
import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.ResourceBundle;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.VenueType;
import org.jfree.ui.RectangleEdge;

/**
 * Displays comparative statistics for a pair of teams.
 * @author Daniel Dyer
 */
public class HeadToHeadPanel extends JPanel implements DataListener
{
    private final ResourceBundle messageResources;

    private LeagueSeason data;

    private final JComboBox homeTeamCombo = new JComboBox();
    private final JComboBox awayTeamCombo = new JComboBox();
    private TeamPanel homeTeamPanel;
    private TeamPanel awayTeamPanel;
    private LeaguePositionGraph positionGraph;

    public HeadToHeadPanel(ResourceBundle messageResources)
    {
        super(new BorderLayout());
        this.messageResources = messageResources;


        add(createControls(), BorderLayout.NORTH);
        add(createTeamsPanel(), BorderLayout.CENTER);
        add(createComparisonPanel(), BorderLayout.SOUTH);
    }


    private JComponent createControls()
    {
        JPanel controls = new JPanel(new GridLayout(1, 2));
        JPanel homeControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        homeControls.add(new JLabel(messageResources.getString("headToHead.homeTeam")));
        homeControls.add(homeTeamCombo);
        JPanel awayControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        awayControls.add(new JLabel(messageResources.getString("headToHead.awayTeam")));
        awayControls.add(awayTeamCombo);
        controls.add(homeControls);
        controls.add(awayControls);
        ItemListener itemListener = new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED
                    && homeTeamCombo.getItemCount() > 0 && awayTeamCombo.getItemCount() > 0)
                {
                    updateView();
                }
            }
        };
        homeTeamCombo.addItemListener(itemListener);
        awayTeamCombo.addItemListener(itemListener);
        return controls;
    }


    private JComponent createTeamsPanel()
    {
        JPanel teamsPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        homeTeamPanel = new TeamPanel(messageResources, VenueType.HOME);
        awayTeamPanel = new TeamPanel(messageResources, VenueType.AWAY);
        teamsPanel.add(homeTeamPanel);
        teamsPanel.add(awayTeamPanel);
        return teamsPanel;
    }


    private JComponent createComparisonPanel()
    {
        positionGraph = new LeaguePositionGraph(messageResources, RectangleEdge.RIGHT);
        positionGraph.setPreferredSize(new Dimension(0, 150));
        return positionGraph;
    }


    public void setLeagueData(LeagueSeason data)
    {
        this.data = data;
        
        homeTeamCombo.removeAllItems();
        awayTeamCombo.removeAllItems();
        for (String teamName : data.getTeamNames())
        {
            homeTeamCombo.addItem(teamName);
            awayTeamCombo.addItem(teamName);
        }
        awayTeamCombo.setSelectedIndex(1); // Default to different team than home team.
    }


    private void updateView()
    {
        String homeTeamName = (String) homeTeamCombo.getSelectedItem();
        String awayTeamName = (String) awayTeamCombo.getSelectedItem();
        positionGraph.updateGraph(new String[]{homeTeamName, awayTeamName}, data);
        homeTeamPanel.setTeam(data.getTeam(homeTeamName), data);
        awayTeamPanel.setTeam(data.getTeam(awayTeamName), data);
    }
}
