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

import net.footballpredictions.footballstats.model.LeagueSeason;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JLabel;

/**
 * Customised {@link LeagueTableRenderer} that formats position labels. 
 * @author Daniel Dyer
 */
public class PositionRenderer extends LeagueTableRenderer
{
    /**
     * @param data League data is used to determine row colours.
     * @param highlightZones Whether or not to render promotion and relegation zones
     * in different colours to other positions.
     */
    public PositionRenderer(LeagueSeason data, boolean highlightZones)
    {
        super(data, highlightZones);
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
        component.setText(component.getText() + ". ");
        return component;
    }
}
