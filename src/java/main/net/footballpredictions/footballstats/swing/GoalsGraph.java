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

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import java.util.ResourceBundle;
import java.awt.Color;
import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * Plot goals scored and conceded against number of matches played.
 * @author Daniel Dyer
 */
class GoalsGraph extends ChartPanel
{
    private final ResourceBundle messageResources;

    public GoalsGraph(ResourceBundle messageResources)
    {
        super(null, false, false, false, false, true);
        this.messageResources = messageResources;
    }


    /**
     * Plot goals scored and conceded against number of matches played.
     */
    public void updateGraph(String teamName, LeagueSeason data)
    {
        XYSeriesCollection dataSet = new XYSeriesCollection();

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
        setChart(chart);
    }
}
