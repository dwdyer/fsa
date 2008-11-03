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

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import net.footballpredictions.footballstats.model.LeagueMetaData;

/**
 * Default renderer for cells in a statistics table.  Never renders selection or
 * focus and deals with optional row highlighting for configured 'zones'.
 * @author Daniel Dyer
 */
class TableRenderer extends DefaultTableCellRenderer
{
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    private final LeagueMetaData metadata;
    private final boolean highlightZones;


    public TableRenderer()
    {
        this(null, false);
    }

    
    /**
     * @param metadata League metadata is used to determine row colours.
     * @param highlightZones Whether or not to render promotion and relegation zones
     * in different colours to other positions.
     */
    public TableRenderer(LeagueMetaData metadata, boolean highlightZones)
    {
        this.metadata = metadata;
        this.highlightZones = highlightZones;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        Object formattedValue = value instanceof Double ? DECIMAL_FORMAT.format(value) : value;
        JLabel component = (JLabel) super.getTableCellRendererComponent(table,
                                                                        formattedValue,
                                                                        false, // Never render selection.
                                                                        false, // Never render focus.
                                                                        row,
                                                                        column);
        component.setBackground(getRowColour(row));
        component.setHorizontalAlignment(value instanceof Number ? JLabel.RIGHT : JLabel.LEFT);

        return component;
    }


    /**
     * Determine the row background colour according to which 'zone' that row is in.
     * If zone highlighting is disabled, this method returns null for all positions.
     * @param row The row to colour.
     * @return A (possibly null) background colour.
     */
    private Color getRowColour(int row)
    {
        if (highlightZones && metadata != null)
        {
            int zone = metadata.getZoneForPosition(row + 1); // Convert zero-based row index into one-based position.
            if (zone > 0)
            {
                return Colours.PRIZES[Math.min(zone, Colours.PRIZES.length) - 1];
            }
            else if (zone < 0)
            {
                return Colours.RELEGATION[Math.min(-zone, Colours.RELEGATION.length) - 1];
            }
        }
        return null;
    }
}
