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
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import java.util.ResourceBundle;
import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * Plot points earned against number of matches played for one or more teams.
 * @author Daniel Dyer
 */
class PointsGraph extends ChartPanel
{
    private final ResourceBundle messageResources;

    public PointsGraph(ResourceBundle messageResources)
    {
        super(null, false, false, false, false, true);
        this.messageResources = messageResources;
    }


    /**
     * Plot points earned against number of matches played.
     */
    public void updateGraph(Object[] teams, LeagueSeason data)
    {
        assert teams.length > 0 : "Must be at least one team selected.";
        XYSeriesCollection dataSet = new XYSeriesCollection();
        int max = 0;
        for (Object team : teams)
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
        setChart(chart);
    }
}
