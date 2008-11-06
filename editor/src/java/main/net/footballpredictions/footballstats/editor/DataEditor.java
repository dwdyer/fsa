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
import java.awt.BorderLayout;

/**
 * Top-level window for the FSA data editor application.
 * @author Daniel Dyer
 */
public class DataEditor extends JFrame
{
    public DataEditor()
    {
        super("FSA Data Editor");
        add(new TeamsPanel(), BorderLayout.WEST);
    }


    public static void main(String[] args)
    {
        DataEditor editor = new DataEditor();
        editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        editor.setSize(800, 600);
        editor.validate();
        editor.setVisible(true);
    }
}
