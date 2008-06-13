// $Header: $
package net.footballpredictions.footballstats.awt;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.TeamRecord;

/**
 * @author Daniel Dyer
 * @since 1/2/2004
 * @version $Revision: $
 */
public class HeadToHead implements StatsPanel
{
    private LeagueSeason data = null;

    private final TeamRecordPanel homeTeamPanel = new TeamRecordPanel();
    private final TeamRecordPanel awayTeamPanel = new TeamRecordPanel();
    private final Choice homeTeamChoice = new Choice();
    private final Choice awayTeamChoice = new Choice();
    private final Choice typeChoice = new Choice();
    
    private Panel controls = null;
    private Panel view = null;
    
    public void setLeagueData(LeagueSeason data, String highlightedTeam)
    {
        this.data = data;
        homeTeamPanel.setLeagueData(data);
        awayTeamPanel.setLeagueData(data);
        
        homeTeamChoice.removeAll();
        awayTeamChoice.removeAll();

        for (String teamName : data.getTeamNames())
        {
            homeTeamChoice.add(teamName);
            awayTeamChoice.add(teamName);
        }

        if (homeTeamChoice.getItem(0).equals(highlightedTeam) || highlightedTeam == null)
        {
            awayTeamChoice.select(1);
        }
        else
        {
            homeTeamChoice.select(highlightedTeam);
        }
        

        if (view != null)
        {
            updateView();
        }
    }
    
    
    public void setTheme(Theme theme)
    {
        homeTeamPanel.setTheme(theme);
        awayTeamPanel.setTheme(theme);
    }
    
    
    public Component getControls()
    {
        if (controls == null)
        {
            controls = new Panel(new BorderLayout());
            Panel innerPanel = new Panel(new GridLayout(6, 1));
            
            innerPanel.add(new Label("Home Team:"));
            innerPanel.add(homeTeamChoice);
            innerPanel.add(new Label("Away Team:"));
            innerPanel.add(awayTeamChoice);
            innerPanel.add(new Label("Comparison Type:"));
            typeChoice.add("Overall");
            typeChoice.add("Home/Away");
            innerPanel.add(typeChoice);
            
            ItemListener itemListener = new ItemListener()
            {
                public void itemStateChanged(ItemEvent ev)
                {
                    updateView();
                }
            };
            homeTeamChoice.addItemListener(itemListener);
            awayTeamChoice.addItemListener(itemListener);
            typeChoice.addItemListener(itemListener);
            
            homeTeamChoice.setForeground(Color.black);
            awayTeamChoice.setForeground(Color.black);
            typeChoice.setForeground(Color.black);
            
            controls.add(innerPanel, BorderLayout.NORTH);
        }
        return controls;
    }
    
    
    public Component getView()
    {
        if (view == null)
        {
            view = new Panel(new GridLayout(1, 2, 10, 0));
            view.add(homeTeamPanel);
            view.add(awayTeamPanel);
            updateView();
        }
        return view;
    }
    
    
    private void updateView()
    {
        if (data != null)
        {
            boolean overall = typeChoice.getSelectedIndex() <= 0;
            homeTeamPanel.setTeam(data.getTeam(homeTeamChoice.getSelectedItem()), overall ? TeamRecord.BOTH : TeamRecord.HOME);
            awayTeamPanel.setTeam(data.getTeam(awayTeamChoice.getSelectedItem()), overall ? TeamRecord.BOTH : TeamRecord.AWAY);
            view.validate();
        }
    }
}