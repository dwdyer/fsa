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

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.util.ResourceBundle;
import java.util.SortedSet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.footballpredictions.footballstats.model.StandardRecord;
import net.footballpredictions.footballstats.model.Team;
import net.footballpredictions.footballstats.model.VenueType;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.FormRecord;

/**
 * Displays a summary of an individual team's performance over the season.
 * @author Daniel Dyer
 */
class TeamPanel extends JPanel
{
    private final ResourceBundle messageResources;
    private ResultsPieChart pieChart;
    private JLabel matchesLabel = new JLabel("0", JLabel.RIGHT);
    private JLabel pointsLabel = new JLabel("0", JLabel.RIGHT);
    private JLabel goalDifferenceLabel = new JLabel("0", JLabel.RIGHT);
    private JLabel positionLabel = new JLabel("0", JLabel.RIGHT);
    private FormLabel formLabel = new FormLabel();

    public TeamPanel(ResourceBundle messageResources)
    {
        super(new GridLayout(2, 1));
        this.messageResources = messageResources;
        add(createOverviewPanel());
    }


    private JComponent createOverviewPanel()
    {
        JPanel overview = new JPanel(new GridLayout(1, 2, 5, 0));
        this.pieChart = new ResultsPieChart(messageResources);
        overview.add(createSummaryPanel());
        overview.add(pieChart);
        return overview;
    }


    private JComponent createSummaryPanel()
    {
        JPanel summary = new JPanel(new GridBagLayout());
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridwidth = GridBagConstraints.RELATIVE;
        labelConstraints.weightx = 1;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints valueConstraints = (GridBagConstraints) labelConstraints.clone();
        valueConstraints.gridwidth = GridBagConstraints.REMAINDER;
        valueConstraints.weightx = 0;
        summary.add(new JLabel(messageResources.getString("headToHead.played")), labelConstraints);
        summary.add(matchesLabel, valueConstraints);
        summary.add(new JLabel(messageResources.getString("headToHead.points")), labelConstraints);
        summary.add(pointsLabel, valueConstraints);
        summary.add(new JLabel(messageResources.getString("headToHead.goalDifference")), labelConstraints);
        summary.add(goalDifferenceLabel, valueConstraints);
        summary.add(new JLabel(messageResources.getString("headToHead.leaguePosition")), labelConstraints);
        summary.add(positionLabel, valueConstraints);
        summary.add(new JLabel(messageResources.getString("headToHead.form")), labelConstraints);
        summary.add(formLabel, valueConstraints);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(summary, BorderLayout.NORTH);
        return wrapper;
    }


    public void setTeam(Team team, LeagueSeason data)
    {
        StandardRecord record = team.getRecord(VenueType.BOTH);
        matchesLabel.setText(String.valueOf(record.getPlayed()));
        pointsLabel.setText(String.valueOf(record.getPoints()));
        int goalDifference = record.getGoalDifference();
        String gdString = String.valueOf(goalDifference);
        // Goal difference always has explicit plus/minus sign.
        goalDifferenceLabel.setText(goalDifference > 0 ? '+' + gdString : gdString);
        // Goal difference is rendered green for positive, red for negative and black for zero.
        goalDifferenceLabel.setForeground(Colours.getNumberColour(record.getGoalDifference()));
        positionLabel.setText(String.valueOf(team.getLastLeaguePosition()));
        SortedSet<FormRecord> formTable = data.getFormTable(VenueType.BOTH);
        int stars = record.getFormRecord().getFormStars(formTable.last().getPoints(),
                                                        formTable.first().getPoints());
        formLabel.setForm(stars, record.getForm());
        pieChart.updateGraph(record.getWon(),
                             record.getDrawn(),
                             record.getLost());
    }
}
