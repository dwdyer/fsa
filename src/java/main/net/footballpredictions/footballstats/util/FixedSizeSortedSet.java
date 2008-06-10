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
        // Only store the 20 highest/lowest attendances.
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


    public int getMaxSize()
    {
        return maxSize;
    }


    @SuppressWarnings("unchecked")
    @Override
    public FixedSizeSortedSet<E> clone()
    {
        return (FixedSizeSortedSet<E>) super.clone();
    }
}
