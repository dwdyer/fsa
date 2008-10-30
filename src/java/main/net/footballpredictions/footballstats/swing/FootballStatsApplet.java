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
import java.awt.FlowLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import net.footballpredictions.footballstats.model.LeagueSeason;

/**
 * This class provides football stats for a web page as a Swing applet.
 * @author Daniel Dyer
 */
public final class FootballStatsApplet extends JApplet
{
    private static final String VERSION_STRING = "Version 3.0 Alpha";
    private static final String COPYRIGHT_STRING = "© Copyright 2000-2008, Daniel W. Dyer";
    private static final String URL_STRING = "http://fsa.footballpredictions.net";

    // Mapping from display name to file name for results files.
    private final Map<String, URL> dataFiles = new LinkedHashMap<String, URL>(10);
    // Mapping from display name to highlighted team.
    private final Map<String, String> highlightedTeams = new HashMap<String, String>(10);

    private final List<StatsPanel> panels = new LinkedList<StatsPanel>();
    private ResourceBundle res;

    public FootballStatsApplet()
    {
        System.out.println(getAppletInfo());
        setLayout(new BorderLayout());
    }


    public void init()
    {
        System.out.println("Initialising applet...");
        loadConfiguration();
        
        String curLocaleString = getParameter("locale");
        if (curLocaleString == null ){
        	System.err.println("Param locale is null");
        	curLocaleString = "en";
        }
        System.out.println("Param locale=" + curLocaleString);
        
        Locale locale = new Locale(curLocaleString);
        res = ResourceBundle.getBundle("net.footballpredictions.footballstats.messages.fsa", locale);
        
        JTabbedPane tabs = new JTabbedPane();
        LeagueTablePanel leagueTable = new LeagueTablePanel(false);
        panels.add(leagueTable);
        tabs.add("League Table", leagueTable);
        LeagueTablePanel formTable = new LeagueTablePanel(true);
        panels.add(formTable);
        tabs.add("Form", formTable);

        add(createSeasonSelector(), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        // Load the initial data.
        updateLeagueData(dataFiles.values().iterator().next());
    }


    private JComponent createSeasonSelector()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("League Data: "));
        JComboBox seasonCombo = new JComboBox();
        for (String name : dataFiles.keySet())
        {
            seasonCombo.addItem(name);
        }
        seasonCombo.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent itemEvent)
            {
                if (itemEvent.getStateChange() == ItemEvent.SELECTED)
                {
                    final URL dataURL = dataFiles.get(itemEvent.getItem());
                    updateLeagueData(dataURL);
                }
            }
        });
        panel.add(seasonCombo);
        return panel;
    }


    /**
     * Load a new data file and update all stats panels.
     * @param dataURL The URL of the results to load.
     */
    private void updateLeagueData(final URL dataURL)
    {
        new SwingBackgroundTask<LeagueSeason>()
        {
            protected LeagueSeason performTask() throws Exception
            {
                return new LeagueSeason(dataURL,res);
            }

            @Override
            protected void postProcessing(LeagueSeason result)
            {
                for (StatsPanel panel : panels)
                {
                    panel.setLeagueData(result);
                }
            }
        }.execute();
    }


    private void loadConfiguration()
    {
        System.out.println("Processing configuration options...");
        Properties properties = new Properties();
        try
        {
            URL configURL = new URL(getDocumentBase(), getParameter("config.url"));
            System.out.println("Loading configuration from " + configURL.toString());
            properties.load(configURL.openStream());
        }
        catch (IOException ex)
        {
            System.out.println("ERROR: Failed to load configuration file.");
            return;
        }

        String displayName;
        int count = 0;
        do
        {
            count++;
            displayName = properties.getProperty("datafile" + count + ".displayname");
            String fileURL = properties.getProperty("datafile" + count + ".url");
            String highlightedTeam = properties.getProperty("datafile" + count + ".highlightteam");
            if (displayName != null && fileURL != null)
            {
                try
                {
                    URL old = dataFiles.put(displayName, new URL(getDocumentBase(), fileURL));
                    if (old != null)
                    {
                        System.out.println("WARNING: Duplicate data file entry for " + displayName);
                    }
                    if (highlightedTeam != null)
                    {
                        highlightedTeams.put(displayName, highlightedTeam);
                    }
                }
                catch (IOException ex)
                {
                    System.out.println("WARNING: Unable to open data file " + fileURL + " - " + ex.getMessage());
                }
            }
        } while (displayName != null);
    }



    /**
     * @return Information about this applet.
     */
    public String getAppletInfo()
    {
        return "Football Statistics Applet - " + VERSION_STRING + "\n" + COPYRIGHT_STRING + "\n" + URL_STRING;
    }
}