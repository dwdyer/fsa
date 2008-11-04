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

/**
 * Colour constants used by the applet.
 * @author Daniel Dyer
 */
final class Colours
{
    /** Green colour for rendering positive numbers. */
    public static final Color POSITIVE = new Color(0, 102, 0);
    /** Translucent green colour used to paint positive areas on graphs. */
    public static final Color POSITIVE_FILL = new Color(0, 255, 0, 128);
    /** Red colour for rendering negative numbers. */
    public static final Color NEGATIVE = new Color(153, 0, 0);
    /** Translucent red colour used to paint negative areas on graphs. */
    public static final Color NEGATIVE_FILL = new Color(255, 0, 0, 128);
    /** Colour used when the number is neither positive nor negative (i.e. it's zero). */
    public static final Color ZERO = Color.BLACK;

    public static final Color WIN = new Color(128, 255, 128);
    public static final Color DRAW = new Color(255, 255, 102);
    public static final Color DEFEAT = new Color(255, 153, 153);

    public static final Color[] PRIZES = new Color[]{new Color(255, 204, 0),
                                                     new Color(255, 255, 102),
                                                     new Color(255, 255, 187),
                                                     new Color(238, 238, 238)};

    public static final Color[] RELEGATION = new Color[]{new Color(255, 153, 153),
                                                         new Color(255, 204, 204)};
    
    public static final Color STARS = new Color(255, 153, 0);

    public static final Color NOTES = Color.BLUE;

    
    private Colours()
    {
        // Private constructor prevents instantiation.
    }


    public static Color getNumberColour(int value)
    {
        if (value == 0)
        {
            return ZERO;
        }
        else if (value > 0)
        {
            return POSITIVE;
        }
        else
        {
            return NEGATIVE;
        }
    }
}
