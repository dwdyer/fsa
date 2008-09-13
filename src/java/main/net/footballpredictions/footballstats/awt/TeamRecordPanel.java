// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   � Copyright 2000-2008 Daniel W. Dyer
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
package net.footballpredictions.footballstats.awt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.ResourceBundle;

import net.footballpredictions.footballstats.model.Result;
import net.footballpredictions.footballstats.model.StandardRecord;
import net.footballpredictions.footballstats.model.Team;
import net.footballpredictions.footballstats.model.VenueType;

/**
 * Sub-panel of Head-to-Head display.
 * @author Daniel Dyer.
 */
final class TeamRecordPanel extends Panel
{
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    
    private Theme theme;
    
    private final Label nameLabel = new Label("", Label.CENTER);
    private final Label positionLabel = new Label("", Label.CENTER);
    private final Label playingRecordTitleLabel = new Label();
    private final Label playedLabel = new Label();
    private final Label wonLabel = new Label();
    private final Label wonPercentageLabel = new Label();
    private final Label drawnLabel = new Label();
    private final Label drawnPercentageLabel = new Label();
    private final Label lostLabel = new Label();
    private final Label lostPercentageLabel = new Label();
    private final Label gdLabel = new Label();
    private final Label gdDetailsLabel = new Label();
    private final Label scoredAvgLabel = new Label();
    private final Label concededAvgLabel = new Label();
    private final Label pointsLabel = new Label();
    private final Label pointsAverageLabel = new Label();
    private final Label formLabel = new Label();
    private final Label bigWinTitleLabel = new Label();
    private final Label bigDefeatTitleLabel = new Label();
    private final Label mostRecentTitleLabel = new Label();
    private final Label bigWinLabel = new Label();
    private final Label bigDefeatLabel = new Label();
    private final Label mostRecentLabel = new Label();

    private final Label[] notesLabels = new Label[3];
    
    private ResourceBundle res = null;
        
