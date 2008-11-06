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

import java.awt.BorderLayout;
import java.util.Collections;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import net.footballpredictions.footballstats.model.Result;

/**
 * @author Daniel Dyer
 */
class ResultsPanel extends JPanel
{
    public ResultsPanel()
    {
        super(new BorderLayout());
        add(createTable(), BorderLayout.CENTER);
        setBorder(BorderFactory.createTitledBorder("Results"));
    }


    private JComponent createTable()
    {
        JTable resultsTable = new JTable(new ResultsTableModel(Collections.<Result>emptyList()));
        return new JScrollPane(resultsTable);
    }
}
