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
import java.awt.Font;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTable;
import net.footballpredictions.footballstats.model.Result;

/**
 * Customised {@link TableRenderer} that formats match scores.
 * @author Daniel Dyer
 */
class ScoreRenderer extends TableRenderer
{
    private static final Font FIXED_WIDTH_FONT = new Font("Monospaced", Font.BOLD, 13);
    private final String team;

    public ScoreRenderer()
    {
        this(null);
    }


    /**
     * Render scores for a particular team (results are coloured depending on
     * whether the team won, drew or lost).
     * @param team The team that is involved in all results.
     */
    public ScoreRenderer(String team)
    {
        this.team = team;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        Result result = (Result) value;
        JLabel component = (JLabel) super.getTableCellRendererComponent(table,
                                                                        resultToString(result),
                                                                        isSelected,
                                                                        hasFocus,
                                                                        row,
                                                                        column);
        component.setFont(FIXED_WIDTH_FONT);
        component.setHorizontalAlignment(JLabel.CENTER);
        component.setBackground(getColour(result));
        return component;
    }


    private String resultToString(Result result)
    {
        StringBuilder buffer = new StringBuilder();
        if (result.getHomeGoals() < 10)
        {
            buffer.append(' ');
        }
        buffer.append(result.getHomeGoals());

        buffer.append(" - ");

        buffer.append(result.getAwayGoals());
        if (result.getAwayGoals() < 10)
        {
            buffer.append(' ');
        }

        return buffer.toString();
    }


    private Color getColour(Result result)
    {
        if (team == null)
        {
            return null;
        }
        else if (result.isWin(team))
        {
            return Colours.WIN;
        }
        else if (result.isDraw())
        {
            return Colours.DRAW;
        }
        else
        {
            return Colours.DEFEAT;
        }
    }
}
