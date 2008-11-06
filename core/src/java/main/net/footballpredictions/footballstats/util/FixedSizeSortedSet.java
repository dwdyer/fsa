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
package net.footballpredictions.footballstats.util;

import java.util.TreeSet;
import java.util.Comparator;

/**
 * A {@link java.util.SortedSet} with a size limit.  If adding an element to
 * the set would take it beyond the size limit, the element at the end is
 * discarded.
 * @author Daniel Dyer
 */
public class FixedSizeSortedSet<E> extends TreeSet<E>
{
    private final int maxSize;

    public FixedSizeSortedSet(int maxSize)
    {
        this.maxSize = maxSize;
    }


    public FixedSizeSortedSet(int maxSize, Comparator<? super E> comparator)
    {
        super(comparator);
        this.maxSize = maxSize;
    }


    @Override
    public boolean add(E e)
    {
        boolean result = true;
        if (size() < maxSize || compare(e, last()) < 0)
        {
            result = super.add(e);
        }
        // If the set exceeds the maximum size, remove the last element (as
        // determined by the comparator).
        if (size() > maxSize)
        {
            remove(last());
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    private int compare(E e1, E e2)
    {
        if (comparator() != null)
        {
            return comparator().compare(e1, e2);
        }
        else
        {
            return ((Comparable<E>) e1).compareTo(e2);
        }
    }


    /**
     * @return The maximum capacity of this set.
     */
    public int getMaxSize()
    {
        return maxSize;
    }
}
