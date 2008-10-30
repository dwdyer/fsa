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
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Color;
import java.text.DecimalFormat;
import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * Default renderer for cells in a league table.
 * @author Daniel Dyer
 */
class LeagueTableRenderer extends DefaultTableCellRenderer
{
    private static final Color[] PRIZE_COLOURS = new Color[]{hexStringToColor("FFCC00"),
                                                             hexStringToColor("FFFF66"),
                                                             hexStringToColor("FFFFCC"),
                                                             hexStringToColor("EEEEEE")};

    private static final Color[] RELEGATION_COLOURS = new Color[]{hexStringToColor("FF9999"),
                                                                  hexStringToColor("FFCCCC")};

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");


    private final LeagueSeason data;
    private final boolean highlightZones;

    /**
     * @param data League data is used to determine row colours.
     * @param highlightZones Whether or not to render promotion and relegation zones
     * in different colours to other positions.
     */
    public LeagueTableRenderer(LeagueSeason data, boolean highlightZones)
    {
        this.data = data;
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
        if (highlightZones)
        {
            component.setBackground(getRowColour(row));
        }
        component.setHorizontalAlignment(value instanceof Number ? JLabel.RIGHT : JLabel.LEFT);

        return component;
    }


    private Color getRowColour(int row)
    {
        int zone = data.getZoneForPosition(row + 1); // Convert zero-based row index into one-based position.
        if (zone == 0)
        {
            return null;
        }
        else if (zone > 0)
        {
            return PRIZE_COLOURS[Math.min(zone, PRIZE_COLOURS.length) - 1];
        }
        else
        {
            return RELEGATION_COLOURS[Math.min(-zone, RELEGATION_COLOURS.length) - 1];
        }
    }


    protected static Color hexStringToColor(String hex)
    {
        int value = Integer.parseInt(hex, 16);
        return new Color(value);
    }
}
