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

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import net.footballpredictions.footballstats.model.LeagueSeason;
import net.footballpredictions.footballstats.model.VenueType;
import java.awt.BorderLayout;

/**
 * @author Daniel Dyer
 */
class LeagueTablePanel extends JPanel implements StatsPanel
{
    private final LeagueTableModel leagueTableModel = new LeagueTableModel();

    public LeagueTablePanel()
    {
        super(new BorderLayout());
        JTable table = new JTable(leagueTableModel);
        JScrollPane scroller = new JScrollPane(table);
        add(scroller, BorderLayout.CENTER);
    }

    public void setLeagueData(LeagueSeason data)
    {
        leagueTableModel.setTeams(data.getStandardLeagueTable(VenueType.BOTH));
    }
}
