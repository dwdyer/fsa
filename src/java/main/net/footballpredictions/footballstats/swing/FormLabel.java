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

import javax.swing.JLabel;

/**
 * Component that renders a team's current form as a number of stars between 1
 * (lowest) and 5 (highest).
 * @author Daniel Dyer
 */
class FormLabel extends JLabel
{
    private final char STAR = '\u2605';

    public FormLabel()
    {
        setHorizontalAlignment(RIGHT);
        setForeground(Colours.STARS);
        setForm(3, null);
    }

    public void setForm(int stars, String form)
    {
        StringBuilder starsString = new StringBuilder();
        for (int i = 0; i < stars; i++)
        {
            starsString.append(STAR);
        }
        setText(starsString.toString());
        setToolTipText(form);
    }
}
