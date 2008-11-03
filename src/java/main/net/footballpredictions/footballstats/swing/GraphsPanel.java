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
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * Panel for displaying graphs illustrating the performance of one or more teams.
 * @author Daniel Dyer
 */
public class GraphsPanel extends JPanel implements DataListener
{
    private final ResourceBundle messageResources;

    private LeagueSeason data = null;
    
    private final CardLayout chartsLayout = new CardLayout();
    private final JPanel chartsPanel = new JPanel(chartsLayout);
    private final JList teamsList = new JList();
    private EnumComboBox<GraphType> graphTypeCombo;
    private LeaguePositionGraph leaguePositionGraph;
    private PointsGraph pointsGraph;
    private GoalsGraph goalsGraph;

    public GraphsPanel(ResourceBundle messageResources)
    {
        super(new BorderLayout());
        this.messageResources = messageResources;
        
        JPanel inner = new JPanel(new BorderLayout());
        inner.add(createControls(), BorderLayout.NORTH);

        inner.add(createCharts(), BorderLayout.CENTER);
        add(inner, BorderLayout.CENTER);

        add(createTeamSelector(), BorderLayout.EAST);
    }


    private JComponent createCharts()
    {
        leaguePositionGraph = new LeaguePositionGraph(messageResources);
        chartsPanel.add(leaguePositionGraph, GraphType.LEAGUE_POSITION.name());
        pointsGraph = new PointsGraph(messageResources);
        chartsPanel.add(pointsGraph, GraphType.POINTS.name());
        goalsGraph = new GoalsGraph(messageResources);
        chartsPanel.add(goalsGraph, GraphType.GOALS.name());
        return chartsPanel;
    }


    private JComponent createControls()
    {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        graphTypeCombo = new EnumComboBox<GraphType>(GraphType.class, messageResources);
        graphTypeCombo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    changeGraph();
                }
            }
        });
        controls.add(new JLabel(messageResources.getString("graphs.type")));
        controls.add(graphTypeCombo);
        return controls;
    }


    private JComponent createTeamSelector()
    {
        teamsList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent listSelectionEvent)
            {
                if (teamsList.getSelectedIndex() >= 0)
                {
                    changeGraph();
                }
            }
        });
        JScrollPane scroller = new JScrollPane(teamsList);
        scroller.setBackground(null);
        scroller.setBorder(BorderFactory.createTitledBorder(messageResources.getString("graphs.teams")));
        return scroller;
    }


    public void setLeagueData(LeagueSeason data)
    {
        this.data = data;
        teamsList.setListData(data.getTeamNames().toArray());
        teamsList.setSelectedIndex(0);
    }


    /**
     * Updates the graph in response to a change in the selected graph type or selected
     * team(s).
     */
    private void changeGraph()
    {
        GraphType type = (GraphType) graphTypeCombo.getSelectedItem();
        if (type == GraphType.LEAGUE_POSITION)
        {
            teamsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            leaguePositionGraph.updateGraph(teamsList.getSelectedValues(), data);
        }
        else if (type == GraphType.POINTS)
        {
            teamsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            pointsGraph.updateGraph(teamsList.getSelectedValues(), data);
        }
        else if (type == GraphType.GOALS)
        {
            // Only one team can be selected for the goals graph.
            teamsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            // If there is more than one selected already, change the selection so that
            // only the first one is selected.
            teamsList.setSelectedIndex(teamsList.getSelectedIndex());
            goalsGraph.updateGraph((String) teamsList.getSelectedValue(), data);
        }
        chartsLayout.show(chartsPanel, type.name());
    }
}
