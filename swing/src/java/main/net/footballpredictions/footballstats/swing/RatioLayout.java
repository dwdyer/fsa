// ============================================================================
//   The Football Statistics Applet (http://fsa.footballpredictions.net)
//   © Copyright 2000-2010 Daniel W. Dyer
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

import java.awt.LayoutManager;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Container;

/**
 * Simple {@link LayoutManager} that arranges two components side-by-side with
 * the relative sizes determined by a pre-configured ratio.
 * @author Daniel Dyer
 */
public class RatioLayout implements LayoutManager
{
    private final double ratio;
    private final boolean horizontal;


    /**
     * Creates a horizontal layout using the specified ratio to control the widths
     * of the components.
     * @param ratio The size of the first component as a fraction of the
     * total container size.  The size of the second component, as a fraction,
     * of the total container size is then {@code 1 - ratio}.
     */
    public RatioLayout(double ratio)
    {
        this(ratio, true);
    }

    /**
     * @param ratio The size of the first component as a fraction of the
     * total container size.  The size of the second component, as a fraction,
     * of the total container size is then {@code 1 - ratio}.
     * @param horizontal If true, the two components are arranged side-by-side,
     * otherwise they are arranged one above the other.
     */
    public RatioLayout(double ratio, boolean horizontal)
    {
        if (ratio <= 0 || ratio >= 1)
        {
            throw new IllegalArgumentException("Ratio must be greater than zero and less than one.");
        }
        this.ratio = ratio;
        this.horizontal = horizontal;
    }


    /**
     * {@inheritDoc}
     */
    public void addLayoutComponent(String string, Component component)
    {
        // Do nothing.
    }


    /**
     * {@inheritDoc}
     */
    public void removeLayoutComponent(Component component)
    {
        // Do nothing.
    }


    /**
     * The preferred size is the smallest size that respects both the configured ratio and the
     * preferred sizes of the individual components.
     */
    public Dimension preferredLayoutSize(Container container)
    {
        Component[] components = container.getComponents();
        return getContainerSize(components[0].getPreferredSize(), components[1].getPreferredSize());
    }


    /**
     * The minimum size is the smallest size that respects both the configured ratio and the
     * minimum sizes of the individual components.
     */
    public Dimension minimumLayoutSize(Container container)
    {
        Component[] components = container.getComponents();
        return getContainerSize(components[0].getMinimumSize(), components[1].getMinimumSize());
    }


    private Dimension getContainerSize(Dimension component1Size, Dimension component2Size)
    {
        if (horizontal)
        {
            int height = Math.max(component1Size.height, component2Size.height);
            int width = getContainerDimension(component1Size.width, component2Size.width);
            return new Dimension(width, height);
        }
        else
        {
            int width = Math.max(component1Size.width, component2Size.width);
            int height = getContainerDimension(component1Size.height, component2Size.height);
            return new Dimension(width, height);
        }
    }


    private int getContainerDimension(int component1Size, int component2Size)
    {
        double relativeSize = (1 / (1 - ratio)) - 1;
        int firstComponentSize = (int) Math.ceil(component2Size * relativeSize);
        if (firstComponentSize >= component1Size)
        {
            return firstComponentSize + component2Size;
        }
        else
        {
            relativeSize = (1 / ratio) - 1;
            int secondComponentSize = (int) Math.ceil(component1Size * relativeSize);
            return component1Size + secondComponentSize;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void layoutContainer(Container container)
    {
        Component[] components = container.getComponents();
        if (horizontal)
        {
            int width = container.getSize().width;
            int firstComponentWidth = (int) Math.ceil(width * ratio);
            int secondComponentWidth = width - firstComponentWidth;
            components[0].setBounds(0, 0, firstComponentWidth, container.getSize().height);
            components[1].setBounds(firstComponentWidth, 0, secondComponentWidth, container.getSize().height);
        }
        else
        {
            int height = container.getSize().height;
            int firstComponentHeight = (int) Math.ceil(height * ratio);
            int secondComponentHeight = height - firstComponentHeight;
            components[0].setBounds(0, 0, container.getSize().width, firstComponentHeight);
            components[1].setBounds(0, firstComponentHeight, container.getSize().width, secondComponentHeight);            
        }
    }
}
