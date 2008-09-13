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
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.VenueType;

/**
 * AWT panel for displaying head-to-head comparisons for a pair of teams.
 * @author Daniel Dyer
 * @since 1/2/2004
 */
public class HeadToHead implements StatsPanel
{
    private LeagueSeason data = null;

    private final TeamRecordPanel homeTeamPanel;
    private final TeamRecordPanel awayTeamPanel;
    private final Choice homeTeamChoice = new Choice();
    private final Choice awayTeamChoice = new Choice();
    private final Choice typeChoice = new Choice();
    
    private Panel controls = null;
    private Panel view = null;
    private ResourceBundle res = null;
    
    
    
    public HeadToHead(ResourceBundle res) {
		this.res = res;
		homeTeamPanel = new TeamRecordPanel(res);
		awayTeamPanel = new TeamRecordPanel(res);
		
	}


	public void setLeagueData(LeagueSeason data, String highlightedTeam)
    {
        this.data = data;
        
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
            
            innerPanel.add(new Label(res.getString("head2head.homeTeam")));
            innerPanel.add(homeTeamChoice);
            innerPanel.add(new Label(res.getString("head2head.awayTeam")));
            innerPanel.add(awayTeamChoice);
            innerPanel.add(new Label(res.getString("head2head.comparisonType")));
            typeChoice.add(res.getString("head2head.comparisonType.overall"));
            typeChoice.add(res.getString("head2head.comparisonType.home_away"));
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
            homeTeamPanel.setTeam(data.getTeam(homeTeamChoice.getSelectedItem()), overall ? VenueType.BOTH : VenueType.HOME);
            awayTeamPanel.setTeam(data.getTeam(awayTeamChoice.getSelectedItem()), overall ? VenueType.BOTH : VenueType.AWAY);
            view.validate();
        }
    }
}