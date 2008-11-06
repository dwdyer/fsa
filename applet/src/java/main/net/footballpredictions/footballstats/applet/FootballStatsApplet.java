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
package net.footballpredictions.footballstats.applet;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import net.footballpredictions.footballstats.swing.DataSelector;
import net.footballpredictions.footballstats.swing.LeagueTablePanel;
import net.footballpredictions.footballstats.swing.ResultsPanel;
import net.footballpredictions.footballstats.swing.SequencesPanel;
import net.footballpredictions.footballstats.swing.HeadToHeadPanel;
import net.footballpredictions.footballstats.swing.GraphsPanel;
import net.footballpredictions.footballstats.swing.Colours;

/**
 * This class provides football stats for a web page as a Swing applet.
 * @author Daniel Dyer
 */
public final class FootballStatsApplet extends JApplet
{
    // Attempt to set a native look-and-feel.
    static
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
            // Won't happen, we're loading a known look-and-feel.
            ex.printStackTrace();
        }
    }
    
    private static final String NAME_STRING = "Football Statistics Applet";
    private static final String VERSION_STRING = "Version 3.0 (Preview)";
    private static final String COPYRIGHT_STRING = "\u00A9 Copyright 2000-2008, Daniel W. Dyer";
    private static final String URL_STRING = "http://fsa.footballpredictions.net";

    private final DataSelector dataSelector = new DataSelector();

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
            add(createTopBar(), BorderLayout.NORTH);

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

            HeadToHeadPanel headToHead = new HeadToHeadPanel(messageResources);
            dataSelector.addDataListener(headToHead);
            tabs.add(messageResources.getString("headToHead.tab"), headToHead);

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


    private JComponent createTopBar()
    {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(dataSelector, BorderLayout.CENTER);
        // Using JLabel's hacky HTML support to allow for multi-line label.
        JLabel infoLabel = new JLabel("<html><p align='right'>" + NAME_STRING + "<br>" + VERSION_STRING + "</p></html>");
        infoLabel.setVerticalAlignment(JLabel.TOP);
        infoLabel.setFont(new Font("Dialog", Font.ITALIC, 10));
        infoLabel.setForeground(Colours.NOTES);
        infoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        infoLabel.setToolTipText(URL_STRING);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        infoLabel.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent mouseEvent)
            {
                if (mouseEvent.getButton() == MouseEvent.BUTTON1)
                {
                    try
                    {
                        getAppletContext().showDocument(new URL(URL_STRING), "_top");
                    }
                    catch (MalformedURLException ex)
                    {
                        // Won't happen.
                        ex.printStackTrace();
                    }
                }
            }
        });
        topBar.add(infoLabel, BorderLayout.EAST);
        return topBar;
    }


    /**
     * @return Information about this applet.
     */
    public String getAppletInfo()
    {
        return NAME_STRING + " - " + VERSION_STRING + "\n" + COPYRIGHT_STRING + "\n" + URL_STRING;
    }
}