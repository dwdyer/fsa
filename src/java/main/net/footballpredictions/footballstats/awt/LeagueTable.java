// $Header: $
package net.footballpredictions.footballstats.awt;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.SortedSet;
import net.footballpredictions.footballstats.model.FormRecord;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.StandardRecord;
import net.footballpredictions.footballstats.model.TeamRecord;
import net.footballpredictions.footballstats.model.VenueType;

/**
 * @author Daniel Dyer
 * @since 27/12/2003
 * @version $Revision: $
 */
public class LeagueTable implements StatsPanel
{
    private final boolean isFormTable;
    
    private final Choice tableTypeChoice = new Choice();
    private final Choice matchesChoice = new Choice();
    private final Checkbox zonesCheckbox = new Checkbox("Show Zones");
    private final Label titleLabel = new Label();
    private Label[] zoneTitleLabels;
    
    private LeagueSeason data = null;
    private Theme theme = null;
    private String highlightedTeam = null;

    
    private Panel controls = null;
    private Panel innerControlPanel = new Panel();
    private Panel view = null;
    private Panel tablePanel = null;
    private Panel positionsColumn = new Panel(new GridLayout(0, 1));
    private Panel teamsColumn = new Panel(new GridLayout(0, 1));
    private Panel statsColumns = new Panel(new GridLayout(0, 8));
    private Panel optionalColumn = new Panel(new GridLayout(0, 1));
    
    
    public LeagueTable(boolean isFormTable)
    {
        this.isFormTable = isFormTable;
        zonesCheckbox.setState(!isFormTable);        
    }
    
    
    public void setLeagueData(LeagueSeason data, String highlightedTeam)
    {
        this.data = data;
        this.highlightedTeam = highlightedTeam;
        
        // Reset GUI.
        positionsColumn = null;
        
        if (view != null)
        {
            updateControls();
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
            ItemListener controlListener = new ItemListener()
            {
                public void itemStateChanged(ItemEvent ev)
                {
                    if (ev.getSource() == zonesCheckbox)
                    {
                        positionsColumn = null;
                        for (Label zoneTitleLabel : zoneTitleLabels)
                        {
                            zoneTitleLabel.setVisible(zonesCheckbox.getState());
                        }
                    }
                    updateView();
                }
            };
            tableTypeChoice.addItemListener(controlListener);
            matchesChoice.addItemListener(controlListener);
            zonesCheckbox.addItemListener(controlListener);

            tableTypeChoice.add("Points Won");
            tableTypeChoice.add("Average Points");
            tableTypeChoice.add("Points Dropped");            
            tableTypeChoice.setForeground(Color.black);
            matchesChoice.add("Home & Away");
            matchesChoice.add("Home Only");
            matchesChoice.add("Away Only");
            matchesChoice.setForeground(Color.black);

            controls = Util.borderLayoutWrapper(innerControlPanel, BorderLayout.NORTH);
            updateControls();
        }
        return controls;
    }
    
    
    private void updateControls()
    {
        innerControlPanel.removeAll();
        
        String[] prizeZoneNames = data.getPrizeZoneNames();
        String[] relegationZoneNames = data.getRelegationZoneNames();
        if (isFormTable)
        {
            innerControlPanel.setLayout(new GridLayout(5 + prizeZoneNames.length + relegationZoneNames.length, 1));
        }
        else
        {
            innerControlPanel.setLayout(new GridLayout(7 + prizeZoneNames.length + relegationZoneNames.length, 1));
            innerControlPanel.add(new Label("Table Type:"));
            innerControlPanel.add(tableTypeChoice);            
        }
            
        innerControlPanel.add(new Label("Matches:"));
        innerControlPanel.add(matchesChoice);
            
        innerControlPanel.add(new Label()); // Blank line.
        innerControlPanel.add(zonesCheckbox);

        zoneTitleLabels = new Label[prizeZoneNames.length + relegationZoneNames.length];
        zonesCheckbox.setEnabled(zoneTitleLabels.length > 0);
        for (int i = 0; i < prizeZoneNames.length; i++)
        {
            Label label = new Label(prizeZoneNames[i]);
            label.setForeground(theme.getMainViewTextColour());
            label.setBackground(theme.getZoneColour(i + 1));
            label.setVisible(zonesCheckbox.getState());
            zoneTitleLabels[i] = label;
            innerControlPanel.add(label);
        }
            
        for (int i = 0; i < relegationZoneNames.length; i++)
        {
            Label label = new Label(relegationZoneNames[i]);
            label.setForeground(theme.getMainViewTextColour());
            label.setBackground(theme.getZoneColour(0 - (i + 1)));
            label.setVisible(zonesCheckbox.getState());
            zoneTitleLabels[i + prizeZoneNames.length] = label;
            innerControlPanel.add(label);
        }
        controls.validate();
    }
    
    
    public Component getView()
    {
        if (view == null)
        {
            view = new Panel(new BorderLayout());
            tablePanel = new Panel(new GridBagLayout());
            titleLabel.setFont(theme.getTitleFont());
            titleLabel.setAlignment(Label.CENTER);
            view.add(titleLabel, BorderLayout.NORTH);
            view.add(Util.wrapTable(tablePanel), BorderLayout.CENTER);
            updateView();
        }
        return view;
    }
    
    
    @SuppressWarnings("unchecked")
    private void updateView()
    {
        tablePanel.removeAll();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.NORTH;
        
        int index = matchesChoice.getSelectedIndex();
        VenueType where = (index <= 0 ? VenueType.BOTH : (index == 1 ? VenueType.HOME : VenueType.AWAY));
        
        StringBuffer titleText = new StringBuffer();
        SortedSet<? extends TeamRecord> table;
        
        boolean showOptionalColumn = true;
        if (isFormTable)
        {
            titleText.append("Form Table");
            if (where == VenueType.HOME)
            {
                titleText.append(" (Last 4 Home Matches)");
            }
            else if (where == VenueType.AWAY)
            {
                titleText.append(" (Last 4 Away Matches)");
            }   
            else
            {
                titleText.append(" (Last 6 Matches)");
            }
            
            table = data.getFormTable(where);
        }
        else
        {
            index = tableTypeChoice.getSelectedIndex();
            if (index <= 0)
            {
                titleText.append("League Table");
                table = data.getStandardLeagueTable(where);
                showOptionalColumn = false;
            }
            else if (index == 1)
            {
                titleText.append("Average Points Per Game");
                table = data.getAverageLeagueTable(where);
            }
            else
            {
                titleText.append("Total Points Dropped");
                table = data.getInvertedLeagueTable(where);
            }
            
            if (where == VenueType.HOME)
            {
                titleText.append(" (Home Matches Only)");
            }
            else if (where == VenueType.AWAY)
            {
                titleText.append(" (Away Matches Only)");
            }            
        }
        titleText.append(" - ");
        titleText.append(theme.getLongDateFormat().format(data.getMostRecentDate()));
        
        tablePanel.add(getPositionsColumn(), constraints);
        constraints.weightx = 4.0;
        constraints.gridwidth = showOptionalColumn ? 1 : GridBagConstraints.RELATIVE;
        tablePanel.add(getTeamsColumn(table), constraints);
        constraints.weightx = 0.0;
        constraints.gridwidth = showOptionalColumn ? GridBagConstraints.RELATIVE : GridBagConstraints.REMAINDER;
        tablePanel.add(getStatsColumns(table, where, !showOptionalColumn), constraints);
        constraints.weightx = 1.0;
        
        if (isFormTable)
        {
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            tablePanel.add(getFormColumn((SortedSet<FormRecord>) table), constraints);
        }
        else
        {
            if (index == 1)
            {
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                tablePanel.add(getAverageColumn((SortedSet<StandardRecord>) table), constraints);
            }
            else if (index == 2)
            {
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                tablePanel.add(getDroppedColumn((SortedSet<StandardRecord>) table), constraints);
            }
        }
        
        titleLabel.setText(titleText.toString());
        view.validate();
    }
    
    
    private Component getPositionsColumn()
    {
        if (positionsColumn == null)
        {
            int teamCount = data.getTeamNames().size();
            positionsColumn = new Panel(new GridLayout(teamCount + 1, 1));
            positionsColumn.add(new Label()); // Blank space at the top.
            for (int i = 1; i <= teamCount; i++)
            {
                Label label = new Label(String.valueOf(i));
                if (zonesCheckbox.getState())
                {
                    label.setBackground(theme.getZoneColour(data.getZoneForPosition(i)));
                }
                positionsColumn.add(label);
            }
        }
        return positionsColumn;
    }
    
    
    private Component getTeamsColumn(SortedSet<? extends TeamRecord> teams)
    {
        teamsColumn.removeAll();
        teamsColumn.add(new Label()); // Blank space at the top.
        int index = 1;
        for (TeamRecord team : teams)
        {
            Label label = new Label(team.getName());
            if (team.getName().equals(highlightedTeam))
            {
                label.setFont(theme.getBoldFont());
            }
            if (zonesCheckbox.getState())
            {
                label.setBackground(theme.getZoneColour(data.getZoneForPosition(index)));
            }
            teamsColumn.add(label);
            ++index;
        }
        return teamsColumn;
    }
    
    
    private Component getStatsColumns(SortedSet<? extends TeamRecord> teams,
                                      VenueType where,
                                      boolean highlightPoints)
    {
        statsColumns.removeAll();
        statsColumns.add(new Label("P", Label.CENTER));
        statsColumns.add(new Label("W", Label.CENTER));
        statsColumns.add(new Label("D", Label.CENTER));
        statsColumns.add(new Label("L", Label.CENTER));
        statsColumns.add(new Label("F", Label.CENTER));
        statsColumns.add(new Label("A", Label.CENTER));
        statsColumns.add(new Label("GD", Label.CENTER));
        statsColumns.add(new Label("Pts", Label.CENTER));
        int index = 1;
        for (TeamRecord team : teams)
        {
            Color backgroundColour = theme.getZoneColour(data.getZoneForPosition(index));
            Label playedLabel = new Label(String.valueOf(team.getPlayed()), Label.CENTER);
            statsColumns.add(playedLabel);
            Label wonLabel = new Label(String.valueOf(team.getWon()), Label.CENTER);
            statsColumns.add(wonLabel);
            Label drawnLabel = new Label(String.valueOf(team.getDrawn()), Label.CENTER);
            statsColumns.add(drawnLabel);
            Label lostLabel = new Label(String.valueOf(team.getLost()), Label.CENTER);
            statsColumns.add(lostLabel);
            Label forLabel = new Label(String.valueOf(team.getScored()), Label.CENTER);
            statsColumns.add(forLabel);
            Label againstLabel = new Label(String.valueOf(team.getConceded()), Label.CENTER);
            statsColumns.add(againstLabel);
            int goalDifference = team.getGoalDifference();
            Label goalDifferenceLabel = new Label(goalDifferenceAsString(goalDifference), Label.CENTER);
            goalDifferenceLabel.setForeground(theme.getGoalDifferenceColour(goalDifference));
            statsColumns.add(goalDifferenceLabel);
            Label pointsLabel = new Label(String.valueOf(team.getPoints()), Label.CENTER);
            if (highlightPoints)
            {
                pointsLabel.setFont(theme.getBoldFont());
            }
            statsColumns.add(pointsLabel);
            
            if (zonesCheckbox.getState())
            {
                playedLabel.setBackground(backgroundColour);
                wonLabel.setBackground(backgroundColour);
                drawnLabel.setBackground(backgroundColour);
                lostLabel.setBackground(backgroundColour);
                forLabel.setBackground(backgroundColour);
                againstLabel.setBackground(backgroundColour);
                goalDifferenceLabel.setBackground(backgroundColour);
                pointsLabel.setBackground(backgroundColour);
            }
            ++index;
        }
        return statsColumns;
    }
    
    
    private Component getAverageColumn(SortedSet<StandardRecord> teams)
    {
        optionalColumn.removeAll();
        optionalColumn.add(new Label("Pts/P", Label.CENTER));
        int index = 1;
        for (StandardRecord team : teams)
        {
            Label label = new Label(theme.getDecimalFormat().format(team.getAveragePoints()), Label.CENTER);
            label.setFont(theme.getBoldFont());
            if (zonesCheckbox.getState())
            {
                label.setBackground(theme.getZoneColour(data.getZoneForPosition(index)));
            }
            optionalColumn.add(label);
            ++index;
        }
        return optionalColumn;
    }
    
    
    private Component getDroppedColumn(SortedSet<StandardRecord> teams)
    {
        optionalColumn.removeAll();
        optionalColumn.add(new Label("Dropped", Label.CENTER));
        int index = 1;
        for (StandardRecord team : teams)
        {
            Label label = new Label(String.valueOf(team.getDroppedPoints()), Label.CENTER);
            label.setFont(theme.getBoldFont());
            if (zonesCheckbox.getState())
            {
                label.setBackground(theme.getZoneColour(data.getZoneForPosition(index)));
            }
            optionalColumn.add(label);
            ++index;
        }
        return optionalColumn;
    }
    
    
    private Component getFormColumn(SortedSet<FormRecord> teams)
    {
        optionalColumn.removeAll();
        optionalColumn.add(new Label("Form", Label.CENTER));
        int index = 1;
        for (FormRecord team : teams)
        {
            Label label = new Label(team.getForm(), Label.CENTER);
            label.setFont(theme.getFixedWidthFont());
            if (zonesCheckbox.getState())
            {
                label.setBackground(theme.getZoneColour(data.getZoneForPosition(index)));
            }
            optionalColumn.add(label);
            ++index;
        }
        return optionalColumn;
    }
    
    
    /**
     * Helper method to prefix goal difference values with a sign.
     */
    private String goalDifferenceAsString(int value)
    {
        return value <= 0 ? String.valueOf(value) : "+" + value;
    }
}