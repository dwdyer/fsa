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

import net.footballpredictions.footballstats.swing.LabelledComponentsPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

/**
 * GUI control for manipulating league metadata (points for win,
 * relegation positions, etc.)
 * @author Daniel Dyer
 */
class MetaDataPanel extends LabelledComponentsPanel
{
    public MetaDataPanel()
    {
        addLabelledComponent("Points for a win:", new JSpinner(new SpinnerNumberModel(3, 1, 10, 1)));
        addLabelledComponent("Points for a draw:", new JSpinner(new SpinnerNumberModel(1, 1, 10, 1)));
        addLabelledComponent("Include attendances:", new JCheckBox());
        setBorder(BorderFactory.createTitledBorder("League Metadata"));
    }
}
