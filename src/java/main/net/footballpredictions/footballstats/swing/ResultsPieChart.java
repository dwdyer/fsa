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

import java.util.ResourceBundle;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 * Plot points earned against number of matches played for one or more teams.
 * @author Daniel Dyer
 */
class ResultsPieChart extends ChartPanel
{
    private final ResourceBundle messageResources;
    private final String title;

    public ResultsPieChart(ResourceBundle messageResources,
                           String title)
    {
        super(null, false, false, false, false, true);
        this.messageResources = messageResources;
        this.title = title;
    }


    /**
     * Plot points earned against number of matches played.
     */
    public void updateGraph(int won, int drawn, int lost)
    {
        DefaultKeyedValues values = new DefaultKeyedValues();
        values.addValue(messageResources.getString("headToHead.won"), won);
        values.addValue(messageResources.getString("headToHead.drawn"), drawn);
        values.addValue(messageResources.getString("headToHead.lost"), lost);
        PieDataset dataSet = new DefaultPieDataset(values);

        JFreeChart chart = ChartFactory.createPieChart(title,
                                                       dataSet,
                                                       false, // Legend.
                                                       true, // Tooltips.
                                                       false); // URLs.
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setCircular(true);
        plot.setLabelGap(-0.1);
        plot.setLabelLinksVisible(false);
        plot.setInteriorGap(0);
        plot.setSectionPaint(0, Colours.WIN);
        plot.setSectionPaint(1, Colours.DRAW);
        plot.setSectionPaint(2, Colours.DEFEAT);
        plot.setBackgroundPaint(null);
        plot.setOutlinePaint(null);
        plot.setLabelGenerator(null);
        plot.setToolTipGenerator(new StandardPieToolTipGenerator("{0} {1} ({2})"));
        setChart(chart);
    }
}
