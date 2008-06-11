// $Header: $
package net.footballpredictions.footballstats.awt;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.util.Date;
import java.util.Collection;
import java.util.List;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.Result;
import net.footballpredictions.footballstats.model.Team;

/**
 * @author Daniel Dyer
 * @since 28/12/2003
 * @version $Revision: $
 */
public class Results implements StatsPanel
{
    private LeagueSeason data = null;
    private Theme theme = null;
    private String highlightedTeam = null;
    
    private final Choice teamChoice = new Choice();
    private final Choice dateChoice = new Choice();
    private final Choice matchesChoice = new Choice();
    private final CheckboxGroup optionGroup = new CheckboxGroup();
    private final Checkbox byDateCheckbox = new Checkbox("By Date", optionGroup, true);
    private final Checkbox byTeamCheckbox = new Checkbox("By Team", optionGroup, false);
    private final Label titleLabel = new Label();
    
    private Panel controls = null;
    private Panel view = null;
    private Panel resultsPanel = null;
    
    public Results()
    {
        matchesChoice.add("Home & Away");
        matchesChoice.add("Home Only");
        matchesChoice.add("Away Only");
    }

    
    public void setLeagueData(LeagueSeason data, String highlightedTeam)
    {
        this.data = data;
        this.highlightedTeam = highlightedTeam;
        
        teamChoice.removeAll();
        for (String teamName : data.getTeamNames())
        {
            teamChoice.add(teamName);
        }

        if (highlightedTeam != null)
        {
            teamChoice.select(highlightedTeam);
            byTeamCheckbox.setState(true);
        }
        
        dateChoice.removeAll();
        Collection<Date> dates = data.getDates();
        for (Date date : dates)
        {
            dateChoice.add(theme.getLongDateFormat().format(date));
        }
        
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
        if (controls == null)
        {
            Panel innerPanel = new Panel(new GridLayout(6, 1));
            
            ItemListener checkboxListener = new ItemListener()
            {
                public void itemStateChanged(ItemEvent ev)
                {
                    updateView();
                }
            };
            byDateCheckbox.addItemListener(checkboxListener);
            byTeamCheckbox.addItemListener(checkboxListener);
            
            ItemListener dropDownListener = new ItemListener()
            {
                public void itemStateChanged(ItemEvent ev)
                {
                    updateView();
                }
            };

            dateChoice.addItemListener(dropDownListener);
            teamChoice.addItemListener(dropDownListener);
            matchesChoice.addItemListener(dropDownListener);

            innerPanel.add(byDateCheckbox);
            innerPanel.add(dateChoice);
            innerPanel.add(new Label()); // Blank space.            
            innerPanel.add(byTeamCheckbox);
            innerPanel.add(teamChoice);
            innerPanel.add(matchesChoice);
            
            dateChoice.setForeground(Color.black);
            teamChoice.setForeground(Color.black);
            matchesChoice.setForeground(Color.black);
            
            controls = Util.borderLayoutWrapper(innerPanel, BorderLayout.NORTH);
        }
        return controls;
    }
    
    
    public Component getView()
    {
        if (view == null)
        {
            view = new Panel(new BorderLayout());
            ScrollPane scroller = new ScrollPane();
            resultsPanel = new Panel(new GridBagLayout());
            Panel outerPanel = Util.borderLayoutWrapper(resultsPanel, BorderLayout.NORTH);
            scroller.add(outerPanel);
            
            titleLabel.setFont(theme.getTitleFont());
            titleLabel.setAlignment(Label.CENTER);
            
            view.add(titleLabel, BorderLayout.NORTH);
            view.add(scroller, BorderLayout.CENTER);

            updateView();
        }
        return view;
    }
    
    
    private void updateView()
    {
        if (data != null)
        {
            resultsPanel.removeAll();
            
            if (byDateCheckbox.getState())
            {
                try
                {
                    showResults(theme.getLongDateFormat().parse(dateChoice.getSelectedItem()));
                }
                catch (ParseException ex)
                {
                    // Cannot happen, this is a closed system, all strings in the drop-down are valid dates.
                }
            }
            else
            {
                showResults(data.getTeam(teamChoice.getSelectedItem()));
            }
            teamChoice.setEnabled(byTeamCheckbox.getState());
            matchesChoice.setEnabled(byTeamCheckbox.getState());
            dateChoice.setEnabled(byDateCheckbox.getState());
        }
    }
    
    
    /**
     * Show the specified teams results for the season.
     */
    private void showResults(Team team)
    {
        StringBuffer titleText = new StringBuffer("Results for ");
        titleText.append(team.getName());

        Result[] results;
        int index = matchesChoice.getSelectedIndex();
        if (index == 0)
        {
            results = team.getResults(Team.BOTH);
        }
        else if (index == 1)
        {
            results = team.getResults(Team.HOME);
            titleText.append(" (Home Matches Only)");
        }
        else
        {
            results = team.getResults(Team.AWAY);
            titleText.append(" (Away Matches Only)");
        }        
        
        titleLabel.setText(titleText.toString());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        for (Result result : results)
        {
            constraints.gridwidth = 1;
            resultsPanel.add(new Label(theme.getLongDateFormat().format(result.getDate())), constraints);
            if (result.getHomeTeam().equals(team)) // Home
            {
                resultsPanel.add(new Label(result.getAwayTeam().getName() + " (H)"), constraints);
            }
            else // Away
            {
                resultsPanel.add(new Label(result.getHomeTeam().getName() + " (A)"), constraints);
            }
            constraints.gridwidth = GridBagConstraints.RELATIVE;
            constraints.weightx = 0.0;
            Label scoreLabel = new Label(result.getGoalsFor(team) + "-" + result.getGoalsAgainst(team), Label.CENTER);
            scoreLabel.setFont(theme.getBoldFont());
            if (result.isDraw())
            {
                scoreLabel.setBackground(theme.getDrawColour());
            }
            else
            {
                scoreLabel.setBackground(result.isWin(team) ? theme.getWinColour() : theme.getDefeatColour());
            }
            resultsPanel.add(scoreLabel, constraints);
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.weightx = 1.0;
            resultsPanel.add(new Label(result.getAttendance() >= 0 ? String.valueOf(result.getAttendance()) : "N/A", Label.RIGHT),
                             constraints);
        }

        view.validate();
    }
    
    
    /**
     * Show all results from the specified date.
     */
    private void showResults(Date date)
    {
        List<Result> results = data.getResults(date);
        
        titleLabel.setText("Results for " + theme.getLongDateFormat().format(date));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weighty = 1.0;
        
        for (Result result : results)
        {
            constraints.gridwidth = 1;
            constraints.weightx = 1.0;
            Label homeTeamLabel = new Label(result.getHomeTeam().getName(), Label.RIGHT);
            homeTeamLabel.setFont(result.getHomeTeam().getName().equals(highlightedTeam) ? theme.getBoldFont() : theme.getPlainFont());
            resultsPanel.add(homeTeamLabel, constraints);
            Label scoreLabel = new Label(result.getHomeGoals() + "-" + result.getAwayGoals(), Label.CENTER);
            scoreLabel.setFont(theme.getBoldFont());
            // constraints.weightx = 0.0;
            resultsPanel.add(scoreLabel, constraints);
            constraints.gridwidth = GridBagConstraints.RELATIVE;
            // constraints.weightx = 1.0;
            Label awayTeamLabel = new Label(result.getAwayTeam().getName(), Label.LEFT);
            awayTeamLabel.setFont(result.getAwayTeam().getName().equals(highlightedTeam) ? theme.getBoldFont() : theme.getPlainFont());
            resultsPanel.add(awayTeamLabel, constraints);
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.weightx = 0.0;
            resultsPanel.add(new Label(result.getAttendance() >= 0 ? String.valueOf(result.getAttendance()) : "N/A", Label.RIGHT), constraints);
        }
        
        view.validate();
    }
}