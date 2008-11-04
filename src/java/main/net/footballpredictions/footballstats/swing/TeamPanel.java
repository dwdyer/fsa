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
import java.awt.Font;
import java.awt.Color;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.Map;
import java.text.MessageFormat;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import net.footballpredictions.footballstats.model.StandardRecord;
import net.footballpredictions.footballstats.model.Team;
import net.footballpredictions.footballstats.model.VenueType;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.FormRecord;
import net.footballpredictions.footballstats.model.SequenceType;

/**
 * Displays a summary of an individual team's performance over the season.
 * @author Daniel Dyer
 */
class TeamPanel extends JPanel
{
    private final ResourceBundle messageResources;
    private final VenueType venue;
    private ResultsPieChart overallPieChart;
    private ResultsPieChart venuePieChart;
    private JLabel matchesLabel = new JLabel("0", JLabel.RIGHT);
    private JLabel pointsLabel = new JLabel("0", JLabel.RIGHT);
    private JLabel goalDifferenceLabel = new JLabel("0", JLabel.RIGHT);
    private JLabel positionLabel = new JLabel("0", JLabel.RIGHT);
    private FormLabel formLabel = new FormLabel();
    private final JTextArea notes = new JTextArea();

    public TeamPanel(ResourceBundle messageResources, VenueType venue)
    {
        super(new GridLayout(1, 2, 5, 0));
        this.messageResources = messageResources;
        this.venue = venue;
        add(createSummaryPanel());
        add(createPieCharts());
    }


    private JComponent createPieCharts()
    {
        JPanel charts = new JPanel(new GridLayout(2, 1));
        this.overallPieChart = new ResultsPieChart(messageResources, messageResources.getString("headToHead.results"));
        String titleKey = "combo.VenueType." + venue.name();
        this.venuePieChart = new ResultsPieChart(messageResources, messageResources.getString(titleKey));
        charts.add(overallPieChart);
        charts.add(venuePieChart);
        return charts;
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
        pointsLabel.setFont(pointsLabel.getFont().deriveFont(Font.BOLD));
        summary.add(pointsLabel, valueConstraints);
        summary.add(new JLabel(messageResources.getString("headToHead.goalDifference")), labelConstraints);
        summary.add(goalDifferenceLabel, valueConstraints);
        summary.add(new JLabel(messageResources.getString("headToHead.leaguePosition")), labelConstraints);
        summary.add(positionLabel, valueConstraints);
        summary.add(new JLabel(messageResources.getString("headToHead.form")), labelConstraints);
        summary.add(formLabel, valueConstraints);
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.add(summary, BorderLayout.NORTH);
        wrapper.add(createNotes(), BorderLayout.CENTER);
        return wrapper;
    }


    private JComponent createNotes()
    {
        notes.setBackground(null);
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setEnabled(false);
        notes.setFont(new Font("Dialog", Font.PLAIN, 10));
        notes.setDisabledTextColor(Color.BLUE);
        return notes;
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
        overallPieChart.updateGraph(record.getWon(),
                                    record.getDrawn(),
                                    record.getLost());

        StandardRecord venueRecord = team.getRecord(venue);
        venuePieChart.updateGraph(venueRecord.getWon(),
                                  venueRecord.getDrawn(),
                                  venueRecord.getLost());

        updateNotes(record.getInterestingSequences(),
                    venueRecord.getInterestingSequences());
    }


    private void updateNotes(Map<SequenceType, Integer> overallSequences,
                             Map<SequenceType, Integer> venueSequences)
    {
        StringBuilder notesText = new StringBuilder();
        for (Map.Entry<SequenceType, Integer> entry : overallSequences.entrySet())
        {
            String noteFormat = messageResources.getString("headToHead.notes." + entry.getKey().name());
            notesText.append("\u2022 "); // Bullet.
            notesText.append(MessageFormat.format(noteFormat, entry.getValue()));
            notesText.append('\n');
        }
        for (Map.Entry<SequenceType, Integer> entry : venueSequences.entrySet())
        {
            String noteFormat = messageResources.getString("headToHead.notes." + entry.getKey().name());
            String venueText = messageResources.getString(venue == VenueType.HOME
                                                          ? "headToHead.notes.home"
                                                          : "headToHead.notes.away");
            notesText.append("\u2022 "); // Bullet.
            notesText.append(MessageFormat.format(noteFormat, entry.getValue() + " " + venueText));
            notesText.append('\n');
        }
        notes.setText(notesText.toString());
    }
}
