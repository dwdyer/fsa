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
package net.footballpredictions.footballstats.editor;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Panel for managing which teams are included in a data file.
 * @author Daniel Dyer
 */
public class TeamsPanel extends JPanel
{
    private final TeamsListModel teamsListModel = new TeamsListModel();
    private final JList teamsList = new JList(teamsListModel);
    private final JButton deleteButton = new JButton("Delete");
    private final TitledBorder titledBorder = BorderFactory.createTitledBorder("Teams (0)");

    public TeamsPanel()
    {
        super(new BorderLayout());
        add(createTeamsList(), BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);
        setBorder(titledBorder);
    }


    private JComponent createTeamsList()
    {
        teamsList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent listSelectionEvent)
            {
                // Disable the delete button when there are no rows selected.
                deleteButton.setEnabled(teamsList.getSelectedIndex() >= 0);
            }
        });
        teamsListModel.addListDataListener(new ListDataListener()
        {
            public void intervalAdded(ListDataEvent listDataEvent)
            {
                contentsChanged(listDataEvent);
            }

            public void intervalRemoved(ListDataEvent listDataEvent)
            {
                contentsChanged(listDataEvent);
            }

            public void contentsChanged(ListDataEvent listDataEvent)
            {
                titledBorder.setTitle("Teams (" + teamsListModel.getSize() + ")");
                TeamsPanel.this.repaint();
            }
        });
        return new JScrollPane(teamsList);
    }


    private JComponent createButtons()
    {
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        JButton addButton = new JButton("Add...");
        addButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                String newTeam = JOptionPane.showInputDialog(TeamsPanel.this,
                                                             "Enter team name:",
                                                             "Add Team",
                                                             JOptionPane.QUESTION_MESSAGE);
                teamsListModel.addTeam(newTeam);
            }
        });
        buttonsPanel.add(addButton);
        deleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                int[] rows = teamsList.getSelectedIndices();
                teamsListModel.removeTeams(rows);
            }
        });
        deleteButton.setEnabled(false);
        buttonsPanel.add(deleteButton);
        return buttonsPanel;
    }


}
