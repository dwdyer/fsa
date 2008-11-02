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
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.footballpredictions.footballstats.model.LeagueSeason;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * Panel for displaying graphs illustrating the performance of one or more teams.
 * @author Daniel Dyer
 */
public class GraphsPanel extends JPanel implements DataListener
{
    private final ResourceBundle messageResources;

    private LeagueSeason data = null;
    private final JList teamsList = new JList();
    private final TimeSeriesCollection dataSet = new TimeSeriesCollection();
    private JFreeChart chart;

    public GraphsPanel(ResourceBundle messageResources)
    {
        super(new BorderLayout());
        this.messageResources = messageResources;
        add(createControls(), BorderLayout.EAST);
        add(createGraph(), BorderLayout.CENTER);
    }


    private JComponent createControls()
    {
        ListSelectionListener selectionListener = new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent listSelectionEvent)
            {
                changeGraph();
            }
        };
        teamsList.addListSelectionListener(selectionListener);
        JScrollPane scroller = new JScrollPane(teamsList);
        scroller.setBackground(null);
        scroller.setBorder(BorderFactory.createTitledBorder(messageResources.getString("graphs.teams")));
        return scroller;
    }


    private JComponent createGraph()
    {
        chart = ChartFactory.createTimeSeriesChart(null, // Title
                                                   "Time",
                                                   "League Position",
                                                   dataSet,
                                                   true, // Legend.
                                                   false, // Tooltips.
                                                   false); // URLs.
        chart.getXYPlot().getRangeAxis().setInverted(true);
        chart.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return new ChartPanel(chart);
    }


    public void setLeagueData(LeagueSeason data)
    {
        this.data = data;
        teamsList.setListData(data.getTeamNames().toArray());
        teamsList.setSelectedIndex(0);
    }


    private void changeGraph()
    {
        dataSet.removeAllSeries();
        Object[] teamNames = teamsList.getSelectedValues();
        for (Object team : teamNames)
        {
            String teamName = (String) team; 
            TimeSeries positionSeries = new TimeSeries(teamName);

            SortedMap<Date, Integer> positions = data.getTeam(teamName).getLeaguePositions();
            for (Map.Entry<Date, Integer> entry : positions.entrySet())
            {
                positionSeries.add(new Day(entry.getKey()), entry.getValue());
            }
            dataSet.addSeries(positionSeries);
        }
        chart.getXYPlot().getRangeAxis().setRangeWithMargins(1, data.getTeamNames().size());
    }
}
