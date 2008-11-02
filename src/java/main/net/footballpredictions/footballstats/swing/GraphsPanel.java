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
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Panel for displaying graphs illustrating the performance of one or more teams.
 * @author Daniel Dyer
 */
public class GraphsPanel extends JPanel implements DataListener
{
    private final ResourceBundle messageResources;

    private LeagueSeason data = null;
    private final JList teamsList = new JList();
    private EnumComboBox<GraphType> graphTypeCombo;
    private ChartPanel chartPanel = new ChartPanel(null, false, false, false, false, true);

    public GraphsPanel(ResourceBundle messageResources)
    {
        super(new BorderLayout());
        this.messageResources = messageResources;
        
        JPanel inner = new JPanel(new BorderLayout());
        inner.add(createControls(), BorderLayout.NORTH);
        inner.add(chartPanel, BorderLayout.CENTER);
        add(inner, BorderLayout.CENTER);

        add(createTeamSelector(), BorderLayout.EAST);
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


    private void changeGraph()
    {
        GraphType type = (GraphType) graphTypeCombo.getSelectedItem();
        if (type == GraphType.LEAGUE_POSITION)
        {
            teamsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            chartPanel.setChart(createLeaguePositionGraph());
        }
        else if (type == GraphType.POINTS)
        {
            teamsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            chartPanel.setChart(createPointsGraph());
        }
        else if (type == GraphType.GOALS)
        {
            teamsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            teamsList.setSelectedIndex(teamsList.getSelectedIndex()); // Only one team can be selected now.
            chartPanel.setChart(createGoalsGraph());
        }
    }


    /**
     * Plot league positions by date.
     */
    private JFreeChart createLeaguePositionGraph()
    {
        TimeSeriesCollection dataSet = new TimeSeriesCollection();
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

        JFreeChart chart = ChartFactory.createTimeSeriesChart(null, // Title
                                                              messageResources.getString("graphs.date"),
                                                              messageResources.getString("combo.GraphType.LEAGUE_POSITION"),
                                                              dataSet,
                                                              true, // Legend.
                                                              false, // Tooltips.
                                                              false); // URLs.
        chart.getXYPlot().getRangeAxis().setInverted(true);
        chart.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart.getXYPlot().getRangeAxis().setRangeWithMargins(1, data.getTeamNames().size());
        return chart;
    }


    /**
     * Plot points earned against number of matches played.
     */
    private JFreeChart createPointsGraph()
    {
        XYSeriesCollection dataSet = new XYSeriesCollection();
        Object[] teamNames = teamsList.getSelectedValues();
        int max = 0;
        for (Object team : teamNames)
        {
            String teamName = (String) team;
            XYSeries pointsSeries = new XYSeries(teamName);

            int[] points = data.getTeam(teamName).getPointsData(data.getMetaData().getPointsForWin(),
                                                                data.getMetaData().getPointsForDraw());
            for (int i = 0; i < points.length; i++)
            {
                pointsSeries.add(i, points[i]);
            }
            max = Math.max(max, points[points.length - 1]);
            dataSet.addSeries(pointsSeries);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(null, // Title
                                                          messageResources.getString("graphs.matches"),
                                                          messageResources.getString("combo.GraphType.POINTS"),
                                                          dataSet,
                                                          PlotOrientation.VERTICAL,
                                                          true, // Legend.
                                                          false, // Tooltips.
                                                          false); // URLs.
        chart.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart.getXYPlot().getRangeAxis().setRange(0, max + 1);
        return chart;
    }


    /**
     * Plot goals scored and conceded against number of matches played.
     */
    private JFreeChart createGoalsGraph()
    {
        XYSeriesCollection dataSet = new XYSeriesCollection();
        String teamName = (String) teamsList.getSelectedValue();
        XYSeries forSeries = new XYSeries(teamName + ' ' + messageResources.getString("graphs.scored"));
        XYSeries againstSeries = new XYSeries(teamName + ' ' + messageResources.getString("graphs.conceded"));

        int[][] goals = data.getTeam(teamName).getGoalsData();
        for (int i = 0; i < goals.length; i++)
        {
            forSeries.add(i, goals[i][0]);
            againstSeries.add(i, goals[i][1]);
        }

        dataSet.addSeries(forSeries);
        dataSet.addSeries(againstSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(null, // Title
                                                          messageResources.getString("graphs.matches"),
                                                          messageResources.getString("combo.GraphType.GOALS"),
                                                          dataSet,
                                                          PlotOrientation.VERTICAL,
                                                          true, // Legend.
                                                          false, // Tooltips.
                                                          false); // URLs.
        chart.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        int max = Math.max(goals[goals.length - 1][0], goals[goals.length - 1][1]);
        chart.getXYPlot().getRangeAxis().setRange(0, max + 1);
        XYDifferenceRenderer renderer = new XYDifferenceRenderer();
        renderer.setSeriesPaint(0, new Color(0, 128, 0)); // Green.
        renderer.setPositivePaint(new Color(0, 255, 0, 128)); // Translucent green.
        renderer.setSeriesPaint(1, new Color(192, 0, 0)); // Red.
        renderer.setNegativePaint(new Color(255, 0, 0, 128)); // Translucent red.
        chart.getXYPlot().setRenderer(renderer);
        return chart;
    }
}
