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

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.footballpredictions.footballstats.model.LeagueMetaData;

/**
 * Customised {@link TableRenderer} that correctly formats goal difference values.
 * @author Daniel Dyer
 */
class GoalDifferenceRenderer extends TableRenderer
{
    /**
     * @param metadata League metadata is used to determine row colours.
     * @param highlightZones Whether or not to render promotion and relegation zones
     * in different colours to other positions.
     */
    public GoalDifferenceRenderer(LeagueMetaData metadata, boolean highlightZones)
    {
        super(metadata, highlightZones);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        JLabel component = (JLabel) super.getTableCellRendererComponent(table,
                                                                        value,
                                                                        isSelected,
                                                                        hasFocus,
                                                                        row,
                                                                        column);
        Integer goalDifference = (Integer) value;
        if (goalDifference == 0)
        {
            component.setForeground(Colours.ZERO);
        }
        else if (goalDifference > 0)
        {
            component.setForeground(Colours.POSITIVE);
            // Add explicit plus sign for positive goal differences.
            component.setText('+' + component.getText());
        }
        else
        {
            component.setForeground(Colours.NEGATIVE);
        }
        
        return component;
    }
}
