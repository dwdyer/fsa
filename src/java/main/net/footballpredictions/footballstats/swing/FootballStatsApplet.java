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

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JApplet;
import javax.swing.JTabbedPane;

/**
 * This class provides football stats for a web page as a Swing applet.
 * @author Daniel Dyer
 */
public final class FootballStatsApplet extends JApplet
{
    private static final String VERSION_STRING = "Version 3.0 Alpha";
    private static final String COPYRIGHT_STRING = "© Copyright 2000-2008, Daniel W. Dyer";
    private static final String URL_STRING = "http://fsa.footballpredictions.net";

    public FootballStatsApplet()
    {
        System.out.println(getAppletInfo());
        setLayout(new BorderLayout());
    }


    public void init()
    {
        System.out.println("Initialising applet...");

        try
        {
            URL configURL = new URL(getDocumentBase(), getParameter("config.url"));
            DataSelector dataSelector = new DataSelector();
            add(dataSelector, BorderLayout.NORTH);

            // Use the default JVM locale unless it has been over-ridden in the properties file.
            String localeString = getParameter("locale");
            Locale locale = localeString != null ? new Locale(localeString) : Locale.getDefault();

            ResourceBundle messageResources = ResourceBundle.getBundle("net.footballpredictions.footballstats.messages.fsa",
                                                                       locale);

            JTabbedPane tabs = new JTabbedPane();

            LeagueTablePanel leagueTable = new LeagueTablePanel(false, messageResources);
            dataSelector.addDataListener(leagueTable);
            tabs.add(messageResources.getString("leagueTable.tab"), leagueTable);

            ResultsPanel results = new ResultsPanel(messageResources);
            dataSelector.addDataListener(results);
            tabs.add(messageResources.getString("results.tab"), results);

            LeagueTablePanel formTable = new LeagueTablePanel(true, messageResources);
            dataSelector.addDataListener(formTable);
            tabs.add(messageResources.getString("formTable.tab"), formTable);

            SequencesPanel sequences = new SequencesPanel(messageResources);
            dataSelector.addDataListener(sequences);
            tabs.add(messageResources.getString("sequences.tab"), sequences);

            GraphsPanel graphs = new GraphsPanel(messageResources);
            dataSelector.addDataListener(graphs);
            tabs.add(messageResources.getString("graphs.tab"), graphs);

            add(tabs, BorderLayout.CENTER);
            
            dataSelector.loadConfig(configURL.openStream(), getDocumentBase());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }


    /**
     * @return Information about this applet.
     */
    public String getAppletInfo()
    {
        return "Football Statistics Applet - " + VERSION_STRING + "\n" + COPYRIGHT_STRING + "\n" + URL_STRING;
    }
}