    public TeamRecordPanel(ResourceBundle res)
    {
        super(new BorderLayout());

        this.res = res;
        // Header.
        Panel header = new Panel(new BorderLayout());
        header.add(nameLabel, BorderLayout.NORTH);
        header.add(positionLabel, BorderLayout.CENTER);
        header.add(playingRecordTitleLabel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
            
        // Footer.
        Panel notesPanel = new Panel(new GridLayout(3, 1));
        for (int i = 0; i < notesLabels.length; i++)
        {
            notesLabels[i] = new Label("");
            notesPanel.add(notesLabels[i]);
        }
        add(notesPanel, BorderLayout.SOUTH);
            
        // Main stats.
        Panel mainPanel = new Panel(new GridLayout(0, 2));
        
        mainPanel.add(new Label(res.getString("team.playing_record.played")));
        mainPanel.add(playedLabel);

        mainPanel.add(new Label(res.getString("team.playing_record.won")));
        mainPanel.add(Util.wrapLabelPair(wonLabel, wonPercentageLabel));
        
        mainPanel.add(new Label(res.getString("team.playing_record.drawn")));
        mainPanel.add(Util.wrapLabelPair(drawnLabel, drawnPercentageLabel));

        mainPanel.add(new Label(res.getString("team.playing_record.lost")));
        mainPanel.add(Util.wrapLabelPair(lostLabel, lostPercentageLabel));

        mainPanel.add(new Label(res.getString("team.playing_record.goal_difference")));
        mainPanel.add(Util.wrapLabelPair(gdLabel, gdDetailsLabel));
        
        mainPanel.add(new Label(res.getString("team.playing_record.goals_average_game")));
        mainPanel.add(Util.wrapLabelPair(scoredAvgLabel, concededAvgLabel));


        mainPanel.add(new Label(res.getString("team.playing_record.points")));
        mainPanel.add(Util.wrapLabelPair(pointsLabel, pointsAverageLabel));

        mainPanel.add(new Label(res.getString("team.playing_record.form")));
        mainPanel.add(formLabel);

        // Key results.
        Panel resultsPanel = new Panel(new GridLayout(0, 1));
        resultsPanel.add(bigWinTitleLabel);
        resultsPanel.add(bigWinLabel);
        resultsPanel.add(bigDefeatTitleLabel);
        resultsPanel.add(bigDefeatLabel);
        resultsPanel.add(mostRecentTitleLabel);
        resultsPanel.add(mostRecentLabel);
            
        Panel statsWrapper = new Panel(new GridLayout(2, 1));
        statsWrapper.add(mainPanel);
        statsWrapper.add(resultsPanel);
        add(statsWrapper, BorderLayout.CENTER);
    }
    
    
    public void setTheme(Theme theme)
    {
        this.theme = theme;
        nameLabel.setFont(theme.getTitleFont());
        positionLabel.setFont(theme.getSmallFont());
        playingRecordTitleLabel.setFont(theme.getBoldFont());
        pointsLabel.setFont(theme.getBoldFont());
        formLabel.setFont(theme.getFixedWidthFont());
        bigWinTitleLabel.setFont(theme.getBoldFont());
        bigDefeatTitleLabel.setFont(theme.getBoldFont());
        mostRecentTitleLabel.setFont(theme.getBoldFont());
        for (Label notesLabel : notesLabels)
        {
            notesLabel.setForeground(theme.getNoteColour());
        }
    }

        
    public void setTeam(Team team, VenueType where)
    {
        StandardRecord record = team.getRecord(where);
        
        nameLabel.setText(team.getName());
        int pos = team.getLastLeaguePosition();
        positionLabel.setText( res.getString("team.currentPosition") + pos + getSuffix(pos));
        String venueText = getVenueText(where);
        playingRecordTitleLabel.setText(venueText + res.getString("team.playing_record"));
        if (where == VenueType.BOTH)
        {
            bigWinTitleLabel.setText( res.getString("team.playing_record.biggestWin"));
            bigDefeatTitleLabel.setText(res.getString("team.playing_record.biggestDefeat"));
            mostRecentTitleLabel.setText(res.getString("team.playing_record.mostRecentResult"));
        }
        else
        {
        	if (res.getLocale() != null && res.getLocale().getCountry().toLowerCase().equals("sk") == true){
        		bigWinTitleLabel.setText(res.getString("team.playing_record.biggest") 
    					+ res.getString("team.playing_record.label.win").toLowerCase() + " "
    					+ venueText.toLowerCase());
			    bigDefeatTitleLabel.setText(res.getString("team.playing_record.biggest") 
			    					+  res.getString("team.playing_record.label.defeat").toLowerCase() + " "
			    					+ venueText.toLowerCase());
			    mostRecentTitleLabel.setText(res.getString("team.playing_record.mostRecent") + " "
			    					+ venueText.toLowerCase());
        	}
        	else{
        		bigWinTitleLabel.setText(res.getString("team.playing_record.biggest") 
    					+ venueText.toLowerCase() 
    					+ res.getString("team.playing_record.label.win").toLowerCase());
			    bigDefeatTitleLabel.setText(res.getString("team.playing_record.biggest") 
			    					+ venueText.toLowerCase()
			    					+  res.getString("team.playing_record.label.defeat").toLowerCase());
			    mostRecentTitleLabel.setText(res.getString("team.playing_record.mostRecent")
			    					+ venueText.toLowerCase() 
			    					+  res.getString("team.playing_record.label.result").toLowerCase());
		}
            
        }

        int played = record.getPlayed();
        playedLabel.setText(String.valueOf(played));
        
        wonLabel.setText(String.valueOf(record.getWon()));
        wonPercentageLabel.setText("(" + DECIMAL_FORMAT.format(getPercentage(record.getWon(), played)) + "%)");
        
        drawnLabel.setText(String.valueOf(record.getDrawn()));
        drawnPercentageLabel.setText("(" + DECIMAL_FORMAT.format(getPercentage(record.getDrawn(), played)) + "%)");
        
        lostLabel.setText(String.valueOf(record.getLost()));
        lostPercentageLabel.setText("(" + DECIMAL_FORMAT.format(getPercentage(record.getLost(), played)) + "%)");
        
        int gd = record.getGoalDifference();
        gdLabel.setText(gd > 0 ? "+" + gd : String.valueOf(gd));
        gdLabel.setForeground(theme.getGoalDifferenceColour(gd));
        gdDetailsLabel.setText("("+ res.getString("team.playing_record.scored.short")
        		+":" + record.getScored() 
        		+ ", "+ res.getString("team.playing_record.conceded.short")
        		+":" + record.getConceded() + ")");

        scoredAvgLabel.setText(
        		res.getString("team.playing_record.scored.short") + ":" 
        		+ DECIMAL_FORMAT.format((float)record.getScored()/record.getPlayed()));
        
        concededAvgLabel.setText(
        		res.getString("team.playing_record.conceded.short") + ":" 
        		+ DECIMAL_FORMAT.format((float)record.getConceded()/record.getPlayed()));
        
        int points = record.getPoints();
        pointsLabel.setText(String.valueOf(points));
        pointsAverageLabel.setText("("+ res.getString("team.playing_record.average.short") 
        						+ DECIMAL_FORMAT.format(((double) points) / played) + ")");
        
        formLabel.setText(record.getFormRecord().getForm());

        Result bigWin = record.getBiggestWin();
        Result bigDefeat = record.getBiggestDefeat();
        Result mostRecent = record.getLatestResult();
        
        bigWinLabel.setText(resultAsString(team, bigWin));
        bigWinLabel.setForeground(getResultColour(team, bigWin));
        
        bigDefeatLabel.setText(resultAsString(team, bigDefeat));
        bigDefeatLabel.setForeground(getResultColour(team, bigDefeat));
        
        mostRecentLabel.setText(resultAsString(team, mostRecent));
        mostRecentLabel.setForeground(getResultColour(team, mostRecent));
            
        Iterator<String> notes = record.getNotes().iterator();
        for (Label notesLabel : notesLabels)
        {
            notesLabel.setText(notes.hasNext() ? notes.next() : "");
        }
    }
        
        
    private String getVenueText(VenueType venue)
    {
        if (venue == VenueType.HOME)
        {
            return res.getString("team.venue.home");
        }
        else if (venue == VenueType.AWAY)
        {
            return res.getString("team.venue.away");
        }
        return res.getString("team.venue.overall");
    }
            
            
    private String resultAsString(Team team, Result result)
    {
        if (result == null)
        {
            return res.getString("results.not_available");
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append(result.getGoalsFor(team));
        buffer.append('-');
        buffer.append(result.getGoalsAgainst(team));
        if (result.getHomeTeam().equals(team))
        {
            buffer.append(res.getString("results.versus"));
            buffer.append(result.getAwayTeam().getName());
        }
        else
        {
            buffer.append(res.getString("results.at"));
            buffer.append(result.getHomeTeam().getName());
        }
        return buffer.toString();
    }
        
        
    private Color getResultColour(Team team, Result result)
    {
        if (result == null)
        {
            return theme.getMainViewTextColour();
        }
        else if (result.isWin(team))
        {
            return theme.getWinColour();
        }
        else if (result.isDefeat(team))
        {
            return theme.getDefeatColour();
        }
        return theme.getDrawColour();
    }
        
        
    private String getSuffix(int pos)
    {
        String suffix = "th";
        if (pos < 10 || pos > 20)
        {
            switch (pos % 10)
            {
                case 1:
                {
                    suffix = "st";
                    break;
                }
                case 2:
                {
                    suffix = "nd";
                    break;
                }
                case 3:
                {
                    suffix = "rd";
                    break;
                }
                default: suffix = "th";
            }
        }
        return suffix;
    }

        
    private double getPercentage(int numerator, int denominator)
    {
        return denominator == 0 ? 0 : (((double) numerator) / denominator) * 100;
    }
}
