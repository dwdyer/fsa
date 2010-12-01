// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2010 Daniel W. Dyer
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
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.Day;
import org.jfree.ui.RectangleEdge;
import java.util.Date;
import java.util.SortedMap;
import java.util.Map;
import java.util.ResourceBundle;
import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * Plot league positions by date for one or more teams.
 * @author Daniel Dyer
 */
class LeaguePositionGraph extends ChartPanel
{
    private final ResourceBundle messageResources;
    private final RectangleEdge legendPosition;

    public LeaguePositionGraph(ResourceBundle messageResources, RectangleEdge legendPosition)
    {
        super(null, false, false, false, false, true);
        this.messageResources = messageResources;
        this.legendPosition = legendPosition;
    }


    /**
     * Plot league positions by date.
     */
    public void updateGraph(Object[] teams, LeagueSeason data)
    {
        assert teams.length > 0 : "Must be at least one team selected.";
        TimeSeriesCollection dataSet = new TimeSeriesCollection();
        for (Object team : teams)
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
        chart.getLegend().setPosition(legendPosition);
        setChart(chart);
    }
}
