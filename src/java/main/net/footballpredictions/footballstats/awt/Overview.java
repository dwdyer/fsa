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
package net.footballpredictions.footballstats.awt;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.util.ResourceBundle;
import java.util.SortedSet;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.Result;

/**
 * AWT panel for displaying an overview of a league season.
 * @author Daniel Dyer
 * @since 4/1/2004
 */
public class Overview implements StatsPanel
{
    private LeagueSeason data = null;
    private Theme theme = null;
    private String highlightedTeam = null;
    
    private Panel view = null;
    private Label matchesLabel, homeWinsLabel, homeWinsPercentLabel, awayWinsLabel, awayWinsPercentLabel, drawsLabel,
                  drawsPercentLabel, drawsBreakdownLabel, goalsLabel, goalsAverageLabel, goalsBreakdownLabel, cleansheetsLabel,
                  aggregateLabel, averageLabel;
    private Panel homeWinsPanel, awayWinsPanel, aggregatesPanel;
    
    private ResourceBundle res = null;
    
    
    public Overview(ResourceBundle res) {
		this.res = res;
	}


	public void setLeagueData(LeagueSeason data, String highlightedTeam)
    {
        this.data = data;
        this.highlightedTeam = highlightedTeam;
        if (view != null)
        {
            updateView();
        }
    }
    
    
    public void setTheme(Theme theme)
    {
        this.theme = theme;
    }
    
    
    public Component getControls()
    {
        return null;
    }
    
    
    public Component getView()
    {
        if (view == null)
        {
            view = new Panel(new BorderLayout(20, 0));
            view.add(createTotalsPanel(), BorderLayout.WEST);
            Panel innerPanel = new Panel(new GridLayout(3, 1));
            homeWinsPanel = new Panel(new BorderLayout());
            Label homeWinsLabel = new Label(res.getString("overview.biggest_home_win"));
            homeWinsLabel.setFont(theme.getBoldFont());
            homeWinsPanel.add(homeWinsLabel, BorderLayout.NORTH);
            awayWinsPanel = new Panel(new BorderLayout());
            Label awayWinsLabel = new Label(res.getString("overview.biggest_away_win"));
            awayWinsLabel.setFont(theme.getBoldFont());
            awayWinsPanel.add(awayWinsLabel, BorderLayout.NORTH);
            aggregatesPanel = new Panel(new BorderLayout());
            Label aggregatesLabel = new Label(res.getString("overview.highest_match_agregates"));
            aggregatesLabel.setFont(theme.getBoldFont());
            aggregatesPanel.add(aggregatesLabel, BorderLayout.NORTH);
            innerPanel.add(homeWinsPanel);
            innerPanel.add(awayWinsPanel);
            innerPanel.add(aggregatesPanel);            
            view.add(Util.borderLayoutWrapper(innerPanel, BorderLayout.NORTH), BorderLayout.CENTER);
            updateView();
        }
        return view;
    }
    
    
    private Component createTotalsPanel()
    {
        Panel innerPanel = new Panel(new GridBagLayout());
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.weightx = 1.0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.gridwidth = 1;
        GridBagConstraints valueConstraints = (GridBagConstraints) labelConstraints.clone();
        valueConstraints.gridwidth = GridBagConstraints.RELATIVE;
        GridBagConstraints extraConstraints = (GridBagConstraints) labelConstraints.clone();
        extraConstraints.gridwidth = GridBagConstraints.REMAINDER;        
        extraConstraints.weightx = 0.1;
        
        Label totalsLabel = new Label(res.getString("overview.totals.label"));
        totalsLabel.setFont(theme.getBoldFont());
        innerPanel.add(totalsLabel, extraConstraints);
        innerPanel.add(new Label(res.getString("overview.matches_played")), labelConstraints);
        matchesLabel = new Label();
        matchesLabel.setFont(theme.getBoldFont());
        innerPanel.add(matchesLabel, extraConstraints);
        innerPanel.add(new Label(res.getString("overview.home_wins")), labelConstraints);
        homeWinsLabel = new Label();
        homeWinsLabel.setFont(theme.getBoldFont());
        innerPanel.add(homeWinsLabel, valueConstraints);
        homeWinsPercentLabel = new Label();
        innerPanel.add(homeWinsPercentLabel, extraConstraints);
        innerPanel.add(new Label(res.getString("overview.away_wins")), labelConstraints);
        awayWinsLabel = new Label();
        awayWinsLabel.setFont(theme.getBoldFont());
        innerPanel.add(awayWinsLabel, valueConstraints);
        awayWinsPercentLabel = new Label();
        innerPanel.add(awayWinsPercentLabel, extraConstraints);
        innerPanel.add(new Label(res.getString("overview.draws")), labelConstraints);
        drawsLabel = new Label();
        drawsLabel.setFont(theme.getBoldFont());
        innerPanel.add(drawsLabel, valueConstraints);
        drawsPercentLabel = new Label();
        innerPanel.add(drawsPercentLabel, extraConstraints);
        drawsBreakdownLabel = new Label();
        drawsBreakdownLabel.setFont(theme.getSmallFont());
        innerPanel.add(drawsBreakdownLabel, extraConstraints);
        innerPanel.add(new Label(res.getString("overview.goal_scored")), labelConstraints);
        goalsLabel = new Label();
        goalsLabel.setFont(theme.getBoldFont());
        innerPanel.add(goalsLabel, valueConstraints);
        goalsAverageLabel = new Label();
        innerPanel.add(goalsAverageLabel, extraConstraints);
        goalsBreakdownLabel = new Label();
        goalsBreakdownLabel.setFont(theme.getSmallFont());
        innerPanel.add(goalsBreakdownLabel, extraConstraints);
        innerPanel.add(new Label(res.getString("overview.clean_sheets")), labelConstraints);
        cleansheetsLabel = new Label();
        cleansheetsLabel.setFont(theme.getBoldFont());
        innerPanel.add(cleansheetsLabel, extraConstraints);
        innerPanel.add(new Label(), extraConstraints); // Blank Row
        Label attendancesLabel = new Label(res.getString("fsa.panel.attendances"));
        attendancesLabel.setFont(theme.getBoldFont());
        innerPanel.add(attendancesLabel, extraConstraints);
        innerPanel.add(new Label(res.getString("overview.att.aggregate")), labelConstraints);
        aggregateLabel = new Label();
        aggregateLabel.setFont(theme.getBoldFont());
        innerPanel.add(aggregateLabel, extraConstraints);
        innerPanel.add(new Label(res.getString("overview.att.average")), labelConstraints);
        averageLabel = new Label();
        averageLabel.setFont(theme.getBoldFont());
        innerPanel.add(averageLabel, extraConstraints);        
        return Util.borderLayoutWrapper(innerPanel, BorderLayout.NORTH);
    }
    
    
    private void updateView()
    {
        if (data != null)
        {
            matchesLabel.setText(String.valueOf(data.getMatchCount()));
            homeWinsLabel.setText(String.valueOf(data.getHomeWins()));
            homeWinsPercentLabel.setText("(" + theme.getDecimalFormat().format(getPercentage(data.getHomeWins(), data.getMatchCount())) + "%)");
            awayWinsLabel.setText(String.valueOf(data.getAwayWins()));
            awayWinsPercentLabel.setText("(" + theme.getDecimalFormat().format(getPercentage(data.getAwayWins(), data.getMatchCount())) + "%)");
            drawsLabel.setText(String.valueOf(data.getScoreDraws() + data.getNoScoreDraws()));
            drawsPercentLabel.setText("(" + theme.getDecimalFormat().format(getPercentage(data.getScoreDraws() + data.getNoScoreDraws(), data.getMatchCount())) + "%)");
            drawsBreakdownLabel.setText("(Scoring Draws: " + data.getScoreDraws() + ", Goalless Draws: " + data.getNoScoreDraws() + ")");
            goalsLabel.setText(String.valueOf(data.getHomeGoals() + data.getAwayGoals()));
            goalsAverageLabel.setText("(" + theme.getDecimalFormat().format(((double) data.getHomeGoals() + data.getAwayGoals()) / data.getMatchCount()) + " per game)");
            goalsBreakdownLabel.setText("("+
            		res.getString("overview.home_goals") + data.getHomeGoals() +", "+ 
            		res.getString("overview.away_goals") + data.getAwayGoals() + ")");
            cleansheetsLabel.setText(String.valueOf(data.getCleansheets()));
            aggregateLabel.setText(String.valueOf(data.getAggregateAttendance()));
            averageLabel.setText(String.valueOf(data.getAverageAttendance()));
            doResultsPanel(data.getBiggestHomeWins(), homeWinsPanel);
            doResultsPanel(data.getBiggestAwayWins(), awayWinsPanel);
            doResultsPanel(data.getHighestMatchAggregates(), aggregatesPanel);
            view.validate();
        }
    }
    
    
    private void doResultsPanel(SortedSet<Result> results, Panel panel)
    {
        if (panel.getComponentCount() > 1)
        {
            panel.remove(1);
        }
        Panel innerPanel = new Panel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        for (Result result : results)
        {
            constraints.gridwidth = 1;
            innerPanel.add(new Label(theme.getShortDateFormat().format(result.getDate())), constraints);
            Label homeTeamLabel = new Label(result.getHomeTeam());
            homeTeamLabel.setFont(result.getHomeTeam().equals(highlightedTeam) ? theme.getBoldFont() : theme.getPlainFont());
            innerPanel.add(homeTeamLabel, constraints);
            constraints.gridwidth = GridBagConstraints.RELATIVE;
            Label scoreLabel = new Label(result.getHomeGoals() + "-" + result.getAwayGoals(), Label.CENTER);
            scoreLabel.setFont(theme.getBoldFont());
            innerPanel.add(scoreLabel, constraints);
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            Label awayTeamLabel = new Label(result.getAwayTeam());
            awayTeamLabel.setFont(result.getAwayTeam().equals(highlightedTeam) ? theme.getBoldFont() : theme.getPlainFont());
            innerPanel.add(awayTeamLabel, constraints);
        }
        panel.add(Util.borderLayoutWrapper(innerPanel, BorderLayout.NORTH), BorderLayout.CENTER);
    }
    
    
    private double getPercentage(int numerator, int denominator)
    {
        return denominator == 0 ? 0 : (((double) numerator) / denominator) * 100;
    }
}