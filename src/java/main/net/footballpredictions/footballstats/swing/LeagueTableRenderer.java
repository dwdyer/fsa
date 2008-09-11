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

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import java.awt.Component;
import java.awt.Color;
import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * @author Daniel Dyer
 */
class LeagueTableRenderer extends DefaultTableCellRenderer
{
    private final LeagueSeason data;

    /**
     * @param data League data is used to determine row colours.
     */
    public LeagueTableRenderer(LeagueSeason data)
    {
        this.data = data;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        Component component = super.getTableCellRendererComponent(table,
                                                                  value,
                                                                  false, // Never render selection.
                                                                  false, // Never render focus.
                                                                  row,
                                                                  column);
        component.setBackground(getRowColour(row));
        return component;
    }


    private Color getRowColour(int row)
    {
        // TO DO:
        return null;
    }
}
