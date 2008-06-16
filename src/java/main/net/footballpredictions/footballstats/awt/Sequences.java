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
import net.footballpredictions.footballstats.model.SequenceType;
import net.footballpredictions.footballstats.model.StandardRecord;
import net.footballpredictions.footballstats.model.VenueType;

/**
 * AWT panel for displaying sequence tables.
 * @author Daniel Dyer
 * @since 4/1/2004
 */
public class Sequences implements StatsPanel
{
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
            for (SequenceType sequence : SequenceType.values())
            {
                sequenceChoice.add(sequence.toString());
            }
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
            boolean current = seasonCheckbox == null || !seasonCheckbox.getState();
            int selectedIndex = matchesChoice.getSelectedIndex();
            VenueType where = (selectedIndex <= 0 ? VenueType.BOTH : (selectedIndex == 1 ? VenueType.HOME : VenueType.AWAY));
            SequenceType sequence = SequenceType.values()[Math.max(0, sequenceChoice.getSelectedIndex())];
            SortedSet<StandardRecord> sequenceTable = data.getSequenceTable(sequence, where, current);
            
            titleLabel.setText(getTitleText(current, where, sequence));
            
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
                labels[index][2].setText(String.valueOf(current ? team.getCurrentSequence(sequence)
                                                                : team.getBestSequence(sequence)));
                ++index;
            }
            
            view.validate();
        }
    }


    /**
     * @param current Use the current sequence if true, the season's best sequence if false.
     * @param where Home matches, away matches or both.
     * @param sequence The type of sequence.
     * @return Text that describes the sequence as defined by the above parameters.
     */
    private String getTitleText(boolean current, VenueType where, SequenceType sequence)
    {
        StringBuffer buffer = new StringBuffer(where.getDescription());
        buffer.append(sequence.toString());
        
        if (current)
        {
            buffer.insert(0, "Current Consecutive ");
        }
        else
        {
            buffer.insert(0, "Most Consecutive ");
        }
        return buffer.toString();
    }
}