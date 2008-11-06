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
package net.footballpredictions.footballstats.editor;

import javax.swing.AbstractListModel;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;

/**
 * List model for team names, used by {@link TeamsPanel}.  Maintains a list of team
 * names in alphabetical order and prevents duplicates.
 */
final class TeamsListModel extends AbstractListModel
{
    private final List<String> teams = new ArrayList<String>(24);

    public int getSize()
    {
        return teams.size();
    }


    public String getElementAt(int i)
    {
        return teams.get(i);
    }


    public void addTeam(String team)
    {
        if (team.length() == 0)
        {
            throw new IllegalArgumentException("Team name cannot be empty.");
        }
        // Insert teams in alphabetical order.
        int index = Collections.binarySearch(teams, team);
        if (index >= 0) // Don't add duplicates.
        {
            throw new IllegalArgumentException("Team name already exists in list.");
        }
        else
        {
            index = -index - 1;
            teams.add(index, team);
            fireIntervalAdded(this, index, index);
        }
    }


    /**
     * @param rowIndices The indices of the rows to be removed (in ascending order).
     */
    public void removeTeams(int[] rowIndices)
    {
        // Iterate over the array backwards so that early removals do not change
        // the indices of rows removed later.
        for (int i = rowIndices.length - 1; i >= 0; i--)
        {
            teams.remove(rowIndices[i]);
            fireIntervalRemoved(this, rowIndices[i], rowIndices[i]);
        }
    }


    /**
     * Replace the existing model contents with a new set of team names.
     * @param newTeams The names of the new teams.
     */
    public void setTeams(Collection<String> newTeams)
    {
        int oldSize = getSize();
        if (oldSize > 0)
        {
            teams.clear();
            fireIntervalRemoved(this, 0, oldSize - 1);
        }
        if (!newTeams.isEmpty())
        {
            teams.addAll(newTeams);
            Collections.sort(teams);
            fireIntervalAdded(this, 0, getSize() - 1);
        }
    }
}
