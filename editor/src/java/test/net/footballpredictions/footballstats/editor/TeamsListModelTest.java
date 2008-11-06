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

import org.testng.annotations.Test;

/**
 * Unit test for {@link TeamsListModel}.
 * @author Daniel Dyer
 */
public class TeamsListModelTest
{
    @Test
    public void testAddRows()
    {
        TeamsListModel model = new TeamsListModel();
        assert model.getSize() == 0 : "Model should be empty initially.";
        model.addTeam("Arsenal");
        assert model.getSize() == 1 : "Model should have one row, not " + model.getSize();
        assert model.getElementAt(0).equals("Arsenal")
            : "First row should be Arsenal, not " + model.getElementAt(0);
        model.addTeam("Aston Villa");
        assert model.getSize() == 2 : "Model should have two rows, not " + model.getSize();
        assert model.getElementAt(0).equals("Arsenal")
            : "First row should be Arsenal, not " + model.getElementAt(0);
        assert model.getElementAt(1).equals("Aston Villa")
            : "Second row should be Aston Villa, not " + model.getElementAt(1);
    }


    @Test(dependsOnMethods = "testAddRows")
    public void testInsertRow()
    {
        TeamsListModel model = new TeamsListModel();
        model.addTeam("Arsenal");
        model.addTeam("Wigan Athletic");
        // Items should be inserted in alphabetical order.
        model.addTeam("Portsmouth");
        assert model.getSize() == 3 : "Model should have three rows, not " + model.getSize();
        assert model.getElementAt(0).equals("Arsenal")
            : "First row should be Arsenal, not " + model.getElementAt(0);
        assert model.getElementAt(1).equals("Portsmouth")
            : "Second row should be Portsmouth, not " + model.getElementAt(1);
        assert model.getElementAt(2).equals("Wigan Athletic")
            : "Third row should be Wigan Athletic, not " + model.getElementAt(2);
    }


    @Test(dependsOnMethods = "testAddRows")
    public void testAddDuplicate()
    {
        TeamsListModel model = new TeamsListModel();
        model.addTeam("Arsenal");
        // Sanity check.
        assert model.getSize() == 1 : "Model should have one row, not " + model.getSize();
        // Attempting to add the same element again should be ignored.
        model.addTeam("Arsenal");
        assert model.getSize() == 1 : "Model should have one row, not " + model.getSize();
    }


    @Test(dependsOnMethods = "testAddRows")
    public void testDeleteSingleRow()
    {
        TeamsListModel model = new TeamsListModel();
        model.addTeam("Barcelona");
        model.addTeam("Real Madrid");
        model.addTeam("Real Sociedad");
        model.addTeam("Valencia");
        // Sanity check.
        assert model.getSize() == 4 : "Model should have four rows, not " + model.getSize();

        model.removeTeams(new int[]{2}); // Remove third row, "Real Sociedad".
        assert model.getSize() == 3 : "Model should have three rows, not " + model.getSize();
        // Check that correct teams remain.
        assert model.getElementAt(0).equals("Barcelona")
            : "First row should be Barcelona, not " + model.getElementAt(0);
        assert model.getElementAt(1).equals("Real Madrid")
            : "Second row should be Real Madrid, not " + model.getElementAt(1);
        assert model.getElementAt(2).equals("Valencia")
            : "Third row should be Valencia, not " + model.getElementAt(2);
    }


    @Test(dependsOnMethods = "testDeleteSingleRow")
    public void testDeleteContiguousRows()
    {
        TeamsListModel model = new TeamsListModel();
        model.addTeam("Barcelona");
        model.addTeam("Real Madrid");
        model.addTeam("Real Sociedad");
        model.addTeam("Valencia");
        // Sanity check.
        assert model.getSize() == 4 : "Model should have four rows, not " + model.getSize();

        model.removeTeams(new int[]{1, 2}); // Remove second and third rows, "Real Madrid" and "Real Sociedad".
        assert model.getSize() == 2 : "Model should have two rows, not " + model.getSize();
        // Check that correct teams remain.
        assert model.getElementAt(0).equals("Barcelona")
            : "First row should be Barcelona, not " + model.getElementAt(0);
        assert model.getElementAt(1).equals("Valencia")
            : "Second row should be Valencia, not " + model.getElementAt(1);
    }


    @Test(dependsOnMethods = "testDeleteSingleRow")
    public void testDeleteNonContiguousRows()
    {
        TeamsListModel model = new TeamsListModel();
        model.addTeam("Barcelona");
        model.addTeam("Real Madrid");
        model.addTeam("Real Sociedad");
        model.addTeam("Valencia");
        // Sanity check.
        assert model.getSize() == 4 : "Model should have four rows, not " + model.getSize();

        model.removeTeams(new int[]{0, 2}); // Remove first and third rows, "Barcelona" and "Real Sociedad".
        assert model.getSize() == 2 : "Model should have two rows, not " + model.getSize();
        // Check that correct teams remain.
        assert model.getElementAt(0).equals("Real Madrid")
            : "First row should be Real Madrid, not " + model.getElementAt(0);
        assert model.getElementAt(1).equals("Valencia")
            : "Second row should be Valencia, not " + model.getElementAt(1);
    }
}
