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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.SortedSet;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.Team;
import net.footballpredictions.footballstats.model.TeamRecord;
import net.footballpredictions.footballstats.model.StandardRecord;

/**
 * @author Daniel Dyer
 * @since 4/1/2004
 * @version $Revision: $
 */
public class Sequences implements StatsPanel
{
    private static final String[] SEQUENCE_NAMES = new String[]{"Wins",
                                                                "Draws",
                                                                "Defeats",
                                                                "Games Without Defeat",
                                                                "Games Without Winning",
                                                                "Cleansheets",
                                                                "Games Scored In",
                                                                "Games Without Scoring"};
    
    private LeagueSeason data = null;
    private Theme theme = null;
    private String highlightedTeam = null;
    
    private final Choice sequenceChoice = new Choice();
    private final Choice matchesChoice = new Choice();
    private final Label titleLabel = new Label();
    private final Panel positionsColumn = new Panel(new GridLayout(0, 1));
    private final Panel teamsColumn = new Panel(new GridLayout(0, 1));
    private final Panel sequenceColumn = new Panel(new GridLayout(0, 1));
    
    private Checkbox seasonCheckbox;
    private Panel controls = null;
    private Panel view = null;
    private Label[][] labels = null;

    
    public void setLeagueData(LeagueSeason data, String highlightedTeam)
    {
        this.data = data;
        this.highlightedTeam = highlightedTeam;
        labels = null;
        
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
            Panel innerPanel = new Panel(new GridLayout(5, 1));
            
            innerPanel.add(new Label("Sequence Type:"));
            sequenceChoice.add("Wins");
            sequenceChoice.add("Draws");
            sequenceChoice.add("Defeats");
            sequenceChoice.add("Matches Unbeaten");
            sequenceChoice.add("Matches Without A Win");
            sequenceChoice.add("Cleansheets");
            sequenceChoice.add("Matches Scored In");
            sequenceChoice.add("Matches Without Scoring");
            innerPanel.add(sequenceChoice);
            
            Panel checkboxPanel = new Panel(new GridLayout(1, 2));
            CheckboxGroup optionGroup = new CheckboxGroup();
            Checkbox currentCheckbox = new Checkbox("Current", optionGroup, true);
            seasonCheckbox = new Checkbox("Season", optionGroup, false);
            checkboxPanel.add(currentCheckbox);
            checkboxPanel.add(seasonCheckbox);
            innerPanel.add(checkboxPanel);
            
            innerPanel.add(new Label("Matches:"));
            matchesChoice.add("Home & Away");
            matchesChoice.add("Home Only");
            matchesChoice.add("Away Only");
            innerPanel.add(matchesChoice);            

            ItemListener controlListener = new ItemListener()
            {
                public void itemStateChanged(ItemEvent ev)
                {
                    updateView();
                }
            };
            sequenceChoice.addItemListener(controlListener);
            currentCheckbox.addItemListener(controlListener);
            seasonCheckbox.addItemListener(controlListener);
            matchesChoice.addItemListener(controlListener);
            
            sequenceChoice.setForeground(Color.black);
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
            Panel tablePanel = new Panel(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.anchor = GridBagConstraints.NORTH;
            tablePanel.add(positionsColumn, constraints);
            constraints.gridwidth = GridBagConstraints.RELATIVE;
            tablePanel.add(teamsColumn, constraints);
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            tablePanel.add(sequenceColumn, constraints);
            titleLabel.setFont(theme.getTitleFont());
            titleLabel.setAlignment(Label.CENTER);            
            view.add(titleLabel, BorderLayout.NORTH);
            view.add(Util.wrapTable(tablePanel), BorderLayout.CENTER);
            updateView();
        }
        return view;
    }
    
    
    private void updateView()
    {
        if (data != null)
        {
            int when = (seasonCheckbox != null && seasonCheckbox.getState()) ? Team.SEASON : Team.CURRENT;
            int selectedIndex = matchesChoice.getSelectedIndex();
            int where = (selectedIndex <= 0 ? TeamRecord.BOTH : (selectedIndex == 1 ? TeamRecord.HOME : TeamRecord.AWAY));
            int sequence = Math.max(0, sequenceChoice.getSelectedIndex()); // A cheat really, we know that the indexes correspond to the constant values.
            SortedSet<StandardRecord> sequenceTable = data.getSequenceTable(when, where, sequence);
            
            titleLabel.setText(getTitleText(when, where, sequence));
            
            if (labels == null)
            {
                positionsColumn.removeAll();
                teamsColumn.removeAll();
                sequenceColumn.removeAll();
                
                labels = new Label[sequenceTable.size()][3];
                for (int i = 0; i < labels.length; i++)
                {
                    labels[i][0] = new Label(String.valueOf(i + 1), Label.CENTER);
                    positionsColumn.add(labels[i][0]);
                    labels[i][1] = new Label();
                    teamsColumn.add(labels[i][1]);
                    labels[i][2] = new Label("", Label.CENTER);
                    labels[i][2].setFont(theme.getBoldFont());
                    sequenceColumn.add(labels[i][2]);
                }
            }

            int index = 0;
            for (StandardRecord team : sequenceTable)
            {
                labels[index][1].setText(team.getName());
                labels[index][1].setFont(team.getName().equals(highlightedTeam) ? theme.getBoldFont() : theme.getPlainFont());
                labels[index][2].setText(String.valueOf(team.getSequence(when, sequence)));
                ++index;
            }
            
            view.validate();
        }
    }
    
    
    private String getTitleText(int when, int where, int sequence)
    {
        StringBuffer buffer = new StringBuffer(SEQUENCE_NAMES[sequence]);
        if (where == TeamRecord.HOME)
        {
            buffer.insert(0, "Home ");
        }
        else if (where == TeamRecord.AWAY)
        {
            buffer.insert(0, "Away ");
        }
        
        if (when == Team.CURRENT)
        {
            buffer.insert(0, "Current Consecutive ");
        }
        else
        {
            buffer.insert(0, "Most Consecutive ");
            // buffer.append(" This Season");
        }
        return buffer.toString();
    }
}