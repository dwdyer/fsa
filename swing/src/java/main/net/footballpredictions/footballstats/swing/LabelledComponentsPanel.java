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
package net.footballpredictions.footballstats.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * A Swing component that arranges components and associated labels in two columns.
 * All of the labelled components are placed in the {@link BorderLayout#NORTH NORTH}
 * section of a {@link BorderLayout}.  The other areas of the BorderLayout remain
 * unoccupied and can be added to. 
 * @author Daniel Dyer
 */
public class LabelledComponentsPanel extends JPanel
{
    private static final GridBagConstraints LABEL_CONSTRAINTS = new GridBagConstraints(GridBagConstraints.RELATIVE,
                                                                                       GridBagConstraints.RELATIVE,
                                                                                       GridBagConstraints.RELATIVE,
                                                                                       1,
                                                                                       1,
                                                                                       0,
                                                                                       GridBagConstraints.WEST,
                                                                                       GridBagConstraints.HORIZONTAL,
                                                                                       new Insets(0, 0, 0, 0),
                                                                                       0,
                                                                                       0);

    private static final GridBagConstraints VALUE_CONSTRAINTS = new GridBagConstraints(GridBagConstraints.RELATIVE,
                                                                                       GridBagConstraints.RELATIVE,
                                                                                       GridBagConstraints.REMAINDER,
                                                                                       1,
                                                                                       0,
                                                                                       0,
                                                                                       GridBagConstraints.EAST,
                                                                                       GridBagConstraints.HORIZONTAL,
                                                                                       new Insets(0, 0, 0, 0),
                                                                                       0,
                                                                                       0);

    private final JPanel contentPanel = new JPanel(new GridBagLayout());

    public LabelledComponentsPanel()
    {
        super(new BorderLayout());
        add(contentPanel, BorderLayout.NORTH);
    }


    /**
     * Add another row to the form.
     * @param labelText The text of this component's label.
     * @param component The component to display in the righthand column.
     */
    public void addLabelledComponent(String labelText, JComponent component)
    {
        contentPanel.add(new JLabel(labelText), LABEL_CONSTRAINTS);
        contentPanel.add(component, VALUE_CONSTRAINTS);
    }
}
