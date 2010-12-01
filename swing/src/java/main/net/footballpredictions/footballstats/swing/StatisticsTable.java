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

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import java.util.ResourceBundle;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import net.footballpredictions.footballstats.model.Result;

/**
 * Customised JTable that is pre-configured with the appropriate
 * defaults for displaying football statistics.
 * @author Daniel Dyer
 */
class StatisticsTable extends JTable
{
    private static final TableRenderer DEFAULT_RENDERER = new TableRenderer();
    private static final ScoreRenderer SCORE_RENDERER = new ScoreRenderer();

    public StatisticsTable(ResourceBundle messageResources)
    {
        setIntercellSpacing(new Dimension(0, 1));
        setShowGrid(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(false);

        setDefaultRenderer(Object.class, DEFAULT_RENDERER);
        setDefaultRenderer(String.class, DEFAULT_RENDERER);
        setDefaultRenderer(Number.class, DEFAULT_RENDERER);
        setDefaultRenderer(Double.class, DEFAULT_RENDERER);
        DateFormat dateFormat = new SimpleDateFormat("EEE d MMM yyyy", messageResources.getLocale());
        setDefaultRenderer(Date.class, new DateRenderer(dateFormat));
        setDefaultRenderer(Result.class, SCORE_RENDERER);
    }
}
