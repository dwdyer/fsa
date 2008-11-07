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

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import net.footballpredictions.footballstats.swing.RatioLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;

/**
 * Top-level window for the FSA data editor application.
 * @author Daniel Dyer
 */
public class DataEditor extends JFrame
{
    private final OpenAction openAction = new OpenAction(DataEditor.this);

    public DataEditor()
    {
        super("FSA Data Editor");
        setLayout(new RatioLayout(0.25));

        JPanel sidePanel = new JPanel(new GridLayout(2, 1));
        sidePanel.add(new MetaDataPanel());
        TeamsPanel teamsPanel = new TeamsPanel();
        openAction.addDataListener(teamsPanel);
        sidePanel.add(teamsPanel);

        add(sidePanel);

        ResultsPanel resultsPanel = new ResultsPanel();
        openAction.addDataListener(resultsPanel);
        add(resultsPanel);
        
        setJMenuBar(createMenuBar());
    }


    public static void main(String[] args)
    {
        DataEditor editor = new DataEditor();
        editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        editor.setSize(800, 600);
        editor.validate();
        editor.setVisible(true);
    }


    private JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.add(new JMenuItem(openAction));
        fileMenu.addSeparator();
        fileMenu.add(new JMenuItem(new ExitAction()));

        return menuBar;
    }
}
